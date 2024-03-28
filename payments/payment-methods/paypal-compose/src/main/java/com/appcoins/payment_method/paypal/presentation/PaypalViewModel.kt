package com.appcoins.payment_method.paypal.presentation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.appcoins.payment_manager.di.PaymentsModule
import com.appcoins.payment_method.paypal.PaypalPaymentMethod
import com.appcoins.payment_prefs.di.PaymentPrefsModule
import com.appcoins.payment_prefs.domain.PreSelectedPaymentUseCase
import com.appcoins.payments.arch.TransactionStatus
import com.appcoins.payments.arch.TransactionStatus.COMPLETED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

@Composable
fun rememberPaypalUIState(
  paymentMethodId: String,
): PaypalUIState {
  val packageName = LocalContext.current.packageName
  val vm: PaypalViewModel = viewModel(
    key = paymentMethodId,
    factory = object : Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PaypalViewModel(
          paymentMethod = PaymentsModule.paymentManager.getPaymentMethod(paymentMethodId) as PaypalPaymentMethod,
          packageName = packageName,
          preSelectedPaymentUseCase = PaymentPrefsModule.preSelectedPaymentUseCase,
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState
}

class PaypalViewModel internal constructor(
  private val packageName: String,
  private val paymentMethod: PaypalPaymentMethod,
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
        val billingAgreementData = paymentMethod.init()
        if (billingAgreementData != null) {
          viewModelState.update {
            PaypalUIState.BillingAgreementAvailable(
              purchaseRequest = paymentMethod.purchaseRequest,
              paymentMethodName = paymentMethod.label,
              paymentMethodIconUrl = paymentMethod.iconUrl,
              onBuyClick = ::makePurchase,
              onRemoveBillingAgreementClick = ::removeBillingAgreement
            )
          }
        } else {
          val billingAgreement = paymentMethod.createToken(packageName)
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
        val success = paymentMethod.cancelBillingAgreement()
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
        val transaction = paymentMethod.createTransaction(Unit)

        transaction.status.collect {
          when (it) {
            TransactionStatus.FAILED,
            TransactionStatus.CANCELED,
            TransactionStatus.INVALID_TRANSACTION,
            TransactionStatus.FRAUD,
            -> viewModelState.update { PaypalUIState.Error }

            COMPLETED -> {
              preSelectedPaymentUseCase.saveLastSuccessfulPaymentMethod(paymentMethod.id)
              viewModelState.update { PaypalUIState.Success }
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
        when (resultCode) {
          Activity.RESULT_OK -> {
            paymentMethod.createBillingAgreement(token)
            makePurchase()
          }

          Activity.RESULT_CANCELED -> {
            paymentMethod.cancelToken(token)
            viewModelState.update { PaypalUIState.Canceled }
          }

          else -> {
            paymentMethod.cancelToken(token)
            viewModelState.update { PaypalUIState.Error }
          }
        }
      } catch (e: Throwable) {
        viewModelState.update { PaypalUIState.Error }
      }
    }
  }
}
