package com.appcoins.payments.network.di

import com.appcoins.payments.network.UserAgentInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  @BaseOkHttp
  @Provides
  @Singleton
  fun providePaymentsBaseOkHttpClient(
    userAgentInterceptor: UserAgentInterceptor,
    httpLoggingInterceptor: HttpLoggingInterceptor,
  ): OkHttpClient =
    OkHttpClient.Builder()
      .addInterceptor(userAgentInterceptor)
      .addInterceptor(httpLoggingInterceptor)
      .build()

  @HighTimeoutOkHttp
  @Provides
  @Singleton
  fun provideBrokerOkHttpClient(
    userAgentInterceptor: UserAgentInterceptor,
    httpLoggingInterceptor: HttpLoggingInterceptor,
  ): OkHttpClient =
    OkHttpClient.Builder()
      .addInterceptor(userAgentInterceptor)
      .addInterceptor(httpLoggingInterceptor)
      .readTimeout(30, SECONDS)
      .writeTimeout(30, SECONDS)
      .build()
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MicroServicesHostUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BackendHostUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HighTimeoutOkHttp
