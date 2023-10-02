package cm.aptoide.pt.osp_handler.presentation

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import cm.aptoide.pt.extensions.PreviewAll
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.payment_manager.manager.domain.PurchaseRequest

@PreviewAll
@Composable
fun PaymentScreenPreview() {
  PaymentScreen(
    /*
      https://apichain.dev.catappult.io/transaction/inapp?product=gas&domain=com.appcoins.trivialdrivesample.test&callback_url=https%3A%2F%2Fwww.mygamestudio.com%2FcompletePurchase%3FuserId%3D1234&signature=76a21fc764668b1e31e13c7cb98f2768ab52b3415ba4cd3c6455d223cc3fdaa0
    )*/
    PurchaseRequest(
      scheme = "https",
      host = "apichain.dev.catappult.io",
      path = "transaction/inapp",
      product = "gas",
      domain = "com.appcoins.trivialdrivesample.test",
      callbackUrl = "https%3A%2F%2Fwww.mygamestudio.com%2FcompletePurchase%3FuserId%3D1234",
      orderReference = null,
      signature = "76a21fc764668b1e31e13c7cb98f2768ab52b3415ba4cd3c6455d223cc3fdaa0",
      value = null,
      currency = null,
    )
  )
}

@Composable
fun PaymentScreen(purchaseRequest: PurchaseRequest?) {
  val paymentViewState = paymentViewState(purchaseRequest)

  Text(text = paymentViewState, color = Color.Red)
}

@Composable
fun paymentViewState(purchaseRequest: PurchaseRequest?): String = runPreviewable(
  preview = { "Preview" },
  real = {
    val paymentViewModel = paymentViewModel(purchaseRequest = purchaseRequest)
    val paymentUiState by paymentViewModel.uiState.collectAsState()
    paymentUiState
  },
)
