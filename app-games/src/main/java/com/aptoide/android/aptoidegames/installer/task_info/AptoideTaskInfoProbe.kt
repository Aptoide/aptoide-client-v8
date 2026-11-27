package com.aptoide.android.aptoidegames.installer.task_info

import cm.aptoide.pt.install_manager.Task
import cm.aptoide.pt.install_manager.dto.TaskInfo
import cm.aptoide.pt.install_manager.repository.TaskInfoRepository
import com.aptoide.android.aptoidegames.installer.analytics.InstallAnalytics

/**
 * @param onInstallQueued told the package of every install the install manager queues, whatever
 * screen started it (AND-954). A task restored at app start is not told again; one saved again
 * (allowed on mobile data) is, and the clicks keep one id per install.
 */
class AptoideTaskInfoProbe(
  private val taskInfoRepository: TaskInfoRepository,
  private val installAnalytics: InstallAnalytics,
  private val onInstallQueued: (packageName: String) -> Unit = {},
) : TaskInfoRepository {
  override suspend fun getAll(): Set<TaskInfo> = taskInfoRepository.getAll()

  override suspend fun saveJob(taskInfo: TaskInfo) {
    taskInfoRepository.saveJob(taskInfo)
    installAnalytics.sendOnInstallationQueued(taskInfo.packageName, taskInfo.installPackageInfo)
    if (taskInfo.type == Task.Type.INSTALL) onInstallQueued(taskInfo.packageName)
  }

  override suspend fun remove(vararg taskInfo: TaskInfo) = taskInfoRepository.remove(*taskInfo)
}
