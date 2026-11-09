package com.aptoide.android.aptoidegames.ads.di

import com.aptoide.android.aptoidegames.ads.AdGeoProvider
import com.aptoide.android.aptoidegames.ads.DeviceAdGeoProvider
import com.aptoide.android.aptoidegames.ads.native_ads.MaxNativeAdLoaderFactory
import com.aptoide.android.aptoidegames.ads.native_ads.NativeAdLoaderFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AdsModule {

  @Binds
  abstract fun bindAdGeoProvider(impl: DeviceAdGeoProvider): AdGeoProvider

  @Binds
  abstract fun bindNativeAdLoaderFactory(impl: MaxNativeAdLoaderFactory): NativeAdLoaderFactory
}
