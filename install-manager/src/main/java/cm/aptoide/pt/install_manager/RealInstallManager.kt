package cm.aptoide.pt.install_manager

import android.content.pm.PackageInfo
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.ref.WeakReference

internal class RealInstallManager(builder: InstallManager.IBuilder) : InstallManager,
  Task.Factory {
  private val scope = builder.scope
  private val packageInfoRepository = builder.packageInfoRepository
  private val jobDispatcher = JobDispatcher(scope)
  private val taskInfoRepository = builder.taskInfoRepository
  private val packageDownloader: PackageDownloader = builder.packageDownloader
  private val packageInstaller: PackageInstaller = builder.packageInstaller

  private val context = builder.scope.coroutineContext
  private val clock = builder.clock

  private val cachedApps = HashMap<String, WeakReference<RealApp>>()

  private val systemUpdates = MutableSharedFlow<String>()

  private var restored = false

  private val deferredTasks = MutableStateFlow<List<RealTask>>(emptyList())

  override val waitingForDownload: Flow<List<App>>
    get() = deferredTasks.map { task -> task.map { getOrCreateApp(it.packageName) } }

  private val mutex = Mutex()

  init {
    packageInfoRepository.setOnChangeListener {
      cachedApps[it]?.get()?.update()
      delay(1) // Suspend to let the app data update before informing the listeners
      systemUpdates.emit(it)
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
    jobDispatcher.runningJob.map { task -> task?.packageName?.let { getOrCreateApp(it) } }

  override fun getAppsChanges(): Flow<App> = systemUpdates
    .map { getOrCreateApp(packageName = it) }

  override fun restore() {
    scope.launch {
      if (restored) return@launch
      restored = true

      try {
        mutex.withLock {
          taskInfoRepository.getAll()
            .sortedBy { it.timestamp }
            .map {
              getApp(it.packageName).apply {
                if (tasks.first() == null) {
                  taskInfoRepository.removeAll(it.packageName)
                  when (it.type) {
                    Task.Type.INSTALL -> install(it.installPackageInfo, it.waitForWifi)
                    Task.Type.UNINSTALL -> uninstall()
                  }
                }
              }
            }
        }
      } catch (e: Exception) {
        Timber.e(e)
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
      packageInfoRepository = packageInfoRepository,
      scope = scope,
    ).also {
      cachedApps[packageName] = WeakReference(it)
    }

  override suspend fun createTask(
    packageName: String,
    type: Task.Type,
    forceDownload: Boolean,
    installPackageInfo: InstallPackageInfo,
    onTerminate: suspend (success: Boolean) -> Unit,
  ): Task = RealTask(
    jobDispatcher = jobDispatcher,
    packageName = packageName,
    installPackageInfo = installPackageInfo,
    type = type,
    packageDownloader = packageDownloader,
    packageInstaller = packageInstaller,
    taskInfoRepository = taskInfoRepository,
    forceDownload = forceDownload,
    deferMe = {
      deferredTasks.emit(deferredTasks.first() + it)
    },
    onTerminate = { wasCompleted ->
      //Remove task from deferred if it exists
      deferredTasks.emit(deferredTasks.first().filter { it.packageName != packageName })
      onTerminate(wasCompleted)
    },
    clock = clock
  ).apply {
    enqueue(false)
  }

  override fun enqueueDeferredTasks(forceDownload: Boolean) {
    scope.launch {
      mutex.withLock {
        val tasks = deferredTasks.first()
        deferredTasks.emit(emptyList())
        tasks.forEach { it.enqueue(forceDownload) }
      }
    }
  }
}
