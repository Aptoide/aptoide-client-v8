package com.aptoide.android.aptoidegames.deviceapi.di

import cm.aptoide.pt.device_api.di.DeviceApiRetrofit
import cm.aptoide.pt.feature_categories.data.deviceapi.DeviceApiCategoriesRepository
import cm.aptoide.pt.feature_categories.data.deviceapi.DeviceApiCategoriesService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import javax.inject.Singleton

/** Device-API categories fill-binding — aptoideGamesDev only. */
@Module
@InstallIn(SingletonComponent::class)
object DeviceApiCategoriesBindsModule {

  @Provides
  @Singleton
  fun provideDeviceApiCategoriesRepository(
    @DeviceApiRetrofit retrofit: Retrofit,
  ): DeviceApiCategoriesRepository = DeviceApiCategoriesRepository(
    service = retrofit.create(DeviceApiCategoriesService::class.java),
    scope = CoroutineScope(Dispatchers.IO),
  )
}
