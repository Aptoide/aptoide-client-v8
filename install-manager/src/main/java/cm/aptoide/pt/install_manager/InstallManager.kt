package cm.aptoide.pt.install_manager

import android.content.Context
import cm.aptoide.pt.install_manager.repository.AppDetailsRepository
import cm.aptoide.pt.install_manager.repository.PackageInfoRepository
import cm.aptoide.pt.install_manager.repository.PackageInfoRepositoryImpl
import cm.aptoide.pt.install_manager.repository.TaskInfoRepository
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

/**
 * An apps install manager.
 *
 * This interface represents the manager that controls the apps installs.
 *
 * @param D the type of an [App] details.
 */
interface InstallManager<D> {

  /**
   * Get an app.
   *
   * @param packageName - an app package name
   * @param details - a new details to save and use for the app
   * @returns existing or creates a new [App] for the package name.
   */
  suspend fun getApp(packageName: String, details: D? = null): App<D>

  /**
   * Get all installed apps
   *
   * @returns all apps that are installed
   */
  suspend fun getInstalledApps(): Set<App<D>>

  /**
   * Get currently working app installers
   *
   * @returns a [Flow] of [App] with ongoing installation task job one by one.
   * Emits null as soon as there are no more apps with running task jobs
   */
  fun getWorkingAppInstallers(): Flow<App<D>?>

  /**
   * Restore unfinished tasks
   *
   * Restores and enqueues tasks that were not finished (not removed from [TaskInfoRepository]) so
   * they would continue till finished.
   * Blocks only until [TaskInfoRepository] returns and tasks enqueued.
   */
  suspend fun restore()

  interface IBuilder<P> {
    val packageInfoRepository: PackageInfoRepository
    val appDetailsRepository: AppDetailsRepository<P>
    val packageDownloader: PackageDownloader
    val packageInstaller: PackageInstaller
    val taskInfoRepository: TaskInfoRepository
    val context: CoroutineContext
    val clock: Clock

    fun build(): InstallManager<P> = RealInstallManager(this)
  }

  class Builder<P>(context: Context) : IBuilder<P> {
    override var packageInfoRepository: PackageInfoRepository = PackageInfoRepositoryImpl(context)
    override lateinit var appDetailsRepository: AppDetailsRepository<P>
    override lateinit var packageDownloader: PackageDownloader
    override lateinit var packageInstaller: PackageInstaller
    override lateinit var taskInfoRepository: TaskInfoRepository
    override var context: CoroutineContext = Dispatchers.IO
    override var clock: Clock = Clock { System.currentTimeMillis() }
  }
}