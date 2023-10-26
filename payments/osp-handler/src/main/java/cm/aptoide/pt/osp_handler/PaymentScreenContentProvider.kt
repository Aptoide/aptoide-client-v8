package cm.aptoide.pt.osp_handler

import androidx.compose.runtime.Composable
import cm.aptoide.pt.payment_manager.manager.PurchaseRequest

interface PaymentScreenContentProvider {
  val content: @Composable (PurchaseRequest?, () -> Unit) -> Unit
}
