package com.appcoins.payment_manager.di

import com.appcoins.payment_manager.repository.broker.PaymentsRepositoryImpl.BrokerApi
import com.appcoins.payments.network.di.HighTimeoutOkHttp
import com.appcoins.payments.network.di.MicroServicesHostUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class NetworkModule {

  @Singleton
  @Provides
  fun provideBrokerApi(
    @MicroServicesHostUrl baseUrl: String,
    @HighTimeoutOkHttp okHttpClient: OkHttpClient,
  ): BrokerApi = Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(BrokerApi::class.java)
}
