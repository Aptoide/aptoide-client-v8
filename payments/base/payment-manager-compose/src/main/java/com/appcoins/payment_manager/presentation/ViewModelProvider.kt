package com.appcoins.payment_manager.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.appcoins.payment_manager.di.PaymentsModule
import com.appcoins.payments.arch.PaymentsInitializer
import com.appcoins.payments.arch.PurchaseRequest

@Composable
fun paymentMethodsViewModel(purchaseRequest: PurchaseRequest): Pair<PaymentMethodsUiState, () -> Unit> {
  val vm = viewModel<PaymentMethodsViewModel>(
    factory = object : Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PaymentMethodsViewModel(
          purchaseRequest = purchaseRequest,
          paymentManager = PaymentsModule.paymentManager,
          logger = PaymentsInitializer.logger
        ) as T
      }
    }
  )

  val uiState by vm.uiState.collectAsState()

  return uiState to vm::reload
}
