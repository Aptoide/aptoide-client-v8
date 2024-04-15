package com.appcoins.payments.di

import android.content.Context
import android.os.IBinder
import com.appcoins.billing.sdk.AppcoinsBillingBinder
import com.appcoins.billing.sdk.BillingErrorMapperImpl
import com.appcoins.billing.sdk.BillingSupportMapper
import com.appcoins.billing.sdk.sku_details.ProductSerializerImpl

fun Payments.getAppcoinsBillingBinder(context: Context): IBinder = AppcoinsBillingBinder(
  context = context,
  packageManager = context.packageManager,
  productInventoryRepository = productInventoryRepository,
  billingErrorMapper = BillingErrorMapperImpl(),
  productSerializer = ProductSerializerImpl(),
  walletProvider = walletProvider,
  billingSupportMapper = BillingSupportMapper(),
  logger = logger,
)
