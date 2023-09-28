package cm.aptoide.pt.payment_manager.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.payment_manager.manager.PaymentManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PaymentViewModel(
  private val uri: Uri?,
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
        uri?.let {
          val paymentMethods = paymentManager.loadPaymentMethods(uri)

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
