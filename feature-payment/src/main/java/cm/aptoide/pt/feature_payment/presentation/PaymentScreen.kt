package cm.aptoide.pt.feature_payment.presentation

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.extensions.PreviewAll
import cm.aptoide.pt.extensions.runPreviewable

@PreviewAll
@Composable
fun PaymentScreenPreview() {
  PaymentScreen()
}

@Composable
fun PaymentScreen() {
  val paymentViewState = paymentViewState()

  Text(text = "Hello Payment", color = Color.White)
}

@Composable
fun paymentViewState(): Int = runPreviewable(
  preview = { 0 },
  real = {
    val paymentViewModel = hiltViewModel<PaymentViewModel>()
    val paymentUiState by paymentViewModel.uiState.collectAsState()
    paymentUiState
  },
)
