package cm.aptoide.pt.install_manager

import android.content.Context
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.SizeEstimator
import cm.aptoide.pt.install_manager.dto.SizeEstimatorImpl
import cm.aptoide.pt.install_manager.environment.DeviceStorage
import cm.aptoide.pt.install_manager.environment.DeviceStorageImpl
import cm.aptoide.pt.install_manager.environment.NetworkConnection
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
  val installedApps: Set<App>

  /**
   * Get currently working app installers
   *
   * @returns a [Flow] of [App] with ongoing installation task job one by one.
   * Emits null as soon as there are no more apps with running task jobs
   */
  val workingAppInstallers: Flow<App?>

  /**
   * Get all apps with scheduled (not yet running) unfinished tasks in order of scheduling
   *
   * @returns all apps that have scheduled unfinished tasks in order of scheduling
   */
  val scheduledApps: List<App>

  /**
   * Get apps when they are installed or removed from the system
   *
   * @returns a [Flow] of [App] with just installed or removed app
   */
  val appsChanges: Flow<App>

  /**
   * Calculates how much space freeing is missing for the package download & installation, taking
   * into account all running and scheduled apps tasks.
   *
   * @param installPackageInfo - a package info to use for the calculation
   * @return how much space freeing is required to download and install the [installPackageInfo].
   *    0 or negative value means there is already enough space for a download and installation.
   */
  fun getMissingFreeSpaceFor(installPackageInfo: InstallPackageInfo): Long

  /**
   * Restore unfinished tasks
   *
   * Restores and enqueues tasks that were not finished (not removed from [TaskInfoRepository]) so
   * they would continue till finished.
   * Blocks only until [TaskInfoRepository] returns and tasks enqueued.
   */
  suspend fun restore()

  companion object {

    /**
     * Instantiate an InstallManager implementation with the provided configuration
     *
     * @param context - application [Context] used for [PackageInfoRepository]
     * @param scope - a coroutine scope used to run all [InstallManager] logic
     * @param currentTime - a function to get current time (this abstraction is needed for testing)
     * @param deviceStorage - a [DeviceStorage] to check the available device free space
     * @param packageInfoRepository - a [PackageInfoRepository] to get info about installed apps
     * @param taskInfoRepository - a [TaskInfoRepository] to store running tasks information
     * @param packageDownloader - a [PackageDownloader] to perform installation files download
     * @param packageInstaller - a [PackageInstaller] to perform installation of downloaded files
     * @param networkConnection - a [NetworkConnection] to get the network current state and it's changes
     * @returns an [InstallManager] with provided configuration
     */
    fun with(
      context: Context,
      scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
      currentTime: () -> Long = { System.currentTimeMillis() },
      deviceStorage: DeviceStorage = DeviceStorageImpl(),
      sizeEstimator: SizeEstimator = SizeEstimatorImpl(),
      packageInfoRepository: PackageInfoRepository = PackageInfoRepositoryImpl(context),
      taskInfoRepository: TaskInfoRepository,
      packageDownloader: PackageDownloader,
      packageInstaller: PackageInstaller,
      networkConnection: NetworkConnection,
    ): InstallManager = RealInstallManager(
      scope = scope,
      currentTime = currentTime,
      deviceStorage = deviceStorage,
      sizeEstimator = sizeEstimator,
      packageInfoRepository = packageInfoRepository,
      taskInfoRepository = taskInfoRepository,
      packageDownloader = packageDownloader,
      packageInstaller = packageInstaller,
      networkConnection = networkConnection,
    )
  }
}
