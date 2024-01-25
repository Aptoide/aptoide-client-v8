package com.appcoins.payment_manager.di

import cm.aptoide.pt.aptoide_network.di.BaseOkHttp
import com.appcoins.payment_manager.repository.broker.BrokerRepositoryImpl.BrokerApi
import com.appcoins.payment_manager.repository.developer_wallet.DeveloperWalletRepositoryImpl.DeveloperWalletApi
import com.appcoins.payment_manager.repository.product.ProductRepositoryImpl.ProductApi
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
  @RetrofitDeveloperWallet
  fun provideRetrofitDeveloperWalletRetrofit(
    @APIDeveloperWallet baseUrl: String,
    @BaseOkHttp client: OkHttpClient
  ): Retrofit {
    return Retrofit.Builder()
      .baseUrl(baseUrl)
      .client(client)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  @Singleton
  @Provides
  @RetrofitAPICatappult
  fun provideRetrofitAPIChain(
    @APICatappultUrl baseUrl: String,
    @BaseOkHttp okHttpClient: OkHttpClient,
  ): Retrofit =
    Retrofit.Builder()
      .baseUrl(baseUrl)
      .client(okHttpClient)
      .addConverterFactory(GsonConverterFactory.create())
      .build()

  @Singleton
  @Provides
  fun provideProductApi(@RetrofitAPICatappult retrofit: Retrofit): ProductApi =
    retrofit.create(ProductApi::class.java)

  @Singleton
  @Provides
  fun provideBrokerApi(@RetrofitAPICatappult retrofit: Retrofit): BrokerApi =
    retrofit.create(BrokerApi::class.java)

  @Singleton
  @Provides
  fun provideDeveloperWalletApi(@RetrofitDeveloperWallet retrofit: Retrofit): DeveloperWalletApi =
    retrofit.create(DeveloperWalletApi::class.java)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class APIDeveloperWallet

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class APICatappultUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitAPICatappult

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitDeveloperWallet
