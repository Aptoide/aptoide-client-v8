package com.appcoins.payment_method.paypal.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.appcoins.payment_method.paypal.PaypalPaymentMethod
import com.appcoins.payments.di.Payments
import com.appcoins.payments.di.paymentManager

@Composable
fun rememberPaypalUIState(
  paymentMethodId: String,
): PaypalUIState {
  val packageName = LocalContext.current.packageName
  val uiLogic = viewModel<PaypalViewModel>(
    key = paymentMethodId,
    factory = object : Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PaypalViewModel(
          paymentMethod = Payments.paymentManager.getPaymentMethod(paymentMethodId) as PaypalPaymentMethod,
          packageName = packageName,
          logger = Payments.logger,
        ) as T
      }
    }
  )

  val uiState by uiLogic.uiState.collectAsState()

  return uiState
}
