package com.appcoins.product_inventory.di

import com.appcoins.product_inventory.ProductInventoryRepository
import com.appcoins.product_inventory.ProductInventoryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface PaymentModule {

  @Singleton
  @Binds
  fun bindProductRepository(productRepository: ProductInventoryRepositoryImpl): ProductInventoryRepository
}
