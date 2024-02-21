package com.appcoins.payment_method.paypal.presentation

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
import com.appcoins.payment_manager.manager.PaymentManager
import com.appcoins.payment_method.paypal.PaypalPaymentMethod
import com.appcoins.payment_prefs.domain.PreSelectedPaymentUseCase
import com.appcoins.payments.arch.TransactionStatus
import com.appcoins.payments.arch.TransactionStatus.COMPLETED
import com.appcoins.payments.arch.TransactionStatus.SETTLED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
internal class InjectionsProvider @Inject constructor(
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
          preSelectedPaymentUseCase = viewModelProvider.preSelectedPaymentUseCase,
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState
}

class PaypalViewModel internal constructor(
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
        val paypalPaymentMethod =
          paymentManager.getPaymentMethod(paymentMethodId) as PaypalPaymentMethod

        val billingAgreementData = paypalPaymentMethod.init()

        if (billingAgreementData != null) {
          viewModelState.update {
            PaypalUIState.BillingAgreementAvailable(
              purchaseRequest = paypalPaymentMethod.purchaseRequest,
              paymentMethodName = paypalPaymentMethod.label,
              paymentMethodIconUrl = paypalPaymentMethod.iconUrl,
              onBuyClick = ::makePurchase,
              onRemoveBillingAgreementClick = ::removeBillingAgreement
            )
          }
        } else {
          val billingAgreement = paypalPaymentMethod.createToken(packageName)
          viewModelState.update {
            PaypalUIState.LaunchWebViewActivity(
              url = billingAgreement.url,
              token = billingAgreement.token,
              onWebViewResult = ::onWebViewResult
            )
          }
        }
      } catch (e: Throwable) {
        if (e is IOException) {
          viewModelState.update { PaypalUIState.NoConnection }
        } else {
          viewModelState.update { PaypalUIState.Error }
        }
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
            TransactionStatus.FAILED,
            TransactionStatus.CANCELED,
            TransactionStatus.INVALID_TRANSACTION,
            TransactionStatus.FRAUD,
            -> viewModelState.update { PaypalUIState.Error }

            COMPLETED,
            -> {
              preSelectedPaymentUseCase.saveLastSuccessfulPaymentMethod(paypalPaymentMethod.id)

              viewModelState.update {
                PaypalUIState.Success(
                  valueInDollars = paypalPaymentMethod.productInfo.priceInDollars,
                  uid = transaction.uid
                )
              }
            }

            else -> Unit
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
                SETTLED,
                COMPLETED,
                -> {
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
