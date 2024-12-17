package cm.aptoide.pt.install_manager

import android.content.pm.PackageInfo
import cm.aptoide.pt.install_manager.dto.Constraints
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.SizeEstimator
import cm.aptoide.pt.install_manager.dto.TaskInfo
import cm.aptoide.pt.install_manager.environment.DeviceStorage
import cm.aptoide.pt.install_manager.environment.NetworkConnection
import cm.aptoide.pt.install_manager.repository.AppInfoRepository
import cm.aptoide.pt.install_manager.repository.TaskInfoRepository
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class RealInstallManager(
  private val scope: CoroutineScope,
  private val currentTime: () -> Long,
  private val deviceStorage: DeviceStorage,
  private val sizeEstimator: SizeEstimator,
  networkConnection: NetworkConnection,
  private val appInfoRepository: AppInfoRepository,
  private val taskInfoRepository: TaskInfoRepository,
  private val packageDownloader: PackageDownloader,
  private val packageInstaller: PackageInstaller,
) : InstallManager, Task.Factory {
  private val jobDispatcher = JobDispatcher(scope, networkConnection)

  private val appsCache = AppsCache()

  private val systemUpdates = MutableSharedFlow<String>()

  private var restored = false

  init {
    appInfoRepository.setOnChangeListener {
      appsCache[it]?.update()
      scope.launch {
        systemUpdates.emit(it)
      }
    }
  }

  override fun getApp(packageName: String): App = getOrCreateApp(packageName)

  override val installedApps: Set<App>
    get() = appInfoRepository.getAllPackageInfos()
      .map {
        getOrCreateApp(
          packageName = it.packageName,
          packageInfo = it,
          updatesOwnerPackageName = appInfoRepository.getUpdateOwnerPackageName(it.packageName)
        )
      }
      .toSet()

  override val workingAppInstallers: Flow<App?> = jobDispatcher.runningTask.map { task ->
    task?.let {
      getOrCreateApp(packageName = it.packageName)
    }
  }

  override val scheduledApps: List<App>
    get() = appsCache.busyApps.values
      .filterNotNull()
      .sortedBy { (it.task as RealTask?)?.taskInfo?.timestamp }
      .filter { it.task?.state is Task.State.Pending }

  override val appsChanges: Flow<App> = systemUpdates.map(::getOrCreateApp)

  override fun getMissingFreeSpaceFor(installPackageInfo: InstallPackageInfo): Long =
    jobDispatcher.getScheduledIPF().sumOf(sizeEstimator::getTotalInstallationSize) +
      sizeEstimator.getTotalInstallationSize(installPackageInfo) -
      deviceStorage.availableFreeSpace

  override suspend fun restore() {
    val tasksToRemove = mutableListOf<TaskInfo>()
    if (restored) return
    restored = true
    taskInfoRepository.getAll()
      .sortedBy(TaskInfo::timestamp)
      .forEach { taskInfo ->
        val app = getOrCreateApp(packageName = taskInfo.packageName)

        if (taskInfo.type == Task.Type.INSTALL
          && app.canInstall(taskInfo.installPackageInfo, taskInfo.constraints)
            ?.takeUnless { it is OutOfSpaceException } != null
        ) {
          tasksToRemove.add(taskInfo)
        } else {
          app.apply {
            tasks.value = taskInfo.toTask().enqueue(alreadySaved = true)
          }
        }
      }
    scope.launch {
      taskInfoRepository.remove(*tasksToRemove.toTypedArray())
    }
  }

  private fun getOrCreateApp(
    packageName: String,
    packageInfo: PackageInfo? = null,
    updatesOwnerPackageName: String? = null
  ) = appsCache[packageName] ?: RealApp(
    packageName = packageName,
    packageInfo = packageInfo,
    updatesOwnerPackageName = updatesOwnerPackageName,
    taskFactory = this@RealInstallManager,
    getMissingSpace = { it - deviceStorage.availableFreeSpace },
    sizeEstimator = sizeEstimator,
    appInfoRepository = appInfoRepository
  ).also {
    appsCache[packageName] = it
  }

  override fun enqueue(
    packageName: String,
    type: Task.Type,
    installPackageInfo: InstallPackageInfo,
    constraints: Constraints,
  ): Task = TaskInfo(
    packageName = packageName,
    installPackageInfo = installPackageInfo,
    constraints = constraints,
    type = type,
    timestamp = currentTime()
  )
    .toTask()
    .enqueue()

  private fun TaskInfo.toTask(): RealTask = RealTask(
    scope = scope,
    appsCache = appsCache,
    taskInfo = this,
    hasEnoughSpace = { it - deviceStorage.availableFreeSpace <= 0 },
    sizeEstimator = sizeEstimator,
    jobDispatcher = jobDispatcher,
    packageDownloader = packageDownloader,
    packageInstaller = packageInstaller,
    taskInfoRepository = taskInfoRepository
  ).also {
    appsCache.setBusy(packageName, true)
  }
}
