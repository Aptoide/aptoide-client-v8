package com.aptoide.android.aptoidegames.newservices.di

import cm.aptoide.pt.aptoide_network.data.network.AcceptLanguageInterceptor
import cm.aptoide.pt.aptoide_network.data.network.UserAgentInterceptor
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.device_api.di.DeviceApiDomain
import cm.aptoide.pt.device_api.di.DeviceApiOkHttp
import cm.aptoide.pt.device_api.di.DeviceApiRetrofit
import cm.aptoide.pt.device_api.di.DeviceApiVariant
import cm.aptoide.pt.device_api.network.DeviceProfileProvider
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppsDataSource
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppsService
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.newservices.DEVICE_API_VARIANT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DeviceApiNetworkModule {

  @Provides
  @DeviceApiDomain
  fun provideDeviceApiDomain(): String = BuildConfig.DEVICE_API_DOMAIN

  @Provides
  @DeviceApiVariant
  fun provideDeviceApiVariant(): String = DEVICE_API_VARIANT

  // One client for everything on the device API host, so the catalog reads and the inline
  // install configuration share a connection pool. It carries none of the v7 query
  // parameters - the device API takes the device profile explicitly.
  @DeviceApiOkHttp
  @Provides
  @Singleton
  fun provideDeviceApiOkHttpClient(
    userAgentInterceptor: UserAgentInterceptor,
    acceptLanguageInterceptor: AcceptLanguageInterceptor,
  ): OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(userAgentInterceptor)
    .addInterceptor(
      HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
          HttpLoggingInterceptor.Level.BODY
        } else {
          HttpLoggingInterceptor.Level.NONE
        }
      }
    )
    .addInterceptor(acceptLanguageInterceptor)
    .build()

  @DeviceApiRetrofit
  @Provides
  @Singleton
  fun provideDeviceApiRetrofit(
    @DeviceApiOkHttp okHttpClient: OkHttpClient,
    @DeviceApiDomain domain: String,
  ): Retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .baseUrl(domain)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

  // Shared by the Play build's catalog and the direct build's apkfy fallback
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
}
