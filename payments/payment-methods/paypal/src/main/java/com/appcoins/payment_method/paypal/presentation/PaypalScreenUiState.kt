package com.appcoins.payment_method.paypal.presentation

import com.appcoins.payments.arch.PurchaseRequest

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
