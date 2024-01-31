package com.appcoins.payment_manager.presentation

import com.appcoins.payments.arch.PaymentMethod

sealed class PaymentMethodsUiState {
  data class Idle(
    val paymentMethods: List<PaymentMethod<*>>,
  ) : PaymentMethodsUiState()

  data class PreSelected(
    val preSelectedPaymentMethod: PaymentMethod<*>,
    val paymentMethods: List<PaymentMethod<*>>,
  ) : PaymentMethodsUiState()

  object Loading : PaymentMethodsUiState()

  object LoadingSkeleton : PaymentMethodsUiState()

  object NoConnection : PaymentMethodsUiState()
  object Error : PaymentMethodsUiState()
}
