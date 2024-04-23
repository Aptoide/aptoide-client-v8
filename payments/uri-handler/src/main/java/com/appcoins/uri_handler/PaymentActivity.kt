package com.appcoins.uri_handler

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.appcoins.payments.arch.PaymentsInitializer
import com.appcoins.uri_handler.handler.UriHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PaymentActivity : ComponentActivity() {

  private val uri by lazy { intent?.data }

  @Inject
  lateinit var uriHandler: UriHandler

  @Inject
  lateinit var contentProvider: PaymentScreenContentProvider

  @SuppressLint("SourceLockedOrientationActivity")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
      this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
    } else {
      this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
    }

    if (savedInstanceState == null) {
      logStartEvent(uri)
    }

    val purchaseRequest = runCatching { uriHandler.extract(uri) }
      .onFailure(::logError)
      .getOrNull()

    contentProvider.setContent(this, purchaseRequest) {
      setResult(if (it) RESULT_OK else RESULT_CANCELED)
      finish()
    }
  }
}

private fun logError(throwable: Throwable) = PaymentsInitializer.logger.logError(
  tag = "payments",
  throwable = throwable,
)

private fun logStartEvent(uri: Uri?) = PaymentsInitializer.logger.logEvent(
  tag = "payments",
  message = "payment_flow_start",
  data = mapOf("uri" to uri)
)
