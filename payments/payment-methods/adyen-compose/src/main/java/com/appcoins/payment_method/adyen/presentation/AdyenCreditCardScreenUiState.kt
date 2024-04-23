package com.appcoins.payment_method.adyen.presentation

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultRegistry
import com.adyen.checkout.card.CardComponent
import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.payments.arch.PurchaseRequest

sealed class AdyenCreditCardScreenUiState {
  object MakingPurchase : AdyenCreditCardScreenUiState()
  object Loading : AdyenCreditCardScreenUiState()
  data class Error(val error: Throwable) : AdyenCreditCardScreenUiState()
  data class Input(
    val productInfo: ProductInfoData,
    val purchaseRequest: PurchaseRequest,
    val cardComponent: (ComponentActivity) -> CardComponent,
    val forgetCard: (() -> Unit)?,
  ) : AdyenCreditCardScreenUiState()

  data class Success(val packageName: String) : AdyenCreditCardScreenUiState()

  data class UserAction(val resolveWith: (ActivityResultRegistry) -> Unit) :
    AdyenCreditCardScreenUiState()
}
