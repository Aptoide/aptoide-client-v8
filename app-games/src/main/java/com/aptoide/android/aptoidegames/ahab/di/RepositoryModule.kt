package com.aptoide.android.aptoidegames.ahab.di

import cm.aptoide.pt.aptoide_network.data.network.AcceptLanguageInterceptor
import cm.aptoide.pt.aptoide_network.data.network.UserAgentInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

  @AhabSimpleOkHttp
  @Provides
  @Singleton
  fun provideAhabOkHttpClient(
    userAgentInterceptor: UserAgentInterceptor,
    acceptLanguageInterceptor: AcceptLanguageInterceptor,
  ): OkHttpClient =
    OkHttpClient.Builder()
      .addInterceptor(interceptor = userAgentInterceptor)
      .addInterceptor(
        interceptor = HttpLoggingInterceptor()
          .apply { level = HttpLoggingInterceptor.Level.BODY }
      )
      .addInterceptor(acceptLanguageInterceptor)
      .build()

  @RetrofitAhab
  @Provides
  @Singleton
  fun provideRetrofitAhab(
    @AhabSimpleOkHttp okHttpClient: OkHttpClient,
    @AhabDomain domain: String,
  ): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(domain)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AhabDomain

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AhabSimpleOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitAhab
