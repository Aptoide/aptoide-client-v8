package cm.aptoide.pt.install_manager

import cm.aptoide.pt.install_manager.dto.AppInfo
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.*

internal class RealInstallManager<D>(builder: InstallManager.Builder<D>) : InstallManager<D>,
  Task.Factory {
  private val scope = CoroutineScope(builder.context)
  private val appInfoRepository = builder.appInfoRepository
  private val jobDispatcher = JobDispatcher(scope)
  private val taskInfoRepository = builder.taskInfoRepository
  private val packageDownloader: PackageDownloader = builder.packageDownloader
  private val packageInstaller: PackageInstaller = builder.packageInstaller
  private val context = builder.context
  private val clock = builder.clock

  private val cachedApps = WeakHashMap<String, RealApp<D>>()

  private var restored = false

  override suspend fun getApp(packageName: String, details: D?): RealApp<D> =
    withContext(context) {
      cachedApps[packageName]
        ?: appInfoRepository.get(packageName)?.createApp(true)
          ?.apply { details?.let { setDetails(it) } }
        ?: AppInfo(packageName = packageName, details = details).createApp(false)
    }

  override suspend fun getKnownApps(): List<RealApp<D>> = withContext(context) {
    appInfoRepository.getAll()
      .map {
        cachedApps[it.packageName] ?: it.createApp(true)
      }
  }

  override fun getWorkingAppInstallers(): Flow<RealApp<D>?> =
    jobDispatcher.runningJob.map { task -> task?.packageName?.let { getApp(it) } }

  override suspend fun restore() {
    if (restored) return
    restored = true
    taskInfoRepository.getAll()
      .sortedBy { it.timestamp }
      .map {
        taskInfoRepository.removeAll(it.packageName)
        getApp(it.packageName).apply {
          if (getTask() == null) {
            when (it.type) {
              Task.Type.INSTALL -> install(it.installPackageInfo)
              Task.Type.UNINSTALL -> uninstall()
            }
          }
        }
      }
  }

  private fun AppInfo<D>.createApp(isKnown: Boolean) = RealApp(
    packageName = packageName,
    isKnown = isKnown,
    _installedVersion = installedVersion,
    _details = details,
    taskFactory = this@RealInstallManager,
    appInfoRepository = appInfoRepository,
    context = context,
  ).also {
    cachedApps[packageName] = it
  }

  override suspend fun getTask(packageName: String): Task? = jobDispatcher.findTask(packageName)

  override suspend fun createTask(
    packageName: String,
    type: Task.Type,
    installPackageInfo: InstallPackageInfo,
    onTerminate: suspend (success: Boolean) -> Unit
  ): Task = RealTask(
    jobDispatcher = jobDispatcher,
    packageName = packageName,
    installPackageInfo = installPackageInfo,
    type = type,
    packageDownloader = packageDownloader,
    packageInstaller = packageInstaller,
    taskInfoRepository = taskInfoRepository,
    onTerminate = onTerminate,
    clock = clock
  ).apply { enqueue() }
}

