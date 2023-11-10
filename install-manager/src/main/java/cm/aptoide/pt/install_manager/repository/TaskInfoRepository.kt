package cm.aptoide.pt.install_manager.repository

import cm.aptoide.pt.install_manager.dto.TaskInfo

/**
 * A storage that keeps info for unfinished tasks.
 */
interface TaskInfoRepository {

  /**
   * Get all tasks.
   * Should have only 1 task per package name
   *
   * @returns a list of unfinished tasks with enqueue timestamps.
   */
  suspend fun getAll(): Set<TaskInfo>

  /**
   * Save a new enqueued task info.
   * Tasks info should be unique by timestamp as for the same [TaskInfo.packageName],
   * [TaskInfo.installPackageInfo] and [TaskInfo.type] there can be a number of records for partial
   * task jobs enqueued at different time
   *
   * @param taskInfo - a task info
   */
  suspend fun saveJob(taskInfo: TaskInfo)

  /**
   * Removes all enqueued task jobs by package name.
   *
   * @param packageName - a package name
   */
  suspend fun removeAll(packageName: String)
}

