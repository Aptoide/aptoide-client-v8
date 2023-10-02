package cm.aptoide.pt.osp_handler

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import cm.aptoide.pt.osp_handler.handler.OSPHandler
import cm.aptoide.pt.osp_handler.presentation.PaymentScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PaymentActivity : AppCompatActivity() {

  @Inject
  lateinit var ospHandler: OSPHandler

  private val purchaseRequest by lazy { ospHandler.extract(intent?.data) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      PaymentScreen(purchaseRequest)
    }
  }
}
