package cm.aptoide.pt.installer.platform

import android.content.Intent
import cm.aptoide.pt.extensions.SuspendLock
import cm.aptoide.pt.extensions.SuspendValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed class UserActionRequest {
  data class InstallationAction(val intent: Intent) : UserActionRequest()
}

/**
 * Launcher to request user actions required to finish installation.
 */
interface UserActionLauncher {
  suspend fun launchIntent(intent: Intent): Boolean
}

/**
 * Handler for user actions required to finish installation.
 */
interface UserActionHandler {
  val requests: Flow<UserActionRequest?>

  fun onResult(allowed: Boolean)
}

@Singleton
class UserActionHandlerImpl @Inject constructor() : UserActionLauncher, UserActionHandler {

  private val _requests =
    MutableSharedFlow<UserActionRequest?>(replay = 1, extraBufferCapacity = 10)

  override val requests: Flow<UserActionRequest?> = _requests

  // Lock to make sure only 1 action launched at a time
  private var lock: SuspendLock? = null

  // Used to signal about user interaction result
  private var result: SuspendValue<Boolean> = SuspendValue()

  override suspend fun launchIntent(intent: Intent): Boolean {
    lock?.await()
    return SuspendLock().let {
      lock = it
      try {
        _requests.emit(UserActionRequest.InstallationAction(intent))
        result.await()
          .also { _requests.emit(null) } // Clean the request state
      } finally {
        lock = null
        it.yield()
      }
    }
  }

  override fun onResult(allowed: Boolean) {
    result.yield(allowed)
  }
}
