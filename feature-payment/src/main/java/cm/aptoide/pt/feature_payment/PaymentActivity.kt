package cm.aptoide.pt.feature_payment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import cm.aptoide.pt.feature_payment.presentation.PaymentScreen

class PaymentActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      PaymentScreen()
    }
  }
}
