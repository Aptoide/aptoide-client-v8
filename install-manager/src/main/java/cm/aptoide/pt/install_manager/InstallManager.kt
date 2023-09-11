package cm.aptoide.pt.install_manager

import android.content.Context
import cm.aptoide.pt.install_manager.repository.PackageInfoRepository
import cm.aptoide.pt.install_manager.repository.PackageInfoRepositoryImpl
import cm.aptoide.pt.install_manager.repository.TaskInfoRepository
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

/**
 * An apps install manager.
 *
 * This interface represents the manager that controls the apps installs.
 */
interface InstallManager {

  /**
   * Get an app.
   *
   * @param packageName - an app package name
   * @returns existing or creates a new [App] for the package name.
   */
  fun getApp(packageName: String): App

  /**
   * Get all installed apps
   *
   * @returns all apps that are installed
   */
  suspend fun getInstalledApps(): Set<App>

  /**
   * Get currently working app installers
   *
   * @returns a [Flow] of [App] with ongoing installation task job one by one.
   * Emits null as soon as there are no more apps with running task jobs
   */
  fun getWorkingAppInstallers(): Flow<App?>

  /**
   * Get apps when they are installed or removed from the system
   *
   * @returns a [Flow] of [App] with just installed or removed app
   */
  fun getAppsChanges(): Flow<App>

  /**
   * Restore unfinished tasks
   *
   * Restores and enqueues tasks that were not finished (not removed from [TaskInfoRepository]) so
   * they would continue till finished.
   * Blocks only until [TaskInfoRepository] returns and tasks enqueued.
   */
  fun restore()

  val waitingForDownload: Flow<List<App>>

  fun enqueueDeferredTasks(forceDownload: Boolean)

  interface IBuilder {
    val packageInfoRepository: PackageInfoRepository
    val packageDownloader: PackageDownloader
    val packageInstaller: PackageInstaller
    val taskInfoRepository: TaskInfoRepository
    val scope: CoroutineScope
    val appContext: Context
    val clock: Clock

    fun build(): InstallManager = RealInstallManager(this)
  }

  class Builder(
    context: Context,
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
  ) : IBuilder {
    override var packageInfoRepository: PackageInfoRepository =
      PackageInfoRepositoryImpl(context, scope)
    override lateinit var packageDownloader: PackageDownloader
    override lateinit var packageInstaller: PackageInstaller
    override lateinit var taskInfoRepository: TaskInfoRepository
    override var appContext = context
    override var clock: Clock = Clock { System.currentTimeMillis() }
  }
}