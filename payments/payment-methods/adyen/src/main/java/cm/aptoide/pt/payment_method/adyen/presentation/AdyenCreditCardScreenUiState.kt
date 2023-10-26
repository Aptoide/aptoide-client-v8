package cm.aptoide.pt.payment_method.adyen.presentation

import cm.aptoide.pt.payment_manager.manager.PurchaseRequest
import cm.aptoide.pt.payment_manager.repository.product.domain.ProductInfoData
import com.adyen.checkout.card.CardComponent

sealed class AdyenCreditCardScreenUiState {
  object Loading : AdyenCreditCardScreenUiState()
  data class Error(val error: Throwable) : AdyenCreditCardScreenUiState()
  data class Input(
    val productInfo: ProductInfoData,
    val purchaseRequest: PurchaseRequest,
    val cardComponent: CardComponent,
  ) : AdyenCreditCardScreenUiState()
  object Success : AdyenCreditCardScreenUiState()
}
