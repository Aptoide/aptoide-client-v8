package com.appcoins.product_inventory.di

import com.appcoins.payments.network.di.BrokerOkHttp
import com.appcoins.product_inventory.ProductInventoryRepositoryImpl.ProductInventoryApi
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
  @RetrofitAPICatappult
  fun provideRetrofitAPIChain(
    @APICatappultUrl baseUrl: String,
    @BrokerOkHttp okHttpClient: OkHttpClient,
  ): Retrofit =
    Retrofit.Builder()
      .baseUrl(baseUrl)
      .client(okHttpClient)
      .addConverterFactory(GsonConverterFactory.create())
      .build()

  @Singleton
  @Provides
  fun provideProductApi(@RetrofitAPICatappult retrofit: Retrofit): ProductInventoryApi =
    retrofit.create(ProductInventoryApi::class.java)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class APICatappultUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitAPICatappult
