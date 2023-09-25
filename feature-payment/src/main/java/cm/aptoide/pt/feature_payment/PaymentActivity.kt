package cm.aptoide.pt.feature_payment

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import cm.aptoide.pt.feature_payment.presentation.PaymentScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentActivity : AppCompatActivity() {

  private val uri by lazy { intent?.data }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      PaymentScreen(uri)
    }
  }
}
