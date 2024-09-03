package com.aptoide.android.aptoidegames.installer.di

import android.content.Context
import android.content.pm.PackageManager
import androidx.room.Room
import cm.aptoide.pt.install_info_mapper.domain.CachingInstallPackageInfoMapper
import cm.aptoide.pt.install_info_mapper.domain.InstallPackageInfoMapper
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.install_manager.environment.NetworkConnection
import cm.aptoide.pt.installer.AptoideDownloader
import cm.aptoide.pt.installer.AptoideInstallPackageInfoMapper
import cm.aptoide.pt.installer.AptoideInstaller
import cm.aptoide.pt.task_info.AptoideTaskInfoRepository
import com.aptoide.android.aptoidegames.installer.analytics.AnalyticsInstallPackageInfoMapper
import com.aptoide.android.aptoidegames.installer.analytics.DownloadProbe
import com.aptoide.android.aptoidegames.installer.analytics.InstallAnalytics
import com.aptoide.android.aptoidegames.installer.analytics.InstallProbe
import com.aptoide.android.aptoidegames.installer.database.AppDetailsDao
import com.aptoide.android.aptoidegames.installer.database.InstallerDatabase
import com.aptoide.android.aptoidegames.installer.database.InstallerDatabase.FirstMigration
import com.aptoide.android.aptoidegames.installer.notifications.InstallerNotificationsManager
import com.aptoide.android.aptoidegames.installer.notifications.RealInstallerNotificationsManager
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
      installAnalytics: InstallAnalytics,
      networkConnection: NetworkConnection,
    ): InstallManager = InstallManager.with(
      context = appContext,
      taskInfoRepository = taskInfoRepository,
      packageDownloader = DownloadProbe(
        packageDownloader = downloader,
        analytics = installAnalytics,
      ),
      packageInstaller = InstallProbe(
        packageInstaller = installer,
        analytics = installAnalytics,
      ),
      networkConnection = networkConnection
    )

    @Singleton
    @Provides
    fun providePackageManager(@ApplicationContext context: Context): PackageManager =
      context.packageManager

    @Singleton
    @Provides
    fun provideAppInfoDao(database: InstallerDatabase): AppDetailsDao = database.appInfoDao()

    @Singleton
    @Provides
    fun provideInstalledAppsDatabase(@ApplicationContext appContext: Context): InstallerDatabase =
      Room.databaseBuilder(appContext, InstallerDatabase::class.java, "aptoide_games_installer.db")
        .addMigrations(FirstMigration())
        .build()

    @Singleton
    @Provides
    fun providePayloadMapper(
      installPackageInfoMapper: AptoideInstallPackageInfoMapper,
    ): InstallPackageInfoMapper =
      AnalyticsInstallPackageInfoMapper(
        CachingInstallPackageInfoMapper(
          installPackageInfoMapper
        )
      )
  }
}
