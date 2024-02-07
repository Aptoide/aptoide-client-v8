package com.appcoins.payment_manager.di

import cm.aptoide.pt.aptoide_network.di.BaseOkHttp
import com.appcoins.payment_manager.repository.broker.BrokerRepositoryImpl.BrokerApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class NetworkModule {
  @Singleton
  @Provides
  @RetrofitAPIBroker
  fun provideRetrofitAPIChain(
    @APIBrokerUrl baseUrl: String,
    @BaseOkHttp okHttpClient: OkHttpClient,
  ): Retrofit =
    Retrofit.Builder()
      .baseUrl(baseUrl)
      .client(okHttpClient)
      .addConverterFactory(GsonConverterFactory.create())
      .build()

  @Singleton
  @Provides
  fun provideBrokerApi(@RetrofitAPIBroker retrofit: Retrofit): BrokerApi =
    retrofit.create(BrokerApi::class.java)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class APIBrokerUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitAPIBroker
