package cm.aptoide.pt.installer.platform

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInstaller
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.DefaultLifecycleObserver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

const val INSTALL_SESSION_API_COMPLETE_ACTION = "install_session_api_complete"
internal const val INSTALL_TIMEOUT = 300_000L

interface InstallEvents {
  val events: Flow<InstallResult>
}

@Singleton
class InstallEventsImpl @Inject constructor(
  @ApplicationContext context: Context,
) : BroadcastReceiver(), DefaultLifecycleObserver, InstallEvents {

  init {
    // Register itself to listen for the events
    context.registerReceiver(
      this,
      IntentFilter(INSTALL_SESSION_API_COMPLETE_ACTION)
    )
    // Listen to some sessions changes
    context.packageManager.packageInstaller.registerSessionCallback(
      object : PackageInstaller.SessionCallback() {
        override fun onCreated(sessionId: Int) {}
        override fun onBadgingChanged(sessionId: Int) {}
        override fun onActiveChanged(sessionId: Int, active: Boolean) {}
        override fun onProgressChanged(sessionId: Int, progress: Float) {
          // Needed for the case when user clicks the "Allow" button in the installation confirmation
          // dialog. To remove the confirmation dialog reappearance.
          userActions.remove(sessionId)
        }

        override fun onFinished(sessionId: Int, success: Boolean) {}
      }
    )
  }

  // User actions intents
  private var userActions = mutableMapOf<Int, () -> Unit>()

  // If waiting for user action
  private var busy = false

  var currentActivity: InstallActivity? = null

  fun onResult() {
    busy = false
    // Run the first action scheduled
    userActions.entries.firstOrNull()
      ?.let {
        busy = true
        it.value.invoke()
        val key = it.key
        // Schedule the timeout event
        Handler(Looper.getMainLooper()).postDelayed(
          {
            // If action is still not resolved send the timeout error event
            userActions.remove(key)?.let {
              InstallResult.Fail(key, "Install timeout").send()
            }
          },
          INSTALL_TIMEOUT
        )
      }
  }

  private val _events = MutableSharedFlow<InstallResult>(replay = 1, extraBufferCapacity = 10)

  override val events: Flow<InstallResult> = _events

  override fun onReceive(context: Context, intent: Intent): Unit = intent.extras
    ?.takeIf { INSTALL_SESSION_API_COMPLETE_ACTION == intent.action }
    ?.toEvent()
    ?.send()
    ?: Unit

  private fun InstallResult.send() {
    userActions.remove(sessionId)
    _events.tryEmit(this)
  }

  private fun Bundle.toEvent(): InstallResult? {
    val message = getString(PackageInstaller.EXTRA_STATUS_MESSAGE, "No message")
    val sessionId = getInt(PackageInstaller.EXTRA_SESSION_ID)
    return when (getInt(PackageInstaller.EXTRA_STATUS, -1)) {
      PackageInstaller.STATUS_PENDING_USER_ACTION -> intent.toInstallResult(sessionId, message)
      PackageInstaller.STATUS_SUCCESS -> InstallResult.Success(sessionId)
      PackageInstaller.STATUS_FAILURE_ABORTED -> InstallResult.Cancel(sessionId, message)
      PackageInstaller.STATUS_FAILURE,
      PackageInstaller.STATUS_FAILURE_BLOCKED,
      PackageInstaller.STATUS_FAILURE_CONFLICT,
      PackageInstaller.STATUS_FAILURE_INCOMPATIBLE,
      PackageInstaller.STATUS_FAILURE_INVALID,
      PackageInstaller.STATUS_FAILURE_STORAGE -> InstallResult.Fail(sessionId, message)

      else -> InstallResult.Fail(sessionId, message)
    }
  }

  private val Bundle.intent
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      getParcelable(Intent.EXTRA_INTENT, Intent::class.java)
    } else {
      @Suppress("DEPRECATION")
      getParcelable(Intent.EXTRA_INTENT) as Intent?
    }

  private fun Intent?.toInstallResult(sessionId: Int, message: String) = if (this == null) {
    InstallResult.Fail(sessionId, message)
  } else {
    // Scheduled a user action for the intent
    userActions[sessionId] = {
      currentActivity
        ?.launchUserAction(this)
        ?: InstallResult.Fail(sessionId, "No activity found").send()
    }
    // If not busy run the first scheduled user action
    if (!busy) {
      onResult()
    }
    null
  }
}
