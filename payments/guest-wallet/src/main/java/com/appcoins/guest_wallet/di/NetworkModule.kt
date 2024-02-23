package com.appcoins.guest_wallet.di

import com.appcoins.guest_wallet.repository.WalletRepositoryImpl.WalletApi
import com.appcoins.payments.network.di.BackendHostUrl
import com.appcoins.payments.network.di.BaseOkHttp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
internal class NetworkModule {

  @Provides
  fun provideWalletApi(
    @BackendHostUrl baseUrl: String,
    @BaseOkHttp okHttpClient: OkHttpClient,
  ): WalletApi = Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(WalletApi::class.java)
}
