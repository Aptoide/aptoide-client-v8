package cm.aptoide.pt.feature_payment.presentation

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cm.aptoide.pt.extensions.PreviewAll

@PreviewAll
@Composable
fun PaymentScreenPreview() {
  PaymentScreen()
}

@Composable
fun PaymentScreen() {
  Text(text = "Hello Payment", color = Color.White)
}
