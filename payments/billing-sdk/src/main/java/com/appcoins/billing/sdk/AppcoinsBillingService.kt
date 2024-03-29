package com.appcoins.billing.sdk

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.appcoins.payments.arch.PaymentsInitializer
import com.appcoins.product_inventory.di.ProductModule

class AppcoinsBillingService : Service() {

  override fun onBind(intent: Intent): IBinder {
    return AppcoinsBillingBinder.with(
      context = applicationContext,
      packageManager = applicationContext.packageManager,
      productInventoryRepository = ProductModule.productInventoryRepository,
      walletProvider = PaymentsInitializer.walletProvider,
      logger = PaymentsInitializer.logger,
    )
  }
}
