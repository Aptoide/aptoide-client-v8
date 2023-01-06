package cm.aptoide.pt.install_manager

import android.content.Context
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
 */
interface InstallManager {

  /**
   * Get an app.
   *
   * @param packageName - an app package name
   * @returns existing or creates a new [App] for the package name.
   */
  suspend fun getApp(packageName: String): App

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
   * Restore unfinished tasks
   *
   * Restores and enqueues tasks that were not finished (not removed from [TaskInfoRepository]) so
   * they would continue till finished.
   * Blocks only until [TaskInfoRepository] returns and tasks enqueued.
   */
  suspend fun restore()

  interface IBuilder {
    val packageInfoRepository: PackageInfoRepository
    val packageDownloader: PackageDownloader
    val packageInstaller: PackageInstaller
    val taskInfoRepository: TaskInfoRepository
    val context: CoroutineContext
    val clock: Clock

    fun build(): InstallManager = RealInstallManager(this)
  }

  class Builder(context: Context) : IBuilder {
    override var packageInfoRepository: PackageInfoRepository = PackageInfoRepositoryImpl(context)
    override lateinit var packageDownloader: PackageDownloader
    override lateinit var packageInstaller: PackageInstaller
    override lateinit var taskInfoRepository: TaskInfoRepository
    override var context: CoroutineContext = Dispatchers.IO
    override var clock: Clock = Clock { System.currentTimeMillis() }
  }
}