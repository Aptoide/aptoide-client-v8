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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class NetworkModule {

  @Singleton
  @Provides
  fun provideProductApi(
    @MicroServicesHostUrl baseUrl: String,
    @HighTimeoutOkHttp okHttpClient: OkHttpClient,
  ): ProductInventoryApi = Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(ProductInventoryApi::class.java)
}
