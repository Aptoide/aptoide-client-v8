package com.appcoins.payment_method.adyen.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.adyen.checkout.adyen3ds2.Adyen3DS2Component
import com.adyen.checkout.components.model.payments.response.Threeds2Action
import com.appcoins.payments.di.Payments
import com.appcoins.payments.di.threeDS2Configuration

class Adyen3DS2Activity : ComponentActivity() {

  companion object {
    const val WRONG_URL = 2
  }

  private val logger = Payments.logger

  @Suppress("DEPRECATION")
  private val action by lazy {
    intent.getParcelableExtra<Threeds2Action>(AdyenActionResolveContract.REDIRECT_ACTION)!!
  }

  @SuppressLint("SourceLockedOrientationActivity")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
      this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
    } else {
      this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
    }

    try {
      Adyen3DS2Component.PROVIDER.get(
        this,
        application,
        Payments.threeDS2Configuration
      ).run {
        removeObservers(this@Adyen3DS2Activity)
        observe(this@Adyen3DS2Activity) {
          setResult(
            RESULT_OK,
            Intent().putExtra(AdyenActionResolveContract.ACTION_COMPONENT_DATA, it)
          )
          finish()
        }
        handleAction(this@Adyen3DS2Activity, action)
      }
    } catch (e: Throwable) {
      logger.logError("adyen", e)
      setResult(WRONG_URL)
      finish()
    }
  }

  @Suppress("DEPRECATION")
  @Deprecated("Deprecated in Java")
  override fun onBackPressed() {
    setResult(RESULT_CANCELED)
    super.onBackPressed()
  }
}
