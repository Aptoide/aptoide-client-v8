package com.aptoide.android.aptoidegames.apkfy.di

import cm.aptoide.pt.feature_apkfy.domain.ApkfyCatalogFallback
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppsDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Aptoide Games off Google Play resolves apkfy titles v7 does not know in the device API, so
 * the cohort titles Aptoide never distributed can still be shown and installed. Vanilla and
 * the Play build bind no fallback and keep resolving through v7 alone.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object ApkfyFallbackModule {

  @Provides
  fun provideApkfyCatalogFallback(dataSource: DeviceApiAppsDataSource): ApkfyCatalogFallback =
    ApkfyCatalogFallback(dataSource::detail)
}
