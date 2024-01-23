package cm.aptoide.pt.payment_method.paypal.presentation

import android.app.Activity
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
import cm.aptoide.pt.payment_manager.transaction.TransactionStatus.COMPLETED
import cm.aptoide.pt.payment_method.paypal.PaypalPaymentMethod
import com.aptoide.pt.payments_prefs.domain.PreSelectedPaymentUseCase
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
  val preSelectedPaymentUseCase: PreSelectedPaymentUseCase,
) : ViewModel()

@Composable
fun rememberPaypalUIState(
  paymentMethodId: String,
): PaypalUIState {
  val viewModelProvider = hiltViewModel<InjectionsProvider>()
  val packageName = LocalContext.current.packageName
  val vm: PaypalViewModel = viewModel(
    key = paymentMethodId,
    factory = object : Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PaypalViewModel(
          paymentManager = viewModelProvider.paymentManager,
          paymentMethodId = paymentMethodId,
          packageName = packageName,
          preSelectedPaymentUseCase = viewModelProvider.preSelectedPaymentUseCase
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState
}

class PaypalViewModel(
  private val packageName: String,
  private val paymentMethodId: String,
  private val paymentManager: PaymentManager,
  private val preSelectedPaymentUseCase: PreSelectedPaymentUseCase,
) : ViewModel() {

  private val viewModelState =
    MutableStateFlow<PaypalUIState>(PaypalUIState.Loading)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      viewModelState.update { PaypalUIState.Loading }

      try {
        val creditCardPaymentMethod =
          paymentManager.getPaymentMethod(paymentMethodId) as PaypalPaymentMethod

        val billingAgreementData = creditCardPaymentMethod.init()

        if (billingAgreementData != null) {
          viewModelState.update {
            PaypalUIState.BillingAgreementAvailable(
              purchaseRequest = creditCardPaymentMethod.purchaseRequest,
              paymentMethodName = creditCardPaymentMethod.label,
              paymentMethodIconUrl = creditCardPaymentMethod.iconUrl,
              onBuyClick = ::makePurchase,
              onRemoveBillingAgreementClick = ::removeBillingAgreement
            )
          }
        } else {
          val billingAgreement = creditCardPaymentMethod.createToken(packageName)
          viewModelState.update {
            PaypalUIState.LaunchWebViewActivity(
              url = billingAgreement.url,
              token = billingAgreement.token,
              onWebViewResult = ::onWebViewResult
            )
          }
        }
      } catch (e: Throwable) {
        viewModelState.update { PaypalUIState.Error }
      }
    }
  }

  private fun removeBillingAgreement() {
    viewModelScope.launch {
      try {
        viewModelState.update { PaypalUIState.Loading }
        val creditCardPaymentMethod =
          paymentManager.getPaymentMethod(paymentMethodId) as PaypalPaymentMethod

        val success = creditCardPaymentMethod.cancelBillingAgreement()

        if (success) {
          viewModelState.update { PaypalUIState.PaypalAgreementRemoved }
        } else {
          viewModelState.update { PaypalUIState.Error }
        }
      } catch (e: Throwable) {
        viewModelState.update { PaypalUIState.Error }
      }
    }
  }

  private fun makePurchase() {
    viewModelScope.launch {
      try {
        viewModelState.update { PaypalUIState.MakingPurchase }
        val paypalPaymentMethod =
          paymentManager.getPaymentMethod(paymentMethodId) as PaypalPaymentMethod

        val transaction = paypalPaymentMethod.createTransaction(Unit)

        transaction.status.collect {
          when (it) {
            COMPLETED -> {
              preSelectedPaymentUseCase.saveLastSuccessfulPaymentMethod(paypalPaymentMethod.id)

              viewModelState.update {
                PaypalUIState.Success(

                  valueInDollars = paypalPaymentMethod.productInfo.priceInDollars,
                  uid = transaction.uid
                )
              }
            }

            else -> viewModelState.update { PaypalUIState.Error }
          }
        }
      } catch (e: Throwable) {
        viewModelState.update { PaypalUIState.Error }
      }
    }
  }

  private fun onWebViewResult(
    token: String,
    resultCode: Int,
  ) {
    viewModelScope.launch {
      try {
        viewModelState.update { PaypalUIState.Loading }
        val paypalPaymentMethod =
          paymentManager.getPaymentMethod(paymentMethodId) as PaypalPaymentMethod
        when (resultCode) {
          Activity.RESULT_OK -> {
            viewModelState.update { PaypalUIState.MakingPurchase }

            paypalPaymentMethod.createBillingAgreement(token)

            val transaction = paypalPaymentMethod.createTransaction(Unit)

            transaction.status.collect {
              when (it) {
                COMPLETED -> {
                  preSelectedPaymentUseCase.saveLastSuccessfulPaymentMethod(paypalPaymentMethod.id)

                  viewModelState.update {
                    PaypalUIState.Success(

                      valueInDollars = paypalPaymentMethod.productInfo.priceInDollars,
                      uid = transaction.uid
                    )
                  }
                }

                else -> viewModelState.update { PaypalUIState.Error }
              }
            }
          }

          Activity.RESULT_CANCELED -> {
            paypalPaymentMethod.cancelToken(token)
            viewModelState.update { PaypalUIState.Canceled }
          }

          else -> {
            paypalPaymentMethod.cancelToken(token)
            viewModelState.update { PaypalUIState.Error }
          }
        }
      } catch (e: Throwable) {
        viewModelState.update { PaypalUIState.Error }
      }
    }
  }
}
