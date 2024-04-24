package com.appcoins.payments.methods.paypal.presentation

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class PaypalResultContract : ActivityResultContract<String, Pair<String, Int>>() {
  override fun createIntent(
    context: Context,
    input: String,
  ): Intent {
    return Intent(context, PaypalWebViewActivity::class.java).apply {
      putExtra(PaypalWebViewActivity.EXTRA_URL, input)
    }
  }

  override fun parseResult(
    resultCode: Int,
    intent: Intent?,
  ): Pair<String, Int> {
    val uri = intent?.data
    val baToken = uri?.getQueryParameter("ba_token")
      ?: ""
    return baToken to resultCode
  }
}
