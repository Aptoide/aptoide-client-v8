package com.appcoins.uri_handler

import android.content.Intent
import androidx.activity.ComponentActivity
import com.appcoins.payments.arch.PurchaseRequest

interface PaymentScreenContentProvider {
  fun handleIntent(context: ComponentActivity, intent: Intent?)

  val setContent: (context: ComponentActivity, purchaseRequest: PurchaseRequest?, (Boolean) -> Unit) -> Unit
}
