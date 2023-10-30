package cm.aptoide.pt.osp_handler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import cm.aptoide.pt.payment_manager.manager.PurchaseRequest

interface PaymentScreenContentProvider {
  fun handleIntent(context: AppCompatActivity, intent: Intent?)

  val content: @Composable (PurchaseRequest?, (Boolean) -> Unit) -> Unit
}
