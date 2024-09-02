package cm.aptoide.pt.installer.platform

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInstaller
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import cm.aptoide.pt.extensions.goAsync
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withTimeout
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
  private val userActionLauncher: UserActionLauncher,
) : BroadcastReceiver(), InstallEvents {

  init {
    // Register itself to listen for the events
    ContextCompat.registerReceiver(
      context,
      this,
      IntentFilter(INSTALL_SESSION_API_COMPLETE_ACTION),
      ContextCompat.RECEIVER_NOT_EXPORTED
    )
  }

  private val _events = MutableSharedFlow<InstallResult>(replay = 1, extraBufferCapacity = 10)

  override val events: Flow<InstallResult> = _events

  override fun onReceive(
    context: Context,
    intent: Intent,
  ): Unit = goAsync(Dispatchers.IO) {
    intent.extras
      ?.takeIf { INSTALL_SESSION_API_COMPLETE_ACTION == intent.action }
      ?.toEvent()
      ?.also { _events.emit(it) }
  }

  private suspend fun Bundle.toEvent(): InstallResult? {
    val message = getString(PackageInstaller.EXTRA_STATUS_MESSAGE, "No message")
    val sessionId = getInt(PackageInstaller.EXTRA_SESSION_ID)
    return when (getInt(PackageInstaller.EXTRA_STATUS, -1)) {
      PackageInstaller.STATUS_PENDING_USER_ACTION -> intent.toInstallResult(sessionId, message)
      PackageInstaller.STATUS_SUCCESS -> InstallResult.Success(sessionId)
      PackageInstaller.STATUS_FAILURE_ABORTED -> InstallResult.Abort(sessionId, message)
      PackageInstaller.STATUS_FAILURE,
      PackageInstaller.STATUS_FAILURE_BLOCKED,
      PackageInstaller.STATUS_FAILURE_CONFLICT,
      PackageInstaller.STATUS_FAILURE_INCOMPATIBLE,
      PackageInstaller.STATUS_FAILURE_INVALID,
      PackageInstaller.STATUS_FAILURE_STORAGE,
      -> InstallResult.Fail(sessionId, message)

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

  private suspend fun Intent?.toInstallResult(
    sessionId: Int,
    message: String,
  ): InstallResult? = if (this == null) {
    InstallResult.Fail(sessionId, message)
  } else {
    try {
      // Schedule the timeout for the intent launch result
      withTimeout(INSTALL_TIMEOUT) {
        userActionLauncher.launchIntent(this@toInstallResult)
      }
      null
    } catch (t: TimeoutCancellationException) {
      InstallResult.Fail(sessionId, t.message ?: "Unknown reason")
    }
  }
}
