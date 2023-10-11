package cm.aptoide.pt.payment_method.adyen.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.payment_manager.manager.PaymentManager
import cm.aptoide.pt.payment_method.adyen.credit_card.CreditCardPaymentMethod
import cm.aptoide.pt.payment_method.adyen.presentation.AdyenCreditCardScreenUiState.Error
import cm.aptoide.pt.payment_method.adyen.presentation.AdyenCreditCardScreenUiState.Loading
import cm.aptoide.pt.payment_method.adyen.presentation.AdyenCreditCardScreenUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val paymentManager: PaymentManager,
) : ViewModel()

@Composable
fun adyenCreditCardViewModel(
  paymentMethodId: String,
): AdyenCreditCardScreenUiState {
  val viewModelProvider = hiltViewModel<InjectionsProvider>()
  val vm: AdyenCreditCardViewModel = viewModel(
    key = paymentMethodId,
    factory = object : Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AdyenCreditCardViewModel(
          paymentMethodId = paymentMethodId,
          paymentManager = viewModelProvider.paymentManager,
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState
}

class AdyenCreditCardViewModel(
  private val paymentMethodId: String,
  private val paymentManager: PaymentManager,
) : ViewModel() {

  private val viewModelState = MutableStateFlow<AdyenCreditCardScreenUiState>(Loading)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      viewModelState.update { Loading }

      try {
        val creditCardPaymentMethod = paymentManager.getPaymentMethod(paymentMethodId)

        if (creditCardPaymentMethod is CreditCardPaymentMethod) {

          val json = creditCardPaymentMethod.init()

          viewModelState.update {
            Success(
              creditCardPaymentMethod.productInfo, creditCardPaymentMethod.purchaseRequest, json
            )
          }
        } else {
          viewModelState.update { Error(RuntimeException("PaymentMethod is not credit card")) }
        }
      } catch (e: Throwable) {
        viewModelState.update { Error(e) }
      }
    }
  }
}
