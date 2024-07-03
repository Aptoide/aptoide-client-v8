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
   * The [Flow] of the task state and progress as it changes. Immediately emits the
   * current ones for any new subscriber. Completes as soon as task is finished.
   */
  val stateAndProgress: Flow<Pair<State, Int>>

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

    /* Work is aborted */
    ABORTED,

    /* Work is canceled */
    CANCELED,

    /* Work is failed */
    FAILED,
  }
}
