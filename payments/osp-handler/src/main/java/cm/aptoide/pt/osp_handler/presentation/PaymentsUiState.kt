package cm.aptoide.pt.osp_handler.presentation

import cm.aptoide.pt.payment_manager.payment.PaymentMethod

sealed class PaymentsUiState {
  data class OnBuyerAppLoaded(val buyingPackage: String) : PaymentsUiState()
  data class Idle(
    val paymentMethods: List<PaymentMethod<*>>,
    val buyingPackage: String,
    val gameItemValue: String?,
    val purchaseValue: Int?
  ) : PaymentsUiState()

  object Loading : PaymentsUiState()
  object NoConnection : PaymentsUiState()
  object Error : PaymentsUiState()
}