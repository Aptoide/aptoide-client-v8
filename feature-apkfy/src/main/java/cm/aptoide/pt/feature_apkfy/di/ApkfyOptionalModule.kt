package cm.aptoide.pt.feature_apkfy.di

import cm.aptoide.pt.feature_apkfy.domain.ApkfyCatalogFallback
import cm.aptoide.pt.feature_apkfy.domain.ApkfyResolutionReporter
import dagger.BindsOptionalOf
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * What a build may add to apkfy. One that binds no fallback resolves through v7 alone, and one
 * that binds no reporter reports nothing.
 */
@Module
@InstallIn(SingletonComponent::class)
internal interface ApkfyOptionalModule {

  @BindsOptionalOf
  fun catalogFallback(): ApkfyCatalogFallback

  @BindsOptionalOf
  fun resolutionReporter(): ApkfyResolutionReporter
}
