package cm.aptoide.pt.install_manager

import cm.aptoide.pt.install_manager.dto.Constraints
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import kotlinx.coroutines.flow.Flow

/**
 * This represents an app install/uninstall task
 */

interface Task {
  /**
   * Package name.
   *
   * Used as an identifier of the task.
   */
  val packageName: String

  /**
   * Task Type.
   *
   * Returns the [Type] of this Task
   */
  val type: Type

  /**
   * InstallPackageInfo used to create this task.
   *
   * Returns the [InstallPackageInfo] for this Task
   */
  val installPackageInfo: InstallPackageInfo

  /**
   * Task Constraints.
   *
   * Returns [Constraints] of this Task
   */
  val constraints: Constraints

  /**
   * Flag showing task is finished or not yet.
   */
  val isFinished: Boolean

  /**
   * Current [State] of the task.
   */
  val state: State

  /**
   * The [Flow] of the task state as it changes. Immediately emits the
   * current ones for any new subscriber. Completes as soon as task is finished.
   */
  val stateAndProgress: Flow<State>

  /**
   * Allows the task to perform downloads on metered networks. And schedules it's execution
   * immediately if possible.
   *
   * Tasks that are already running cannot be altered, so nothing will happen.
   */
  fun allowDownloadOnMetered()

  /**
   * Cancel the task if possible.
   *
   * Tasks that are already completed/failed/cancelled cannot be canceled, so nothing will happen.
   */
  fun cancel()

  interface Factory {
    fun enqueue(
      packageName: String,
      type: Type,
      installPackageInfo: InstallPackageInfo,
      constraints: Constraints,
    ): Task
  }

  enum class Type {
    INSTALL,
    UNINSTALL
  }

  sealed class State {
    /* Waiting to be started */
    object Pending: State()

    /* Downloading the files */
    data class Downloading(val progress: Int): State()

    /* Files are ready for installation */
    object ReadyToInstall: State()

    /* Installing the downloaded files */
    data class Installing(val progress: Int): State()

    /* Uninstalling the app files */
    data class Uninstalling(val progress: Int): State()

    /* Work is finished */
    object Completed: State()

    /* Work is aborted */
    object Aborted: State()

    /* Work is canceled */
    object Canceled: State()

    /* Work is failed */
    object Failed: State()

    /* Work is failed due to lack of space */
    object OutOfSpace: State()
  }
}
