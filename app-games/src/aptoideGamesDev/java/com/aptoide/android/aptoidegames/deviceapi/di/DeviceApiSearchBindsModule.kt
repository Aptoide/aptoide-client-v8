package com.aptoide.android.aptoidegames.deviceapi.di

import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.device_api.di.DeviceApiRetrofit
import cm.aptoide.pt.device_api.di.DeviceApiVariant
import cm.aptoide.pt.device_api.network.DeviceProfileProvider
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppsService
import cm.aptoide.pt.feature_search.data.database.SearchHistoryRepository
import cm.aptoide.pt.feature_search.data.deviceapi.DeviceApiSearchRepository
import cm.aptoide.pt.feature_search.data.deviceapi.DeviceApiSuggestService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/** Device-API search fill-binding — aptoideGamesDev only. */
@Module
@InstallIn(SingletonComponent::class)
object DeviceApiSearchBindsModule {

  @Provides
  @Singleton
  fun provideDeviceApiSearchRepository(
    @DeviceApiRetrofit retrofit: Retrofit,
    searchHistoryRepository: SearchHistoryRepository,
    @StoreName storeName: String,
    @DeviceApiVariant variant: String,
    deviceProfileProvider: DeviceProfileProvider,
  ): DeviceApiSearchRepository = DeviceApiSearchRepository(
    appsService = retrofit.create(DeviceApiAppsService::class.java),
    suggestService = retrofit.create(DeviceApiSuggestService::class.java),
    searchHistoryRepository = searchHistoryRepository,
    storeName = storeName,
    variant = variant,
    deviceProfileProvider = deviceProfileProvider,
  )
}
