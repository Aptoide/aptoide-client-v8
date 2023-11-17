package cm.aptoide.pt.payment_method.paypal.presentation

import cm.aptoide.pt.payment_manager.manager.PurchaseRequest

sealed class PaypalScreenUiState {
  data class BillingAgreementAvailable(
    val purchaseRequest: PurchaseRequest,
    val paymentMethodName: String,
    val paymentMethodIconUrl: String,
    val onBuyClick: () -> Unit,
    val onRemoveBillingAgreementClick: () -> Unit,
  ) : PaypalScreenUiState()

  data class LaunchWebViewActivity(
    val url: String,
    val token: String,
    val onWebViewResult: (String, Boolean) -> Unit,
  ) : PaypalScreenUiState()

  object MakingPurchase : PaypalScreenUiState()
  object PaypalAgreementRemoved : PaypalScreenUiState()
  object Loading : PaypalScreenUiState()
  object Error : PaypalScreenUiState()
  data class Success(
    val valueInDollars: String,
    val uid: String,
  ) : PaypalScreenUiState()
}
