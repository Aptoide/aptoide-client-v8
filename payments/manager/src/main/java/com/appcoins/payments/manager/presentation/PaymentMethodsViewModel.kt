package com.appcoins.payments.manager.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcoins.payments.arch.Logger
import com.appcoins.payments.arch.PaymentManager
import com.appcoins.payments.arch.PurchaseRequest
import com.appcoins.payments.manager.presentation.PaymentMethodsUiState.Error
import com.appcoins.payments.manager.presentation.PaymentMethodsUiState.Idle
import com.appcoins.payments.manager.presentation.PaymentMethodsUiState.Loading
import com.appcoins.payments.manager.presentation.PaymentMethodsUiState.NoConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class PaymentMethodsViewModel(
  private val purchaseRequest: PurchaseRequest,
  private val paymentManager: PaymentManager,
  private val logger: Logger,
) : ViewModel() {

  private val viewModelState = MutableStateFlow<PaymentMethodsUiState>(Loading)

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
        logger.logPaymentManagerEvent(
          message = "payment_methods",
          data = purchaseRequest.toData().putResult(true)
        )
        viewModelState.update { Idle(paymentMethods) }
      } catch (e: Throwable) {
        logger.logPaymentManagerEvent(
          message = "payment_methods",
          data = purchaseRequest.toData().putResult(false)
        )
        logger.logPaymentManagerError(e)
        if (e is IOException) {
          viewModelState.update { NoConnection }
        } else {
          viewModelState.update { Error }
        }
      }
    }
  }
}

private fun Logger.logPaymentManagerError(throwable: Throwable) = logError(
  tag = "payment_manager",
  throwable = throwable,
)

private fun Logger.logPaymentManagerEvent(
  message: String,
  data: Map<String, Any?>,
) = logEvent(
  tag = "payment_manager",
  message = message,
  data = data
)

private fun PurchaseRequest.toData(): Map<String, Any?> = mapOf(
  "purchase" to mapOf(
    "package_name" to domain,
    "sku" to product,
    "value" to value,
    "currency" to currency,
  ),
  "oemId" to oemId,
  "oemPackage" to oemPackage,
  "transaction_type" to type,
)

private fun Map<String, Any?>.putResult(success: Boolean) =
  this + mapOf("result" to if (success) "success" else "fail")
