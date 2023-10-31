package cm.aptoide.pt.payment_manager.presentation

import cm.aptoide.pt.payment_manager.payment.PaymentMethod

sealed class PaymentMethodsUiState {
  data class Idle(
    val paymentMethods: List<PaymentMethod<*>>,
    val gameItemValue: String,
    val sku: String,
    val price: String,
    val currency: String,
  ) : PaymentMethodsUiState()

  object Loading : PaymentMethodsUiState()
  object NoConnection : PaymentMethodsUiState()
  object Error : PaymentMethodsUiState()
}
