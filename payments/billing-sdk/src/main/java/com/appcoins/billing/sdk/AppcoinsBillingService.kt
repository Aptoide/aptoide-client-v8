package com.appcoins.billing.sdk

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.appcoins.billing.AppcoinsBilling
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppcoinsBillingService : Service() {

  @Inject
  lateinit var appcoinsBillingBinder: AppcoinsBilling.Stub

  override fun onBind(intent: Intent): IBinder {
    return appcoinsBillingBinder
  }
}
