package com.appcoins.osp_handler

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.appcoins.osp_handler.handler.OSPHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PaymentActivity : AppCompatActivity() {

  private val uri by lazy { intent?.data }

  @Inject
  lateinit var ospHandler: OSPHandler

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

    val purchaseRequest = ospHandler.extract(uri)
    setContent {
      contentProvider.content(purchaseRequest) {
        setResult(if (it) RESULT_OK else RESULT_CANCELED)
        finish()
      }
    }
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    handleIntent(intent)
  }

  private fun handleIntent(intent: Intent?) {
    contentProvider.handleIntent(this, intent)
  }
}
