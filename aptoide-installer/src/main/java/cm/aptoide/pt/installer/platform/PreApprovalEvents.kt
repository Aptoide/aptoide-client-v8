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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

const val PREAPPROVAL_SESSION_API_COMPLETE_ACTION = "preapproval_session_api_complete"
internal const val PREAPPROVAL_TIMEOUT = 300_000L

interface PreApprovalEvents {
  val events: Flow<PreApprovalResult>
}

@Singleton
class PreApprovalEventsImpl @Inject constructor(
  @ApplicationContext context: Context,
  private val userActionLauncher: UserActionLauncher,
) : BroadcastReceiver(), PreApprovalEvents {

  init {
    ContextCompat.registerReceiver(
      context,
      this,
      IntentFilter(PREAPPROVAL_SESSION_API_COMPLETE_ACTION),
      ContextCompat.RECEIVER_NOT_EXPORTED
    )
  }

  private val _events = MutableSharedFlow<PreApprovalResult>(replay = 1, extraBufferCapacity = 10)

  override val events: Flow<PreApprovalResult> = _events

  override fun onReceive(
    context: Context,
    intent: Intent,
  ): Unit = goAsync(Dispatchers.IO) {
    intent.extras
      ?.takeIf { PREAPPROVAL_SESSION_API_COMPLETE_ACTION == intent.action }
      ?.toEvent(context)
      ?.also { _events.emit(it) }
  }

  private suspend fun Bundle.toEvent(context: Context): PreApprovalResult? {
    val message = getString(PackageInstaller.EXTRA_STATUS_MESSAGE, "No message")
    val sessionId = getInt(PackageInstaller.EXTRA_SESSION_ID)
    return when (getInt(PackageInstaller.EXTRA_STATUS, -1)) {
      PackageInstaller.STATUS_PENDING_USER_ACTION -> intent
        ?.putExtra("${context.packageName}.pn", getString("${context.packageName}.pn"))
        ?.putExtra("${context.packageName}.ap", getString("${context.packageName}.ap"))
        .toPreApprovalResult(sessionId, message)

      PackageInstaller.STATUS_SUCCESS -> PreApprovalResult.Success(sessionId)
      PackageInstaller.STATUS_FAILURE_ABORTED -> PreApprovalResult.Abort(sessionId, message)
      PackageInstaller.STATUS_FAILURE_BLOCKED,
        -> PreApprovalResult.Blocked(sessionId, message)

      PackageInstaller.STATUS_FAILURE,
      PackageInstaller.STATUS_FAILURE_CONFLICT,
      PackageInstaller.STATUS_FAILURE_INCOMPATIBLE,
      PackageInstaller.STATUS_FAILURE_INVALID,
      PackageInstaller.STATUS_FAILURE_STORAGE,
        -> PreApprovalResult.Fail(sessionId, message)

      else -> PreApprovalResult.Fail(sessionId, message)
    }
  }

  private val Bundle.intent
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      getParcelable(Intent.EXTRA_INTENT, Intent::class.java)
    } else {
      @Suppress("DEPRECATION")
      getParcelable(Intent.EXTRA_INTENT) as Intent?
    }

  private suspend fun Intent?.toPreApprovalResult(
    sessionId: Int,
    message: String,
  ): PreApprovalResult? = if (this == null) {
    PreApprovalResult.Fail(sessionId, message)
  } else {
    try {
      withTimeout(PREAPPROVAL_TIMEOUT) {
        userActionLauncher.launchIntent(this@toPreApprovalResult)
      }
      null
    } catch (t: Throwable) {
      PreApprovalResult.Fail(sessionId, t.message ?: "Unknown reason")
    }
  }
}
