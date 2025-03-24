package com.aptoide.android.aptoidegames.installer.di

import android.content.Context
import android.content.pm.PackageManager
import androidx.room.Room
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.download_view.di.UIInstallPackageInfoMapper
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.install_info_mapper.domain.CachingInstallPackageInfoMapper
import cm.aptoide.pt.install_info_mapper.domain.InstallPackageInfoMapper
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.install_manager.environment.NetworkConnection
import cm.aptoide.pt.installer.AptoideDownloader
import cm.aptoide.pt.installer.AptoideInstallPackageInfoMapper
import cm.aptoide.pt.installer.AptoideInstaller
import cm.aptoide.pt.installer.LegacyInstaller
import cm.aptoide.pt.installer.obb.OBBInstallManager
import cm.aptoide.pt.task_info.AptoideTaskInfoRepository
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.apkfy.DownloadPermissionStateProbe
import com.aptoide.android.aptoidegames.installer.DownloaderSelector
import com.aptoide.android.aptoidegames.installer.InstallerSelector
import com.aptoide.android.aptoidegames.installer.analytics.AnalyticsInstallPackageInfoMapper
import com.aptoide.android.aptoidegames.installer.analytics.DownloadProbe
import com.aptoide.android.aptoidegames.installer.analytics.InstallAnalytics
import com.aptoide.android.aptoidegames.installer.analytics.InstallAnalyticsImpl
import com.aptoide.android.aptoidegames.installer.analytics.InstallProbe
import com.aptoide.android.aptoidegames.installer.analytics.SilentInstallChecker
import com.aptoide.android.aptoidegames.installer.analytics.SilentInstallCheckerImpl
import com.aptoide.android.aptoidegames.installer.database.AppDetailsDao
import com.aptoide.android.aptoidegames.installer.database.InstallerDatabase
import com.aptoide.android.aptoidegames.installer.database.InstallerDatabase.FirstMigration
import com.aptoide.android.aptoidegames.installer.notifications.InstallerNotificationsManager
import com.aptoide.android.aptoidegames.installer.notifications.RealInstallerNotificationsManager
import com.aptoide.android.aptoidegames.installer.task_info.AptoideTaskInfoProbe
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class InstallerModule {

  @Singleton
  @Provides
  fun provideInstallerNotifications(installerNotificationsManager: RealInstallerNotificationsManager): InstallerNotificationsManager =
    installerNotificationsManager

  @Singleton
  @Provides
  fun provideInstallManager(
    @ApplicationContext appContext: Context,
    taskInfoRepository: AptoideTaskInfoRepository,
    downloadPermissionStateProbe: DownloadPermissionStateProbe,
    installerSelector: InstallerSelector,
    installAnalytics: InstallAnalytics,
    networkConnection: NetworkConnection,
    silentInstallChecker: SilentInstallChecker,
  ): InstallManager = OBBInstallManager(
    context = appContext,
    installManager = InstallManager.with(
      context = appContext,
      taskInfoRepository = AptoideTaskInfoProbe(
        taskInfoRepository = taskInfoRepository,
        installAnalytics = installAnalytics
      ),
      packageDownloader = DownloadProbe(
        packageDownloader = downloadPermissionStateProbe,
        analytics = installAnalytics,
      ),
      packageInstaller = InstallProbe(
        packageInstaller = installerSelector,
        analytics = installAnalytics,
      ),
      networkConnection = networkConnection
    )
  ).also {
    // TODO: Resolve this architecturally
    (silentInstallChecker as? SilentInstallCheckerImpl)?.installManager = it
  }

  @Singleton
  @Provides
  fun provideDownloaderSelector(
    featureFlags: FeatureFlags,
    aptoideDownloader: AptoideDownloader,
  ): DownloaderSelector = DownloaderSelector(
    featureFlags = featureFlags,
    aptoidePackageDownloader = aptoideDownloader,
  )

  @Singleton
  @Provides
  fun provideInstallerSelector(
    aptoideInstaller: AptoideInstaller,
    legacyInstaller: LegacyInstaller
  ): InstallerSelector = InstallerSelector(
    aptoideInstaller = aptoideInstaller,
    legacyInstaller = legacyInstaller,
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
  fun provideSilentInstallChecker(): SilentInstallChecker = SilentInstallCheckerImpl()

  @Singleton
  @Provides
  fun providePayloadMapper(
    installPackageInfoMapper: AptoideInstallPackageInfoMapper,
    silentInstallChecker: SilentInstallChecker,
  ): InstallPackageInfoMapper = AnalyticsInstallPackageInfoMapper(
    mapper = CachingInstallPackageInfoMapper(
      installPackageInfoMapper = installPackageInfoMapper
    ),
    silentInstallChecker = silentInstallChecker,
    isForUI = false
  )

  @Singleton
  @Provides
  @UIInstallPackageInfoMapper
  fun provideUIPayloadMapper(
    installPackageInfoMapper: AptoideInstallPackageInfoMapper,
    silentInstallChecker: SilentInstallChecker,
  ): InstallPackageInfoMapper = AnalyticsInstallPackageInfoMapper(
    mapper = CachingInstallPackageInfoMapper(
      installPackageInfoMapper = installPackageInfoMapper
    ),
    silentInstallChecker = silentInstallChecker,
    isForUI = true
  )

  @Singleton
  @Provides
  fun providesInstallAnalytics(
    @ApplicationContext context: Context,
    featureFlags: FeatureFlags,
    genericAnalytics: GenericAnalytics,
    biAnalytics: BIAnalytics,
    @StoreName storeName: String,
    silentInstallChecker: SilentInstallChecker,
  ): InstallAnalytics = InstallAnalyticsImpl(
    context = context,
    featureFlags = featureFlags,
    genericAnalytics = genericAnalytics,
    biAnalytics = biAnalytics,
    storeName = storeName,
    silentInstallChecker = silentInstallChecker
  )
}
