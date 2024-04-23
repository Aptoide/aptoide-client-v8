package com.appcoins.payment_method.adyen.presentation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adyen.checkout.card.CardComponentState
import com.appcoins.payment_manager.di.PaymentsModule
import com.appcoins.payment_method.adyen.CreditCardPaymentMethod
import com.appcoins.payment_method.adyen.di.cardConfiguration
import com.appcoins.payments.arch.PaymentsInitializer

@Composable
fun rememberAdyenCreditCardUIState(
  paymentMethodId: String,
): Pair<AdyenCreditCardUiState, (CardComponentState) -> Unit> {
  val context = LocalContext.current as ComponentActivity

  val uiLogic = viewModel<AdyenCreditCardViewModel>(
    key = paymentMethodId,
    factory = object : Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AdyenCreditCardViewModel(
          paymentMethod = PaymentsModule.paymentManager.getPaymentMethod(paymentMethodId) as CreditCardPaymentMethod,
          cardConfiguration = PaymentsInitializer.cardConfiguration,
          logger = PaymentsInitializer.logger,
        ) as T
      }
    }
  )

  val uiState by uiLogic.uiState.collectAsState()

  DisposableEffect(Unit) {
    onDispose {
      uiLogic.clearAdyenComponents(context)
    }
  }

  return uiState to uiLogic::buy
}
