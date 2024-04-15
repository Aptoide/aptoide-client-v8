package com.appcoins.payment_manager.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.payments.arch.PurchaseRequest
import com.appcoins.payments.di.Payments
import com.appcoins.payments.di.paymentManager

@Composable
fun paymentMethodsViewModel(purchaseRequest: PurchaseRequest): Pair<PaymentMethodsUiState, () -> Unit> {
  val vw = viewModel<PaymentMethodsViewModel>(
    key = purchaseRequest.hashCode().toString(),
    factory = object : Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PaymentMethodsViewModel(
          purchaseRequest = purchaseRequest,
          paymentManager = Payments.paymentManager,
          logger = Payments.logger
        ) as T
      }
    }
  )

  val uiState by vw.uiState.collectAsState()

  return uiState to vw::reload
}

@Composable
fun rememberProductInfo(): ProductInfoData? {
  val vw = viewModel<ProductInfoViewModel>(
    factory = object : Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ProductInfoViewModel(paymentManager = Payments.paymentManager) as T
      }
    }
  )
  val uiState by vw.uiState.collectAsState()
  return uiState
}
