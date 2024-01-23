package cm.aptoide.pt.payment_method.paypal.presentation

import cm.aptoide.pt.payment_manager.manager.PurchaseRequest

sealed class PaypalUIState {
  data class BillingAgreementAvailable(
    val purchaseRequest: PurchaseRequest,
    val paymentMethodName: String,
    val paymentMethodIconUrl: String,
    val onBuyClick: () -> Unit,
    val onRemoveBillingAgreementClick: () -> Unit,
  ) : PaypalUIState()

  data class LaunchWebViewActivity(
    val url: String,
    val token: String,
    val onWebViewResult: (String, Boolean) -> Unit,
  ) : PaypalUIState()

  object MakingPurchase : PaypalUIState()
  object PaypalAgreementRemoved : PaypalUIState()
  object Loading : PaypalUIState()
  object Error : PaypalUIState()
  data class Success(
    val valueInDollars: String,
    val uid: String,
  ) : PaypalUIState()
}
