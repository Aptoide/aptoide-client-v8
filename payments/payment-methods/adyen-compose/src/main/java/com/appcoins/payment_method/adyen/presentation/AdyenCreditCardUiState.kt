package com.appcoins.payment_method.adyen.presentation

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultRegistry
import com.adyen.checkout.card.CardComponent
import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.payments.arch.PurchaseRequest

sealed class AdyenCreditCardUiState {
  object MakingPurchase : AdyenCreditCardUiState()
  object Loading : AdyenCreditCardUiState()
  data class Error(val error: Throwable) : AdyenCreditCardUiState()
  data class Input(
    val productInfo: ProductInfoData,
    val purchaseRequest: PurchaseRequest,
    val cardComponent: (ComponentActivity) -> CardComponent,
    val forgetCard: (() -> Unit)?,
  ) : AdyenCreditCardUiState()

  data class Success(val packageName: String) : AdyenCreditCardUiState()

  data class UserAction(val resolveWith: (ActivityResultRegistry) -> Unit) :
    AdyenCreditCardUiState()
}
