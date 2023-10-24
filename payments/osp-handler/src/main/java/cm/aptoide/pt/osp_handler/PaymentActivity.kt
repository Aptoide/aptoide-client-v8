package cm.aptoide.pt.osp_handler

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import cm.aptoide.pt.osp_handler.handler.OSPHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PaymentActivity : AppCompatActivity() {

  private val uri by lazy { intent?.data }

  @Inject
  lateinit var ospHandler: OSPHandler

  @Inject
  lateinit var contentProvider: PaymentScreenContentProvider

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val purchaseRequest = ospHandler.extract(uri)
    setContent {
      contentProvider.content(purchaseRequest)
    }
  }
}
