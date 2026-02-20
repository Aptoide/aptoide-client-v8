package cm.aptoide.pt.installer.platform

import android.content.Intent
import cm.aptoide.pt.extensions.SuspendValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

enum class UserConfirmation {
  INSTALL_SOURCE,
  WRITE_EXTERNAL_RATIONALE,
  WRITE_EXTERNAL,
}

sealed class UserActionRequest {
  data class InstallationAction(val intent: Intent) : UserActionRequest()
  data class ConfirmationAction(val confirmation: UserConfirmation) : UserActionRequest()
  data class PermissionState(val permission: String) : UserActionRequest()
  data class PermissionAction(val permission: String) : UserActionRequest()
}

/**
 * Launcher to request user actions required to finish installation.
 */
interface UserActionLauncher {
  suspend fun launchIntent(intent: Intent): Boolean
  suspend fun confirm(confirmation: UserConfirmation): Boolean

  /**
   * True = granted
   * False = denied
   */
  suspend fun requestPermissions(permission: String): Boolean

  /**
   * True = permissions can be requested
   * Null = permissions can be requested, but should show a rationale
   * False = denied forever
   */
  suspend fun checkPermissionState(permission: String): Boolean?
}

/**
 * Handler for user actions required to finish installation.
 */
interface UserActionHandler {
  val requests: Flow<UserActionRequest?>

  fun onResult(allowed: Boolean?)
}

@Singleton
class UserActionHandlerImpl @Inject constructor() : UserActionLauncher, UserActionHandler {

  private val _requests =
    MutableSharedFlow<UserActionRequest?>(replay = 1, extraBufferCapacity = 10)

  override val requests: Flow<UserActionRequest?> = _requests

  // Lock to make sure only 1 action launched at a time
  private val mutex = Mutex()

  // Used to signal about user interaction result
  private var result: SuspendValue<Boolean?> = SuspendValue()

  override suspend fun launchIntent(intent: Intent): Boolean =
    requestUserAction(UserActionRequest.InstallationAction(intent)) ?: false

  override suspend fun confirm(confirmation: UserConfirmation): Boolean =
    requestUserAction(UserActionRequest.ConfirmationAction(confirmation)) ?: false

  override suspend fun checkPermissionState(permission: String): Boolean? =
    requestUserAction(UserActionRequest.PermissionState(permission))

  override suspend fun requestPermissions(permission: String): Boolean =
    requestUserAction(UserActionRequest.PermissionAction(permission)) ?: false

  private suspend fun requestUserAction(request: UserActionRequest): Boolean? = mutex.withLock {
    _requests.emit(request)
    result.await()
      .also { _requests.emit(null) } // Clean the request state
  }

  override fun onResult(allowed: Boolean?) {
    result.yield(allowed)
  }
}
