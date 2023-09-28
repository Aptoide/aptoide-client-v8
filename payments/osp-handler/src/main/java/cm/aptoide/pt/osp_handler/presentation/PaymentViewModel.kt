package cm.aptoide.pt.osp_handler.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.osp_handler.handler.OSPHandler
import cm.aptoide.pt.osp_handler.presentation.PaymentsUiState.Loading
import cm.aptoide.pt.payment_manager.manager.PaymentManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PaymentViewModel(
  private val uri: Uri?,
  private val ospHandler: OSPHandler,
  private val paymentManager: PaymentManager,
) : ViewModel() {

  private val viewModelState =
    MutableStateFlow<PaymentsUiState>(Loading)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      try {
        val purchaseRequest = ospHandler.extract(uri)
        purchaseRequest?.let {
          viewModelState.update {
            PaymentsUiState.OnBuyerAppLoaded(buyingPackage = purchaseRequest.domain)
          }
        }

        purchaseRequest?.let {
          val paymentMethods = paymentManager.loadPaymentMethods(purchaseRequest)
          val gameItemValue = paymentMethods.first().getProductInfo().title
          viewModelState.update {
            PaymentsUiState.Idle(
              paymentMethods,
              purchaseRequest.domain,
              gameItemValue,
              purchaseRequest.value
            )
          }
        } ?: run {
          viewModelState.update { PaymentsUiState.Error } // TODO handle uri null
        }
      } catch (e: Throwable) {
        e.printStackTrace()
        viewModelState.update { PaymentsUiState.Error } // TODO handle uri null
      }
    }
  }
}
