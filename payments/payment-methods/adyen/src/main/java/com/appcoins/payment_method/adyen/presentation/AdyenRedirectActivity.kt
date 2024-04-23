package com.appcoins.payment_method.adyen.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.adyen.checkout.components.model.payments.response.RedirectAction
import com.adyen.checkout.redirect.RedirectComponent
import com.appcoins.payment_method.adyen.di.redirectConfiguration
import com.appcoins.payments.arch.PaymentsInitializer

class AdyenRedirectActivity : ComponentActivity() {

  companion object {
    const val WRONG_URL = 2
  }

  private val logger = PaymentsInitializer.logger

  @Suppress("DEPRECATION")
  private val action by lazy {
    intent.getParcelableExtra<RedirectAction>(AdyenActionResolveContract.REDIRECT_ACTION)!!
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
      RedirectComponent.PROVIDER.get(
        this,
        application,
        PaymentsInitializer.redirectConfiguration
      ).run {
        removeObservers(this@AdyenRedirectActivity)
        observe(this@AdyenRedirectActivity) {
          setResult(
            RESULT_OK,
            Intent().putExtra(AdyenActionResolveContract.ACTION_COMPONENT_DATA, it)
          )
          finish()
        }
        handleAction(this@AdyenRedirectActivity, action)
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

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    val data = intent?.data
    if (data != null && data.toString()
        .startsWith(AdyenActionResolveContract.ADYEN_CHECKOUT_SCHEME)
    ) {
      RedirectComponent.PROVIDER.get(
        this,
        application,
        PaymentsInitializer.redirectConfiguration
      ).run {
        handleIntent(intent)
        removeObservers(this@AdyenRedirectActivity)
      }
    }
  }
}
