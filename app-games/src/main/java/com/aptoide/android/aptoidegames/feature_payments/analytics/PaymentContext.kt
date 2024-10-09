package com.aptoide.android.aptoidegames.feature_payments.analytics

import com.appcoins.payments.arch.PaymentsErrorResult
import com.appcoins.payments.arch.PaymentsSuccessResult
import com.appcoins.payments.manager.presentation.TransactionUIState
import com.appcoins.payments.methods.adyen.presentation.AdyenCreditCardUiState
import com.appcoins.payments.methods.paypal.presentation.PaypalUIState

object PaymentContext {
  const val SECOND_STEP = "2nd_step"
  const val CONCLUSION = "conclusion"
}

val AdyenCreditCardUiState.paymentContext: String?
  get() = when (this) {
    is AdyenCreditCardUiState.Success -> PaymentContext.CONCLUSION
    is AdyenCreditCardUiState.Error -> null
    else -> PaymentContext.SECOND_STEP
  }

val PaypalUIState.paymentContext: String?
  get() = when (this) {
    is PaypalUIState.Success -> PaymentContext.CONCLUSION
    is PaypalUIState.Error -> null
    else -> PaymentContext.SECOND_STEP
  }

val TransactionUIState.paymentContext: String?
  get() = when (this) {
    is TransactionUIState.Finished -> when (result) {
      is PaymentsSuccessResult -> PaymentContext.CONCLUSION
      is PaymentsErrorResult -> null
      else -> PaymentContext.CONCLUSION
    }

    else -> PaymentContext.CONCLUSION
  }
