package com.appcoins.billing.sdk

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AppcoinsBillingService : Service() {

  override fun onBind(intent: Intent): IBinder {
    return AppcoinsBillingBinder()
  }
}
