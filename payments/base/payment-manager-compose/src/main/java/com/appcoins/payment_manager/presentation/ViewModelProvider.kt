package com.appcoins.payment_manager.presentation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.appcoins.payment_manager.di.PaymentsModule
import com.appcoins.payment_prefs.di.PaymentPrefsModule
import com.appcoins.payments.arch.PurchaseRequest

@Composable
fun paymentMethodsViewModel(purchaseRequest: PurchaseRequest): PaymentMethodsViewModel {
  return viewModel(
    factory = object : Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PaymentMethodsViewModel(
          purchaseRequest = purchaseRequest,
          paymentManager = PaymentsModule.paymentManager,
          preSelectedPaymentUseCase = PaymentPrefsModule.preSelectedPaymentUseCase
        ) as T
      }
    }
  )
}
