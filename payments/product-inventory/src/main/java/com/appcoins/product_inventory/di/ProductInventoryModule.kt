package com.appcoins.product_inventory.di

import com.appcoins.payments.network.di.NetworkModule
import com.appcoins.product_inventory.ProductInventoryRepository
import com.appcoins.product_inventory.ProductInventoryRepositoryImpl

object ProductModule {

  val productInventoryRepository: ProductInventoryRepository by lazy {
    ProductInventoryRepositoryImpl(
      NetworkModule.microServicesRestClient
    )
  }
}
