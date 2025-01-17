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

const val UNINSTALL_API_COMPLETE_ACTION = "uninstall_api_complete"
internal const val UNINSTALL_TIMEOUT = 300_000L

interface UninstallEvents {
  val events: Flow<UninstallResult>
}

@Singleton
class UninstallEventsImpl @Inject constructor(
  @ApplicationContext context: Context,
  private val userActionLauncher: UserActionLauncher,
) : BroadcastReceiver(), UninstallEvents {

  init {
    // Register itself to listen for the events
    ContextCompat.registerReceiver(
      context,
      this,
      IntentFilter(UNINSTALL_API_COMPLETE_ACTION),
      ContextCompat.RECEIVER_NOT_EXPORTED
    )
  }

  private val _events = MutableSharedFlow<UninstallResult>(replay = 1, extraBufferCapacity = 10)

  override val events: Flow<UninstallResult> = _events

  override fun onReceive(
    context: Context,
    intent: Intent,
  ): Unit = goAsync(Dispatchers.IO) {
    intent.extras
      ?.takeIf { UNINSTALL_API_COMPLETE_ACTION == intent.action }
      ?.toEvent(context)
      ?.also { _events.emit(it) }
  }

  private suspend fun Bundle.toEvent(context: Context): UninstallResult? {
    val message = getString(PackageInstaller.EXTRA_STATUS_MESSAGE, "No message")
    val id = getInt("${context.packageName}.uninstall_id")
    return when (getInt(PackageInstaller.EXTRA_STATUS, -1)) {
      PackageInstaller.STATUS_PENDING_USER_ACTION -> intent
        .toUninstallResult(id, message)

      PackageInstaller.STATUS_SUCCESS -> UninstallResult.Success(id)
      PackageInstaller.STATUS_FAILURE_ABORTED -> UninstallResult.Abort(id, message)
      PackageInstaller.STATUS_FAILURE,
      PackageInstaller.STATUS_FAILURE_BLOCKED,
      PackageInstaller.STATUS_FAILURE_CONFLICT,
      PackageInstaller.STATUS_FAILURE_INCOMPATIBLE,
      PackageInstaller.STATUS_FAILURE_INVALID,
      PackageInstaller.STATUS_FAILURE_STORAGE,
        -> UninstallResult.Fail(id, message)

      else -> UninstallResult.Fail(id, message)
    }
  }

  private val Bundle.intent
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      getParcelable(Intent.EXTRA_INTENT, Intent::class.java)
    } else {
      @Suppress("DEPRECATION")
      getParcelable(Intent.EXTRA_INTENT) as Intent?
    }

  private suspend fun Intent?.toUninstallResult(
    id: Int,
    message: String,
  ): UninstallResult? = if (this == null) {
    UninstallResult.Fail(id, message)
  } else {
    try {
      // Schedule the timeout for the intent launch result
      withTimeout(UNINSTALL_TIMEOUT) {
        userActionLauncher.launchIntent(this@toUninstallResult)
      }
      null
    } catch (t: TimeoutCancellationException) {
      UninstallResult.Fail(id, t.message ?: "Unknown reason")
    }
  }
}
