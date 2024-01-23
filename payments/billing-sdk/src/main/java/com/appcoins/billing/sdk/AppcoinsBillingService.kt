package com.appcoins.billing.sdk

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppcoinsBillingService : Service() {

  @Inject
  lateinit var appcoinsBillingBinder: AppcoinsBillingBinder

  override fun onBind(intent: Intent): IBinder {
    return appcoinsBillingBinder
  }
}
