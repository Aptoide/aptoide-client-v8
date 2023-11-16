package cm.aptoide.pt.payment_manager.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.payment_manager.manager.PaymentManager
import cm.aptoide.pt.payment_manager.manager.PurchaseRequest
import cm.aptoide.pt.payment_manager.presentation.PaymentMethodsUiState.Loading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class PaymentMethodsViewModel(
  private val purchaseRequest: PurchaseRequest,
  private val paymentManager: PaymentManager,
) : ViewModel() {

  private val viewModelState =
    MutableStateFlow<PaymentMethodsUiState>(Loading)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    reload()
  }

  fun reload() {
    viewModelScope.launch {
      try {
        val paymentMethods = paymentManager.loadPaymentMethods(purchaseRequest)
        viewModelState.update {
          PaymentMethodsUiState.Idle(
            paymentMethods = paymentMethods
          )
        }
      } catch (e: Throwable) {
        e.printStackTrace()
        if (e is IOException) {
          viewModelState.update { PaymentMethodsUiState.NoConnection }
        } else {
          viewModelState.update { PaymentMethodsUiState.Error }
        }
      }
    }
  }
}
