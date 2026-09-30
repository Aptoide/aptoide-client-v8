package com.aptoide.android.aptoidegames.newservices.di

import cm.aptoide.pt.aptoide_network.di.BackendOverride
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.aptoide_network.di.V7Backend
import cm.aptoide.pt.device_api.di.DeviceApiRetrofit
import cm.aptoide.pt.device_api.di.DeviceApiVariant
import cm.aptoide.pt.device_api.network.DeviceProfileProvider
import cm.aptoide.pt.feature_apps.data.AppRepository
import cm.aptoide.pt.feature_apps.data.AppsListRepository
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppsDataSource
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppsService
import cm.aptoide.pt.feature_categories.data.CategoriesRepository
import cm.aptoide.pt.feature_categories.data.deviceapi.DeviceApiCategoriesDataSource
import cm.aptoide.pt.feature_categories.data.deviceapi.DeviceApiCategoriesService
import cm.aptoide.pt.feature_search.data.deviceapi.DeviceApiSuggestDataSource
import cm.aptoide.pt.feature_search.data.deviceapi.DeviceApiSuggestService
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.newservices.PlayAppRepository
import com.aptoide.android.aptoidegames.newservices.PlayAppsListRepository
import com.aptoide.android.aptoidegames.newservices.PlayCategoriesRepository
import com.aptoide.android.aptoidegames.newservices.PlaySearchRepository
import com.aptoide.android.aptoidegames.newservices.selectBackend
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Backs the repositories of the Play build with the new services, behind the build switch.
 * Shared modules declare what can be replaced, and only this build replaces it.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object NewServicesModule {

  @Provides
  @Singleton
  @BackendOverride
  fun provideAppsListRepository(
    @V7Backend v7: AppsListRepository,
    newServices: Provider<PlayAppsListRepository>,
  ): AppsListRepository = selectBackend(BuildConfig.NEW_SERVICES_ENABLED, v7) {
    newServices.get()
  }

  @Provides
  @Singleton
  @BackendOverride
  fun provideAppRepository(
    @V7Backend v7: AppRepository,
    newServices: Provider<PlayAppRepository>,
  ): AppRepository = selectBackend(BuildConfig.NEW_SERVICES_ENABLED, v7) {
    newServices.get()
  }

  @Provides
  @Singleton
  fun providePlayAppRepository(
    @V7Backend v7: AppRepository,
    newServices: DeviceApiAppsDataSource,
    @StoreName storeName: String,
  ): PlayAppRepository = PlayAppRepository(v7, newServices, storeName)

  @Provides
  @Singleton
  @BackendOverride
  fun provideCategoriesRepository(
    @V7Backend v7: CategoriesRepository,
    newServices: Provider<PlayCategoriesRepository>,
  ): CategoriesRepository = selectBackend(BuildConfig.NEW_SERVICES_ENABLED, v7) {
    newServices.get()
  }

  @Provides
  @Singleton
  @BackendOverride
  fun provideSearchRepository(
    @V7Backend v7: SearchRepository,
    newServices: Provider<PlaySearchRepository>,
  ): SearchRepository = selectBackend(BuildConfig.NEW_SERVICES_ENABLED, v7) {
    newServices.get()
  }

  @Provides
  @Singleton
  fun provideDeviceApiAppsDataSource(
    @DeviceApiRetrofit retrofit: Retrofit,
    @DeviceApiVariant variant: String,
    @StoreName storeName: String,
    deviceProfileProvider: DeviceProfileProvider,
  ): DeviceApiAppsDataSource = DeviceApiAppsDataSource(
    service = retrofit.create(DeviceApiAppsService::class.java),
    variant = variant,
    storeName = storeName,
    deviceProfile = deviceProfileProvider::get,
    dispatcher = Dispatchers.IO,
  )

  @Provides
  @Singleton
  fun provideDeviceApiCategoriesDataSource(
    @DeviceApiRetrofit retrofit: Retrofit,
    @DeviceApiVariant variant: String,
  ): DeviceApiCategoriesDataSource = DeviceApiCategoriesDataSource(
    service = retrofit.create(DeviceApiCategoriesService::class.java),
    variant = variant,
    dispatcher = Dispatchers.IO,
  )

  @Provides
  @Singleton
  fun provideDeviceApiSuggestDataSource(
    @DeviceApiRetrofit retrofit: Retrofit,
    @DeviceApiVariant variant: String,
  ): DeviceApiSuggestDataSource = DeviceApiSuggestDataSource(
    service = retrofit.create(DeviceApiSuggestService::class.java),
    variant = variant,
    dispatcher = Dispatchers.IO,
  )
}
