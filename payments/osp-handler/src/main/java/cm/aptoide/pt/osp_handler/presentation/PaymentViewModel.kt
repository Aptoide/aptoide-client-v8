package cm.aptoide.pt.osp_handler.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.payment_manager.manager.PaymentManager
import cm.aptoide.pt.payment_manager.manager.domain.PurchaseRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PaymentViewModel(
  private val purchaseRequest: PurchaseRequest?,
  private val paymentManager: PaymentManager,
) : ViewModel() {

  private val viewModelState = MutableStateFlow("") // TODO change in future tickets

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      try {
        purchaseRequest?.let {
          val paymentMethods = paymentManager.loadPaymentMethods(purchaseRequest)

          viewModelState.update { paymentMethods.toString() } // TODO handle payment methods
        } ?: run {
          viewModelState.update { "Error" } // TODO handle uri null
        }
      } catch (e: Throwable) {
        e.printStackTrace()
        viewModelState.update { "Error" } // TODO handle uri null
      }
    }
  }
}
