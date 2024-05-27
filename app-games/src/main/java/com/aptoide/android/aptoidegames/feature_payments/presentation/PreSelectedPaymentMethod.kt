package com.aptoide.android.aptoidegames.feature_payments.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.extensions.runPreviewable
import com.appcoins.payments.manager.presentation.PaymentMethodsUiState
import com.appcoins.payments.methods.adyen.presentation.AdyenCreditCardUiState
import com.appcoins.payments.methods.paypal.presentation.PaypalUIState
import com.aptoide.android.aptoidegames.feature_payments.getRoute
import com.aptoide.android.aptoidegames.feature_payments.repository.PreSelectedPaymentMethodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreSelectedPaymentMethodViewModel @Inject constructor(
  private val repository: PreSelectedPaymentMethodRepository,
) : ViewModel() {

  val uiState = repository.getPreselectedPaymentMethod()
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      null
    )

  fun setSelection(id: String?) {
    viewModelScope.launch {
      repository.setPreselectedPaymentMethod(id)
    }
  }
}

@Composable
fun PreselectedPaymentMethodEffect(
  paymentMethodsState: PaymentMethodsUiState,
  navigate: (String) -> Unit,
) = runPreviewable(
  preview = { },
  real = {
    var navigated by rememberSaveable { mutableStateOf(false) }
    val vm = hiltViewModel<PreSelectedPaymentMethodViewModel>()
    val preselectedId by vm.uiState.collectAsState()

    LaunchedEffect(key1 = preselectedId, key2 = paymentMethodsState, key3 = navigated) {
      if (paymentMethodsState is PaymentMethodsUiState.Idle && !navigated) {
        paymentMethodsState.paymentMethods.find { it.id == preselectedId }
          ?.getRoute(isPreSelected = true)
          ?.also {
            navigate(it)
            navigated = true
          }
      }
    }
  }
)

@Composable
fun rememberHasPreselectedPaymentMethod(): Boolean = runPreviewable(
  preview = { true },
  real = {
    val vm = hiltViewModel<PreSelectedPaymentMethodViewModel>()
    val uiState by vm.uiState.collectAsState()
    uiState != null
  }
)


@Composable
fun PaypalPaymentStateEffect(
  paymentMethodId: String,
  uiState: PaypalUIState,
) = runPreviewable(
  preview = { },
  real = {
    val vm = hiltViewModel<PreSelectedPaymentMethodViewModel>()
    LaunchedEffect(key1 = paymentMethodId, key2 = uiState) {
      if (uiState is PaypalUIState.Success) vm.setSelection(paymentMethodId)
    }
  }
)

@Composable
fun AdyenCreditCardStateEffect(
  paymentMethodId: String,
  uiState: AdyenCreditCardUiState,
) = runPreviewable(
  preview = { },
  real = {
    val vm = hiltViewModel<PreSelectedPaymentMethodViewModel>()
    LaunchedEffect(key1 = paymentMethodId, key2 = uiState) {
      if (uiState is AdyenCreditCardUiState.Success) vm.setSelection(paymentMethodId)
    }
  }
)
