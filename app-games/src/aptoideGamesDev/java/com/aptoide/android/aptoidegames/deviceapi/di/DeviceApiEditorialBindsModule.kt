package com.aptoide.android.aptoidegames.deviceapi.di

import cm.aptoide.pt.device_api.di.DeviceApiRetrofit
import cm.aptoide.pt.feature_editorial.data.deviceapi.DeviceApiEditorialRepository
import cm.aptoide.pt.feature_editorial.data.deviceapi.DeviceApiEditorialService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/** Device-API editorial fill-binding — aptoideGamesDev only. */
@Module
@InstallIn(SingletonComponent::class)
object DeviceApiEditorialBindsModule {

  @Provides
  @Singleton
  fun provideDeviceApiEditorialRepository(
    @DeviceApiRetrofit retrofit: Retrofit,
  ): DeviceApiEditorialRepository = DeviceApiEditorialRepository(
    service = retrofit.create(DeviceApiEditorialService::class.java),
  )
}
