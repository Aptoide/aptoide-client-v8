package com.appcoins.product_inventory.di

import com.appcoins.payments.arch.GetUserAgent
import com.appcoins.payments.network.RestClient
import com.appcoins.payments.network.di.MicroServicesHostUrl
import com.appcoins.product_inventory.ProductInventoryRepository
import com.appcoins.product_inventory.ProductInventoryRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class ProductModule {

  @Singleton
  @Provides
  fun provideProductRepository(
    @MicroServicesHostUrl baseUrl: String,
    getUserAgent: GetUserAgent,
  ): ProductInventoryRepository = ProductInventoryRepositoryImpl(
    RestClient.with(
      baseUrl = baseUrl,
      getUserAgent = getUserAgent,
    )
  )
}
