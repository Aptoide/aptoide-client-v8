package com.aptoide.android.aptoidegames.apkfy.di

import cm.aptoide.pt.feature_apkfy.domain.ApkfyManager
import cm.aptoide.pt.feature_apkfy.domain.ApkfyManagerImpl
import com.aptoide.android.aptoidegames.IdsRepository
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
    idsRepository: IdsRepository
  ): ApkfyManager =
    ApkfyManagerProbe(
      apkfyManager = apkfyManager,
      apkfyAnalytics = apkfyAnalytics,
      idsRepository = idsRepository
    )

  @Singleton
  @Provides
  fun provideDownloadPermissionStateProbe(
    downloaderSelector: DownloaderSelector,
  ): DownloadPermissionStateProbe = DownloadPermissionStateProbe(
    packageDownloader = downloaderSelector,
  )
}
