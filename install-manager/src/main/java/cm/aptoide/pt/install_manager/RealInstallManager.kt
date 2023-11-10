package cm.aptoide.pt.install_manager

import android.content.pm.PackageInfo
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.TaskInfo
import cm.aptoide.pt.install_manager.repository.PackageInfoRepository
import cm.aptoide.pt.install_manager.repository.TaskInfoRepository
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

internal class RealInstallManager(
  private val scope: CoroutineScope,
  private val currentTime: () -> Long,
  private val packageInfoRepository: PackageInfoRepository,
  private val taskInfoRepository: TaskInfoRepository,
  private val packageDownloader: PackageDownloader,
  private val packageInstaller: PackageInstaller,
) : InstallManager, Task.Factory {
  private val jobDispatcher = JobDispatcher(scope)
  private val context = scope.coroutineContext

  private val cachedApps = HashMap<String, WeakReference<RealApp>>()

  private val systemUpdates = MutableSharedFlow<String>()

  private var restored = false

  init {
    packageInfoRepository.setOnChangeListener {
      cachedApps[it]?.get()?.update()
      scope.launch { systemUpdates.emit(it) }
    }
  }

  override fun getApp(packageName: String): RealApp = getOrCreateApp(packageName)

  override suspend fun getInstalledApps(): Set<RealApp> = withContext(context) {
    packageInfoRepository.getAll()
      .map {
        getOrCreateApp(
          packageName = it.packageName,
          packageInfo = it
        )
      }
      .toSet()
  }

  override fun getWorkingAppInstallers(): Flow<RealApp?> =
    jobDispatcher.runningTask.map { task ->
      task?.let {
        getOrCreateApp(
          packageName = it.packageName,
          task = it
        )
      }
    }

  override fun getAppsChanges(): Flow<App> = systemUpdates.map(::getOrCreateApp)

  override suspend fun restore() {
    if (restored) return
    restored = true
    taskInfoRepository.getAll()
      .sortedBy(TaskInfo::timestamp)
      .map { enqueue(it) }
      .map {
        getOrCreateApp(
          packageName = it.packageName,
          task = it
        )
      }
  }

  private fun getOrCreateApp(
    packageName: String,
    packageInfo: PackageInfo? = null,
    task: Task? = null,
  ) = cachedApps[packageName]?.get()
    ?: RealApp(
      packageName = packageName,
      packageInfo = packageInfo,
      task = task,
      taskFactory = this@RealInstallManager,
      packageInfoRepository = packageInfoRepository,
    ).also {
      cachedApps[packageName] = WeakReference(it)
    }

  override suspend fun enqueue(
    packageName: String,
    type: Task.Type,
    installPackageInfo: InstallPackageInfo,
  ): Task = enqueue(TaskInfo(packageName, installPackageInfo, type, currentTime()))
    .also {
      taskInfoRepository.saveJob(it.taskInfo)
    }

  private suspend fun enqueue(taskInfo: TaskInfo): RealTask = RealTask(
    taskInfo = taskInfo,
    packageDownloader = packageDownloader,
    packageInstaller = packageInstaller,
    taskInfoRepository = taskInfoRepository,
  ).also {
    jobDispatcher.enqueue(it)
  }
}
