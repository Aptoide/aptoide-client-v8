package cm.aptoide.pt.install_manager

import android.content.pm.PackageInfo
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

internal class RealInstallManager(builder: InstallManager.IBuilder) : InstallManager,
  Task.Factory {
  private val scope = CoroutineScope(builder.context)
  private val packageInfoRepository = builder.packageInfoRepository
  private val jobDispatcher = JobDispatcher(scope)
  private val taskInfoRepository = builder.taskInfoRepository
  private val packageDownloader: PackageDownloader = builder.packageDownloader
  private val packageInstaller: PackageInstaller = builder.packageInstaller
  private val context = builder.context
  private val clock = builder.clock

  private val cachedApps = HashMap<String, WeakReference<RealApp>>()

  private var restored = false

  init {
    packageInfoRepository.setOnChangeListener {
      cachedApps[it]?.get()?.update()
    }
  }

  override suspend fun getApp(packageName: String): RealApp =
    withContext(context) { getOrCreateApp(packageName) }

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
    jobDispatcher.runningJob.map { task -> task?.packageName?.let { getOrCreateApp(it) } }

  override suspend fun restore() {
    if (restored) return
    restored = true
    taskInfoRepository.getAll()
      .sortedBy { it.timestamp }
      .map {
        getApp(it.packageName).apply {
          if (tasks.first() == null) {
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
  ) = cachedApps[packageName]?.get()
    ?: RealApp.create(
      packageName = packageName,
      packageInfo = packageInfo,
      taskFactory = this@RealInstallManager,
      packageInfoRepository = packageInfoRepository,
    ).also {
      cachedApps[packageName] = WeakReference(it)
    }

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
