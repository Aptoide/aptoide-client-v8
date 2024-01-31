package com.appcoins.payment_method.adyen.presentation

import androidx.appcompat.app.AppCompatActivity
import com.adyen.checkout.adyen3ds2.Adyen3DS2Configuration
import com.adyen.checkout.card.CardComponent
import com.adyen.checkout.components.ActionComponentData
import com.adyen.checkout.components.model.payments.response.Action
import com.adyen.checkout.components.model.payments.response.RedirectAction
import com.adyen.checkout.redirect.RedirectConfiguration
import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.payments.arch.PurchaseRequest

sealed class AdyenCreditCardScreenUiState {
  object MakingPurchase : AdyenCreditCardScreenUiState()
  object Loading : AdyenCreditCardScreenUiState()
  data class Error(val error: Throwable) : AdyenCreditCardScreenUiState()
  data class Input(
    val productInfo: ProductInfoData,
    val purchaseRequest: PurchaseRequest,
    val cardComponent: (AppCompatActivity) -> CardComponent,
    val forgetCard: (() -> Unit)?,
  ) : AdyenCreditCardScreenUiState()

  data class Success(
    val packageName: String,
    val valueInDollars: String,
    val uid: String,
  ) : AdyenCreditCardScreenUiState()

  data class Redirect(
    val action: RedirectAction,
    val configuration: RedirectConfiguration,
    val submitActionResult: (ActionComponentData) -> Unit,
  ) : AdyenCreditCardScreenUiState()

  data class ThreeDS2(
    val action: Action,
    val configuration: Adyen3DS2Configuration,
    val submitActionResult: (ActionComponentData) -> Unit,
  ) : AdyenCreditCardScreenUiState()
}
