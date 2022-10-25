package cm.aptoide.pt.install_manager

import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import kotlinx.coroutines.flow.Flow

/**
 * This represents an app install/uninstall task
 */

interface Task {
  val isFinished: Boolean

  /**
   * The [Flow] of the task state and progress as it changes. Immediately emits the
   * current ones for any new subscriber. Completes as soon as task is finished.
   */
  val stateAndProgress: Flow<Pair<State, Int>>

  /**
   * Cancel the task if possible.
   *
   * Tasks that are already completed/failed/cancelled cannot be canceled, so nothing will happen.
   */
  fun cancel()

  interface Factory {
    suspend fun getTask(packageName: String): Task?

    suspend fun createTask(
      packageName: String,
      type: Type,
      installPackageInfo: InstallPackageInfo,
      onTerminate: suspend (success: Boolean) -> Unit,
    ): Task
  }

  enum class Type {
    INSTALL,
    UNINSTALL
  }

  enum class State {
    /* Waiting to be started */
    PENDING,

    /* Downloading the files */
    DOWNLOADING,

    /* Files are ready for installation */
    READY_TO_INSTALL,

    /* Installing the downloaded files */
    INSTALLING,

    /* Uninstalling the app files */
    UNINSTALLING,

    /* Work is finished */
    COMPLETED,

    /* Work is canceled */
    CANCELED,

    /* Work is failed */
    FAILED,
  }
}