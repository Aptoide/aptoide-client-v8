package com.aptoide.android.aptoidegames.deviceapi.di

import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.device_api.di.DeviceApiRetrofit
import cm.aptoide.pt.device_api.di.DeviceApiVariant
import cm.aptoide.pt.device_api.network.DeviceProfileProvider
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppRepository
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppsListRepository
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppsService
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiReviewsRepository
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiSplitsRepository
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceHomeAppsCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Fill-bindings for the device-API repositories — **aptoideGamesDev only**.
 * Providing these concrete types makes the feature modules' `Optional<DeviceApi…>`
 * present, so their `@Provides` select the device impl over v7. Absent in every
 * other variant ⇒ those keep v7.
 */
@Module
@InstallIn(SingletonComponent::class)
object DeviceApiBindsModule {

  @Provides
  @Singleton
  fun provideDeviceApiAppRepository(
    @DeviceApiRetrofit retrofit: Retrofit,
    @StoreName storeName: String,
    deviceProfileProvider: DeviceProfileProvider,
  ): DeviceApiAppRepository = DeviceApiAppRepository(
    service = retrofit.create(DeviceApiAppsService::class.java),
    storeName = storeName,
    deviceProfileProvider = deviceProfileProvider,
    scope = CoroutineScope(Dispatchers.IO),
  )

  @Provides
  @Singleton
  fun provideDeviceApiAppsListRepository(
    @DeviceApiRetrofit retrofit: Retrofit,
    @StoreName storeName: String,
    @DeviceApiVariant variant: String,
    deviceProfileProvider: DeviceProfileProvider,
    homeAppsCache: DeviceHomeAppsCache,
  ): DeviceApiAppsListRepository = DeviceApiAppsListRepository(
    service = retrofit.create(DeviceApiAppsService::class.java),
    storeName = storeName,
    variant = variant,
    deviceProfileProvider = deviceProfileProvider,
    homeAppsCache = homeAppsCache,
    scope = CoroutineScope(Dispatchers.IO),
  )

  @Provides
  @Singleton
  fun provideDeviceApiSplitsRepository(): DeviceApiSplitsRepository = DeviceApiSplitsRepository()

  @Provides
  @Singleton
  fun provideDeviceApiReviewsRepository(
    @DeviceApiRetrofit retrofit: Retrofit,
  ): DeviceApiReviewsRepository = DeviceApiReviewsRepository(
    service = retrofit.create(DeviceApiReviewsRepository.Service::class.java),
    scope = CoroutineScope(Dispatchers.IO),
  )
}
