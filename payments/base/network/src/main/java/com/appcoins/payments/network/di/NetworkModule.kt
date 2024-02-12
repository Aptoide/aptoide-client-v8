package com.appcoins.payments.network.di

import cm.aptoide.pt.aptoide_network.data.network.AcceptLanguageInterceptor
import cm.aptoide.pt.aptoide_network.data.network.QLogicInterceptor
import cm.aptoide.pt.aptoide_network.data.network.UserAgentInterceptor
import cm.aptoide.pt.aptoide_network.data.network.VersionCodeInterceptor
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

  @PaymentsBaseOkHttp
  @Provides
  @Singleton
  fun providePaymentsBaseOkHttpClient(
    userAgentInterceptor: UserAgentInterceptor,
    qLogicInterceptor: QLogicInterceptor,
    versionCodeInterceptor: VersionCodeInterceptor,
    languageInterceptor: AcceptLanguageInterceptor,
    httpLoggingInterceptor: HttpLoggingInterceptor,
  ): OkHttpClient =
    OkHttpClient.Builder()
      .addInterceptor(userAgentInterceptor)
      .addInterceptor(qLogicInterceptor)
      .addInterceptor(versionCodeInterceptor)
      .addInterceptor(languageInterceptor)
      .addInterceptor(httpLoggingInterceptor)
      .build()

  @BrokerOkHttp
  @Provides
  @Singleton
  fun provideBrokerOkHttpClient(
    userAgentInterceptor: UserAgentInterceptor,
    qLogicInterceptor: QLogicInterceptor,
    versionCodeInterceptor: VersionCodeInterceptor,
    languageInterceptor: AcceptLanguageInterceptor,
    httpLoggingInterceptor: HttpLoggingInterceptor,
  ): OkHttpClient =
    OkHttpClient.Builder()
      .addInterceptor(userAgentInterceptor)
      .addInterceptor(qLogicInterceptor)
      .addInterceptor(versionCodeInterceptor)
      .addInterceptor(languageInterceptor)
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
