package cm.aptoide.pt.payment_method.adyen.presentation

import androidx.appcompat.app.AppCompatActivity
import cm.aptoide.pt.payment_manager.manager.PurchaseRequest
import cm.aptoide.pt.payment_manager.repository.product.domain.ProductInfoData
import com.adyen.checkout.adyen3ds2.Adyen3DS2Configuration
import com.adyen.checkout.card.CardComponent
import com.adyen.checkout.components.ActionComponentData
import com.adyen.checkout.components.model.payments.response.Action
import com.adyen.checkout.components.model.payments.response.RedirectAction
import com.adyen.checkout.redirect.RedirectConfiguration

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
