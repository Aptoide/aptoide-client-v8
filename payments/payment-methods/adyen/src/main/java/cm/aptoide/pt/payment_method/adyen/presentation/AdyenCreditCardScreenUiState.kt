package cm.aptoide.pt.payment_method.adyen.presentation

import cm.aptoide.pt.payment_manager.manager.domain.PurchaseRequest
import cm.aptoide.pt.payment_manager.repository.product.domain.ProductInfoData
import org.json.JSONObject

sealed class AdyenCreditCardScreenUiState {
  object Loading : AdyenCreditCardScreenUiState()
  data class Error(val error: Throwable) : AdyenCreditCardScreenUiState()
  data class Success(
    val productInfo: ProductInfoData,
    val purchaseRequest: PurchaseRequest,
    val jsonObject: JSONObject
  ) : AdyenCreditCardScreenUiState()
}
