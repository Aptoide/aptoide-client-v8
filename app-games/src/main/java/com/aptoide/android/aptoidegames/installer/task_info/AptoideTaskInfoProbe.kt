package com.aptoide.android.aptoidegames.installer.task_info

import cm.aptoide.pt.install_manager.dto.TaskInfo
import cm.aptoide.pt.install_manager.repository.TaskInfoRepository
import com.aptoide.android.aptoidegames.installer.analytics.InstallAnalytics

class AptoideTaskInfoProbe(
  private val taskInfoRepository: TaskInfoRepository,
  private val installAnalytics: InstallAnalytics
) : TaskInfoRepository {
  override suspend fun getAll(): Set<TaskInfo> = taskInfoRepository.getAll()

  override suspend fun saveJob(taskInfo: TaskInfo) {
    taskInfoRepository.saveJob(taskInfo)
    installAnalytics.sendOnInstallationQueued(taskInfo.packageName, taskInfo.installPackageInfo)
  }

  override suspend fun remove(vararg taskInfo: TaskInfo) = taskInfoRepository.remove(*taskInfo)
}
