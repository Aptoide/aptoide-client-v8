package cm.aptoide.pt.feature_payment.presentation

import android.net.Uri
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import cm.aptoide.pt.extensions.PreviewAll
import cm.aptoide.pt.extensions.runPreviewable

@PreviewAll
@Composable
fun PaymentScreenPreview() {
  PaymentScreen(
    Uri.parse(
      "https://apichain.dev.catappult.io/transaction/inapp?product=gas&domain=com.appcoins.trivialdrivesample.test&callback_url=https%3A%2F%2Fwww.mygamestudio.com%2FcompletePurchase%3FuserId%3D1234&signature=76a21fc764668b1e31e13c7cb98f2768ab52b3415ba4cd3c6455d223cc3fdaa0"
    )
  )
}

@Composable
fun PaymentScreen(uri: Uri?) {
  val paymentViewState = paymentViewState(uri)

  Text(text = paymentViewState, color = Color.Red)
}

@Composable
fun paymentViewState(uri: Uri?): String = runPreviewable(
  preview = { "Preview" },
  real = {
    val paymentViewModel = paymentViewModel(uri = uri)
    val paymentUiState by paymentViewModel.uiState.collectAsState()
    paymentUiState
  },
)
