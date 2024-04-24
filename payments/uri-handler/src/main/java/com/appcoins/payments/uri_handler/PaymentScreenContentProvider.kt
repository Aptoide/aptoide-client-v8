package com.appcoins.payments.uri_handler

import androidx.activity.ComponentActivity
import com.appcoins.payments.arch.PurchaseRequest

interface PaymentScreenContentProvider {
  val setContent: (context: ComponentActivity, purchaseRequest: PurchaseRequest?, (Boolean) -> Unit) -> Unit
}
