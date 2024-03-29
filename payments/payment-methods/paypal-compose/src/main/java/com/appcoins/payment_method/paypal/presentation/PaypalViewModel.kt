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
import com.appcoins.payments.arch.Logger
import com.appcoins.payments.arch.PaymentMethod
import com.appcoins.payments.arch.PaymentsInitializer
import com.appcoins.payments.arch.TransactionStatus
import com.appcoins.payments.arch.TransactionStatus.COMPLETED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.CancellationException

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
          logger = PaymentsInitializer.logger,
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
  private val logger: Logger,
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
        logger.logPaypalEvent(
          message = "payment_method_details",
          data = paymentMethod.toData().putResult(true),
        )
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
        logger.logPaypalEvent(
          message = "payment_method_details",
          data = emptyMap<String, Any?>().putResult(false),
        )
        logger.logPaypalError(e)
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
        logger.logPaypalEvent(
          message = "cancel_billing_agreement",
          data = paymentMethod.toData().putResult(success),
        )

        if (success) {
          viewModelState.update { PaypalUIState.PaypalAgreementRemoved }
        } else {
          logger.logPaypalError(IllegalStateException("Failed to cancel billing agreement!"))
          viewModelState.update { PaypalUIState.Error }
        }
      } catch (e: Throwable) {
        logger.logPaypalEvent(
          message = "cancel_billing_agreement",
          data = paymentMethod.toData().putResult(false),
        )
        logger.logPaypalError(e)
        viewModelState.update { PaypalUIState.Error }
      }
    }
  }

  private fun makePurchase() {
    viewModelScope.launch {
      try {
        viewModelState.update { PaypalUIState.MakingPurchase }
        val transaction = paymentMethod.createTransaction(Unit)
        logger.logPaypalEvent(
          message = "transaction_create",
          data = paymentMethod.toData().putResult(true),
        )

        transaction.status.collect {
          logger.logPaypalEvent(
            message = "transaction_update",
            data = paymentMethod.toData().putTransaction(transaction.uid, it),
          )
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
        logger.logPaypalEvent(
          message = "transaction_create",
          data = emptyMap<String, Any?>().putResult(false),
        )
        logger.logPaypalError(e)
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
            logger.logPaypalEvent(
              message = "payment_method_details",
              data = paymentMethod.toData().putResult(true),
            )
            makePurchase()
          }

          Activity.RESULT_CANCELED -> {
            paymentMethod.cancelToken(token)
            logger.logPaypalError(CancellationException("BA token confirmation cancelled! BA token cancelled"))
            viewModelState.update { PaypalUIState.Canceled }
          }

          else -> {
            paymentMethod.cancelToken(token)
            logger.logPaypalError(Exception("BA token confirmation failed! BA token cancelled"))
            viewModelState.update { PaypalUIState.Error }
          }
        }
      } catch (e: Throwable) {
        logger.logPaypalEvent(
          message = "payment_method_details",
          data = emptyMap<String, Any?>().putResult(false),
        )
        logger.logPaypalError(e)
        viewModelState.update { PaypalUIState.Error }
      }
    }
  }
}

private fun Logger.logPaypalError(throwable: Throwable) = logError(
  tag = "direct_paypal",
  throwable = throwable,
)

private fun Logger.logPaypalEvent(
  message: String,
  data: Map<String, Any?> = emptyMap(),
) = logEvent(
  tag = "direct_paypal",
  message = message,
  data = data
)

private fun <T> PaymentMethod<T>.toData(): Map<String, Any?> = mapOf(
  "payment_method" to id,
  "product_info" to mapOf(
    "sku" to productInfo.sku,
    "value" to productInfo.priceValue,
    "currency" to productInfo.priceCurrency,
  ),
  "wallet" to wallet,
)

private fun Map<String, Any?>.putTransaction(txId: String, status: TransactionStatus) =
  this + mapOf(
    "txId" to txId,
    "status" to status
  )

private fun Map<String, Any?>.putResult(success: Boolean) =
  this + mapOf("result" to if (success) "success" else "fail")
