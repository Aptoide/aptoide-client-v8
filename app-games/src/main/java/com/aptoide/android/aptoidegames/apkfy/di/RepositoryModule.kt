package com.aptoide.android.aptoidegames.apkfy.di

import cm.aptoide.pt.feature_apkfy.domain.ApkfyFilter
import cm.aptoide.pt.feature_apkfy.domain.ApkfyManager
import cm.aptoide.pt.feature_apkfy.domain.ApkfyManagerImpl
import com.aptoide.android.aptoidegames.LocalIdsRepository
import com.aptoide.android.aptoidegames.apkfy.AGApkfyFilter
import com.aptoide.android.aptoidegames.apkfy.ApkfySessionPreferences
import com.aptoide.android.aptoidegames.apkfy.DownloadPermissionStateProbe
import com.aptoide.android.aptoidegames.apkfy.analytics.ApkfyAnalytics
import com.aptoide.android.aptoidegames.apkfy.analytics.ApkfyManagerProbe
import com.aptoide.android.aptoidegames.installer.DownloaderSelector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  @Provides
  @Singleton
  fun provideApkfyManager(
    apkfyManager: ApkfyManagerImpl,
    apkfyAnalytics: ApkfyAnalytics,
    idsRepository: LocalIdsRepository,
    apkfySessionPreferences: ApkfySessionPreferences,
  ): ApkfyManager =
    ApkfyManagerProbe(
      apkfyManager = apkfyManager,
      apkfyAnalytics = apkfyAnalytics,
      idsRepository = idsRepository,
      apkfySessionPreferences = apkfySessionPreferences,
    )

  @Singleton
  @Provides
  fun provideDownloadPermissionStateProbe(
    downloaderSelector: DownloaderSelector,
  ): DownloadPermissionStateProbe = DownloadPermissionStateProbe(
    packageDownloader = downloaderSelector,
  )

  @Provides
  @Singleton
  fun provideApkfyFilter(): ApkfyFilter = AGApkfyFilter()
}
