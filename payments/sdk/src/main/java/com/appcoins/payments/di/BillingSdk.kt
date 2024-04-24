package com.appcoins.payments.di

import android.content.Context
import android.os.IBinder
import com.appcoins.payments.sdk.AppcoinsBillingBinder
import com.appcoins.payments.sdk.BillingErrorMapperImpl
import com.appcoins.payments.sdk.BillingSupportMapper
import com.appcoins.payments.sdk.sku_details.ProductSerializerImpl

fun Payments.getAppcoinsBillingBinder(context: Context): IBinder = AppcoinsBillingBinder(
  context = context,
  packageManager = context.packageManager,
  productsRepository = productsRepository,
  billingErrorMapper = BillingErrorMapperImpl(),
  productSerializer = ProductSerializerImpl(),
  walletProvider = walletProvider,
  billingSupportMapper = BillingSupportMapper(),
  logger = logger,
)
