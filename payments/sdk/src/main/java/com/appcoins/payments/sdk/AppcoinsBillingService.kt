package com.appcoins.payments.sdk

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.appcoins.payments.di.Payments
import com.appcoins.payments.di.getAppcoinsBillingBinder

class AppcoinsBillingService : Service() {

  override fun onBind(intent: Intent): IBinder =
    Payments.getAppcoinsBillingBinder(applicationContext)
}
