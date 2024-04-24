package com.appcoins.payments.manager.presentation

import com.appcoins.payments.arch.PaymentMethod

sealed class PaymentMethodsUiState {
  data class Idle(
    val paymentMethods: List<PaymentMethod<*>>,
  ) : PaymentMethodsUiState()

  object Loading : PaymentMethodsUiState()

  object NoConnection : PaymentMethodsUiState()
  object Error : PaymentMethodsUiState()
}
