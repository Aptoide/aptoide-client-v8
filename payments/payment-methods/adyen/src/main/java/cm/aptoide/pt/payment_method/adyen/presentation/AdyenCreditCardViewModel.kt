package cm.aptoide.pt.payment_method.adyen.presentation

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.payment_manager.manager.PaymentManager
import cm.aptoide.pt.payment_method.adyen.CreditCardPaymentMethod
import cm.aptoide.pt.payment_method.adyen.di.AdyenKey
import cm.aptoide.pt.payment_method.adyen.presentation.AdyenCreditCardScreenUiState.Error
import cm.aptoide.pt.payment_method.adyen.presentation.AdyenCreditCardScreenUiState.Input
import cm.aptoide.pt.payment_method.adyen.presentation.AdyenCreditCardScreenUiState.Loading
import cm.aptoide.pt.payment_method.adyen.presentation.AdyenCreditCardScreenUiState.Success
import com.adyen.checkout.card.CardComponent
import com.adyen.checkout.card.CardComponentState
import com.adyen.checkout.card.CardConfiguration
import com.adyen.checkout.components.model.PaymentMethodsApiResponse
import com.adyen.checkout.redirect.RedirectComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  @AdyenKey val adyenKey: String,
  val paymentManager: PaymentManager,
) : ViewModel()

@Composable
fun adyenCreditCardViewModel(
  paymentMethodId: String,
): Pair<AdyenCreditCardScreenUiState, (CardComponentState) -> Unit> {
  val viewModelProvider = hiltViewModel<InjectionsProvider>()
  val activity = LocalContext.current as? AppCompatActivity
  val vm: AdyenCreditCardViewModel = viewModel(
    key = paymentMethodId,
    factory = object : Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AdyenCreditCardViewModel(
          paymentMethodId = paymentMethodId,
          adyenKey = viewModelProvider.adyenKey,
          paymentManager = viewModelProvider.paymentManager,
        ).apply {
          load(activity)
        } as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState to vm::buy
}

class AdyenCreditCardViewModel(
  private val paymentMethodId: String,
  private val adyenKey: String,
  private val paymentManager: PaymentManager,
) : ViewModel() {

  private val viewModelState = MutableStateFlow<AdyenCreditCardScreenUiState>(Loading)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  private lateinit var returnUrl: String

  fun load(activity: AppCompatActivity?) {
    viewModelScope.launch {
      viewModelState.update { Loading }

      try {
        if (activity == null) throw IllegalStateException("No AppCompatActivity found")
        returnUrl = RedirectComponent.getReturnUrl(activity)
        val creditCardPaymentMethod = paymentManager.getPaymentMethod(paymentMethodId)

        if (creditCardPaymentMethod is CreditCardPaymentMethod) {

          val json = creditCardPaymentMethod.init()

          val paymentMethodsApiResponse = PaymentMethodsApiResponse.SERIALIZER.deserialize(json)

          val cardConfiguration = CardConfiguration.Builder(activity, adyenKey).build()

          val cardComponent =
            paymentMethodsApiResponse.paymentMethods
              ?.first { pm -> pm.type == "scheme" }
              ?.let { CardComponent.PROVIDER.get(activity, it, cardConfiguration) }
              ?: throw IllegalStateException("CardComponent not found")

          viewModelState.update {
            Input(
              productInfo = creditCardPaymentMethod.productInfo,
              purchaseRequest = creditCardPaymentMethod.purchaseRequest,
              cardComponent = cardComponent
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

  fun buy(cardState: CardComponentState) {
    viewModelScope.launch {
      viewModelState.update { Loading }
      cardState.data.paymentMethod
        ?.let {
          try {
            (paymentManager.getPaymentMethod(paymentMethodId) as? CreditCardPaymentMethod)
              ?.createTransaction(paymentDetails = returnUrl to it)
              ?.let {
                viewModelState.update { Success }
              }
              ?: throw Exception("Failed to create transaction")
          } catch (e: Throwable) {
            viewModelState.update { Error(e) }
          }
        }
        ?: viewModelState.update { Error(IllegalArgumentException("Wrong input")) }
    }
  }
}
