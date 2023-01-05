package cm.aptoide.pt.install_manager

import android.content.pm.PackageInfo
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

internal class RealInstallManager<D>(builder: InstallManager.IBuilder<D>) : InstallManager<D>,
  Task.Factory {
  private val scope = CoroutineScope(builder.context)
  private val packageInfoRepository = builder.packageInfoRepository
  private val appDetailsRepository = builder.appDetailsRepository
  private val jobDispatcher = JobDispatcher(scope)
  private val taskInfoRepository = builder.taskInfoRepository
  private val packageDownloader: PackageDownloader = builder.packageDownloader
  private val packageInstaller: PackageInstaller = builder.packageInstaller
  private val context = builder.context
  private val clock = builder.clock

  private val cachedApps = HashMap<String, WeakReference<RealApp<D>>>()

  private var restored = false

  override suspend fun getApp(packageName: String, details: D?): RealApp<D> =
    withContext(context) { getOrCreateApp(packageName, details = details) }

  override suspend fun getInstalledApps(): Set<RealApp<D>> = withContext(context) {
    packageInfoRepository.getAll()
      .map {
        getOrCreateApp(
          packageName = it.packageName,
          packageInfo = it
        )
      }
      .toSet()
  }

  override fun getWorkingAppInstallers(): Flow<RealApp<D>?> =
    jobDispatcher.runningJob.map { task -> task?.packageName?.let { getOrCreateApp(it) } }

  override suspend fun restore() {
    if (restored) return
    restored = true
    taskInfoRepository.getAll()
      .sortedBy { it.timestamp }
      .map {
        getApp(it.packageName).apply {
          if (getTask() == null) {
            taskInfoRepository.removeAll(it.packageName)
            when (it.type) {
              Task.Type.INSTALL -> install(it.installPackageInfo)
              Task.Type.UNINSTALL -> uninstall()
            }
          }
        }
      }
  }

  private suspend fun getOrCreateApp(
    packageName: String,
    packageInfo: PackageInfo? = null,
    details: D? = null
  ) = cachedApps[packageName]?.get()
    ?.apply { details?.let { setDetails(it) } }
    ?: RealApp.create(
      packageName = packageName,
      details = details,
      packageInfo = packageInfo,
      taskFactory = this@RealInstallManager,
      packageInfoRepository = packageInfoRepository,
      appDetailsRepository = appDetailsRepository,
      context = context,
    ).also {
      cachedApps[packageName] = WeakReference(it)
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
