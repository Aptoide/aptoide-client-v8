package com.appcoins.payments.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  @PaymentsBaseOkHttp
  @Provides
  @Singleton
  fun providePaymentsBaseOkHttpClient(
    @UserAgentInterceptor userAgentInterceptor: Interceptor,
    httpLoggingInterceptor: HttpLoggingInterceptor,
  ): OkHttpClient =
    OkHttpClient.Builder()
      .addInterceptor(userAgentInterceptor)
      .addInterceptor(httpLoggingInterceptor)
      .build()

  @BrokerOkHttp
  @Provides
  @Singleton
  fun provideBrokerOkHttpClient(
    @UserAgentInterceptor userAgentInterceptor: Interceptor,
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
annotation class PaymentsBaseOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BrokerOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserAgentInterceptor
