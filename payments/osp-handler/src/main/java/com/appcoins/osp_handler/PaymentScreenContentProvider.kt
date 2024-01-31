package com.appcoins.osp_handler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import com.appcoins.payments.arch.PurchaseRequest

interface PaymentScreenContentProvider {
  fun handleIntent(context: AppCompatActivity, intent: Intent?)

  val content: @Composable (PurchaseRequest?, (Boolean) -> Unit) -> Unit
}
