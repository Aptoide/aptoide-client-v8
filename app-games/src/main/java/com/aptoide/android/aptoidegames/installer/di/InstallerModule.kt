package com.aptoide.android.aptoidegames.installer.di

import android.content.Context
import android.content.pm.PackageManager
import androidx.room.Room
import com.aptoide.android.aptoidegames.installer.database.AppDetailsDao
import com.aptoide.android.aptoidegames.installer.database.InstallerDatabase
import com.aptoide.android.aptoidegames.installer.notifications.InstallerNotificationsManager
import com.aptoide.android.aptoidegames.installer.notifications.RealInstallerNotificationsManager
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
