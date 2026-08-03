package com.aptoide.android.aptoidegames.deviceapi.di

import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.device_api.di.DeviceApiRetrofit
import cm.aptoide.pt.device_api.di.DeviceApiVariant
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceHomeAppsCache
import cm.aptoide.pt.feature_home.data.deviceapi.DeviceApiWidgetsRepository
import cm.aptoide.pt.feature_home.data.deviceapi.DeviceApiWidgetsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import javax.inject.Singleton

/** Device-API home fill-binding — aptoideGamesDev only. */
@Module
@InstallIn(SingletonComponent::class)
object DeviceApiHomeBindsModule {

  @Provides
  @Singleton
  fun provideDeviceApiWidgetsRepository(
    @DeviceApiRetrofit retrofit: Retrofit,
    @DeviceApiVariant variant: String,
    @StoreName storeName: String,
    homeAppsCache: DeviceHomeAppsCache,
  ): DeviceApiWidgetsRepository = DeviceApiWidgetsRepository(
    service = retrofit.create(DeviceApiWidgetsService::class.java),
    variant = variant,
    storeName = storeName,
    homeAppsCache = homeAppsCache,
    scope = CoroutineScope(Dispatchers.IO),
  )
}
