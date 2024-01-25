package com.appcoins.payment_method.paypal.presentation

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class PaypalResultContract : ActivityResultContract<String, Pair<String, Boolean>>() {
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
  ): Pair<String, Boolean> {
    val success = (resultCode == RESULT_OK)
    val uri = intent?.data
    val baToken = uri?.getQueryParameter("ba_token")
      ?: ""
    return baToken to success
  }
}
