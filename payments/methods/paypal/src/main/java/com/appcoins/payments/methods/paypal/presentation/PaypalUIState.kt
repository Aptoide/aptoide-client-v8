package com.appcoins.payments.methods.paypal.presentation

import com.appcoins.payments.arch.PurchaseRequest

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
    val onWebViewResult: (String, Int) -> Unit,
  ) : PaypalUIState()

  object MakingPurchase : PaypalUIState()
  object PaypalAgreementRemoved : PaypalUIState()
  object Loading : PaypalUIState()
  object Error : PaypalUIState()
  object NoConnection : PaypalUIState()
  object Canceled : PaypalUIState()
  object Success : PaypalUIState()
}
