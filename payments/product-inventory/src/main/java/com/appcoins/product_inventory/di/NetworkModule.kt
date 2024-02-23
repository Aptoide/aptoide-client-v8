package com.appcoins.product_inventory.di

import com.appcoins.payments.network.di.HighTimeoutOkHttp
import com.appcoins.payments.network.di.MicroServicesHostUrl
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
    @MicroServicesHostUrl baseUrl: String,
    @HighTimeoutOkHttp okHttpClient: OkHttpClient,
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
annotation class RetrofitAPICatappult
