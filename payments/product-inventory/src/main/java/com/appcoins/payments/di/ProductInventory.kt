package com.appcoins.payments.di

import com.appcoins.product_inventory.ProductInventoryRepository
import com.appcoins.product_inventory.ProductInventoryRepositoryImpl

val Payments.productInventoryRepository: ProductInventoryRepository by lazyInit {
  ProductInventoryRepositoryImpl(
    microServicesRestClient
  )
}
