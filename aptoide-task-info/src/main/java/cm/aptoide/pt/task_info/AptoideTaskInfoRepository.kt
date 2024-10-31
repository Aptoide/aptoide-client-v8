package cm.aptoide.pt.task_info

import cm.aptoide.pt.install_manager.dto.Constraints
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.InstallationFile
import cm.aptoide.pt.install_manager.dto.TaskInfo
import cm.aptoide.pt.install_manager.repository.TaskInfoRepository
import cm.aptoide.pt.task_info.database.InstallationFileDao
import cm.aptoide.pt.task_info.database.TaskInfoDao
import cm.aptoide.pt.task_info.database.model.InstallationFileData
import cm.aptoide.pt.task_info.database.model.TaskInfoData
import javax.inject.Inject

class AptoideTaskInfoRepository @Inject constructor(
  private val taskInfoDao: TaskInfoDao,
  private val installationFileDao: InstallationFileDao,
) : TaskInfoRepository {

  override suspend fun getAll(): Set<TaskInfo> = taskInfoDao.getAll()
    .map {
      TaskInfo(
        packageName = it.taskInfo.packageName,
        installPackageInfo = InstallPackageInfo(
          versionCode = it.taskInfo.versionCode,
          installationFiles = it.installationFiles.map { file ->
            InstallationFile(
              name = file.name,
              type = file.type,
              md5 = file.md5,
              fileSize = file.fileSize,
              url = file.url,
              altUrl = file.altUrl,
              localPath = file.localPath,
            )
          }.toSet(),
          payload = it.taskInfo.payload,
        ),
        constraints = it.taskInfo.constraints.deserialize(),
        type = it.taskInfo.type,
        timestamp = it.taskInfo.timestamp
      )
    }
    .toSet()

  override suspend fun saveJob(taskInfo: TaskInfo) {
    taskInfoDao.save(
      TaskInfoData(
        packageName = taskInfo.packageName,
        versionCode = taskInfo.installPackageInfo.versionCode,
        versionName = "",
        constraints = taskInfo.constraints.serialize(),
        type = taskInfo.type,
        timestamp = taskInfo.timestamp,
        payload = taskInfo.installPackageInfo.payload,
      )
    )
    installationFileDao.save(
      taskInfo.installPackageInfo.installationFiles.map {
        InstallationFileData(
          taskTimestamp = taskInfo.timestamp,
          name = it.name,
          type = it.type,
          md5 = it.md5,
          fileSize = it.fileSize,
          url = it.url,
          altUrl = it.altUrl,
          localPath = it.localPath,
        )
      }
    )
  }

  override suspend fun remove(vararg taskInfo: TaskInfo) {
    val timestamps = taskInfo.map { it.timestamp }.toLongArray()
    taskInfoDao.remove(*timestamps)
    installationFileDao.remove(*timestamps)
  }
}

private fun Constraints.serialize(): String = "$checkForFreeSpace|$networkType"

private fun String.deserialize(): Constraints = split("|").let {
  Constraints(
    checkForFreeSpace = it[0] != "false",
    networkType = try {
      Constraints.NetworkType.valueOf(it[1])
    } catch (e: Exception) {
      Constraints.NetworkType.NOT_REQUIRED
    }
  )
}
