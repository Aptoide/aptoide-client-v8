package com.appcoins.payments.di

import com.appcoins.payments.products.ProductsRepository
import com.appcoins.payments.products.ProductsRepositoryImpl

val Payments.productsRepository: ProductsRepository by lazyInit {
  ProductsRepositoryImpl(
    microServicesRestClient
  )
}
