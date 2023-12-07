package cm.aptoide.pt.install_manager

import android.content.pm.PackageInfo
import cm.aptoide.pt.install_manager.dto.Constraints
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.TaskInfo
import cm.aptoide.pt.install_manager.environment.FreeSpaceChecker
import cm.aptoide.pt.install_manager.repository.PackageInfoRepository
import cm.aptoide.pt.install_manager.repository.TaskInfoRepository
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

internal class RealInstallManager(
  private val scope: CoroutineScope,
  private val currentTime: () -> Long,
  private val freeSpaceChecker: FreeSpaceChecker,
  private val packageInfoRepository: PackageInfoRepository,
  private val taskInfoRepository: TaskInfoRepository,
  private val packageDownloader: PackageDownloader,
  private val packageInstaller: PackageInstaller,
) : InstallManager, Task.Factory {
  private val jobDispatcher = JobDispatcher(scope)

  private val cachedApps = HashMap<String, WeakReference<RealApp>>()

  private val systemUpdates = MutableSharedFlow<String>()

  private var restored = false

  init {
    packageInfoRepository.setOnChangeListener {
      cachedApps[it]?.get()?.update()
      scope.launch {
        systemUpdates.emit(it)
      }
    }
  }

  override fun getApp(packageName: String): App = getOrCreateApp(packageName)

  override val installedApps: Set<App>
    get() = packageInfoRepository.getAll()
      .map {
        getOrCreateApp(
          packageName = it.packageName,
          packageInfo = it
        )
      }
      .toSet()

  override val workingAppInstallers: Flow<App?> = jobDispatcher.runningTask.map { task ->
    task?.let {
      getOrCreateApp(packageName = it.packageName)
    }
  }

  override val appsChanges: Flow<App> = systemUpdates.map(::getOrCreateApp)

  override suspend fun restore() {
    if (restored) return
    restored = true
    taskInfoRepository.getAll()
      .sortedBy(TaskInfo::timestamp)
      .map {
        getOrCreateApp(packageName = it.packageName).apply {
          tasks.value = it.toTask().enqueue(alreadySaved = true)
        }
      }
  }

  private fun getOrCreateApp(
    packageName: String,
    packageInfo: PackageInfo? = null,
  ) = cachedApps[packageName]?.get()
    ?: RealApp(
      packageName = packageName,
      packageInfo = packageInfo,
      taskFactory = this@RealInstallManager,
      jobDispatcher = jobDispatcher,
      freeSpaceChecker = freeSpaceChecker,
      packageInfoRepository = packageInfoRepository
    ).also {
      cachedApps[packageName] = WeakReference(it)
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
    taskInfo = this,
    jobDispatcher = jobDispatcher,
    freeSpaceChecker = freeSpaceChecker,
    packageDownloader = packageDownloader,
    packageInstaller = packageInstaller,
    taskInfoRepository = taskInfoRepository
  )
}
