package com.aptoide.android.aptoidegames.apkfy.di

import cm.aptoide.pt.feature_apkfy.domain.ApkfyManager
import cm.aptoide.pt.feature_apkfy.domain.ApkfyManagerImpl
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.apkfy.analytics.ApkfyManagerProbe
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
    biAnalytics: BIAnalytics,
  ): ApkfyManager =
    ApkfyManagerProbe(
      apkfyManager = apkfyManager,
      biAnalytics = biAnalytics
    )
}
