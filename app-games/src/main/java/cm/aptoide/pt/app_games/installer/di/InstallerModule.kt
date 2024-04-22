package cm.aptoide.pt.app_games.installer.di

import android.content.Context
import android.content.pm.PackageManager
import androidx.room.Room
import cm.aptoide.pt.app_games.installer.UninstallPermissions
import cm.aptoide.pt.app_games.installer.UninstallPermissionsImpl
import cm.aptoide.pt.app_games.installer.database.AppDetailsDao
import cm.aptoide.pt.app_games.installer.database.InstallerDatabase
import cm.aptoide.pt.app_games.installer.notifications.InstallerNotificationsManager
import cm.aptoide.pt.app_games.installer.notifications.RealInstallerNotificationsManager
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.install_manager.environment.NetworkConnection
import cm.aptoide.pt.installer.AptoideDownloader
import cm.aptoide.pt.installer.AptoideInstaller
import cm.aptoide.pt.task_info.AptoideTaskInfoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface InstallerModule {

  companion object {
    @Singleton
    @Provides
    fun provideInstallerNotifications(installerNotificationsManager: RealInstallerNotificationsManager): InstallerNotificationsManager =
      installerNotificationsManager

    @Singleton
    @Provides
    fun provideInstallManager(
      @ApplicationContext appContext: Context,
      taskInfoRepository: AptoideTaskInfoRepository,
      downloader: AptoideDownloader,
      installer: AptoideInstaller,
      networkConnection: NetworkConnection,
    ): InstallManager = InstallManager.with(
      context = appContext,
      taskInfoRepository = taskInfoRepository,
      packageDownloader = downloader,
      packageInstaller = installer,
      networkConnection = networkConnection
    )

    @Singleton
    @Provides
    fun providePackageManager(@ApplicationContext context: Context): PackageManager =
      context.packageManager

    @Provides
    @Singleton
    fun providesUninstallPermissions(installPermissionsImpl: UninstallPermissionsImpl): UninstallPermissions =
      installPermissionsImpl

    @Singleton
    @Provides
    fun provideAppInfoDao(database: InstallerDatabase): AppDetailsDao = database.appInfoDao()

    @Singleton
    @Provides
    fun provideInstalledAppsDatabase(@ApplicationContext appContext: Context): InstallerDatabase =
      Room.databaseBuilder(appContext, InstallerDatabase::class.java, "aptoide_games_installer.db")
        .build()
  }
}
