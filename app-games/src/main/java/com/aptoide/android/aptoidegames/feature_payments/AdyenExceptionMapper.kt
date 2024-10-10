package com.aptoide.android.aptoidegames.feature_payments

import com.appcoins.payments.arch.PaymentsResult
import com.appcoins.payments.methods.adyen.AdyenError
import com.appcoins.payments.methods.adyen.AdyenErrorType
import com.appcoins.payments.methods.adyen.AdyenRefusal
import com.appcoins.payments.methods.adyen.AdyenRefusalReasonCode
import com.aptoide.android.aptoidegames.R

fun getAdyenErrorMessage(e: PaymentsResult.Error) = when (e) {
  /*Adyen errors*/
  is AdyenError -> when (e.type) {
    AdyenErrorType.FRAUD -> null //R.string.error_message_generic_title
    AdyenErrorType.BLOCKED -> null //R.string.error_message_generic_title
    AdyenErrorType.SUB_ALREADY_OWNED -> null //R.string.error_message_generic_title
    AdyenErrorType.CONFLICT -> null //R.string.error_message_generic_title
    AdyenErrorType.UNKNOWN -> null
    AdyenErrorType.INVALID_CARD -> R.string.purchase_error_invalid_credit_card_title
    AdyenErrorType.PAYMENT_ERROR -> R.string.purchase_error_payment_rejected
    AdyenErrorType.CARD_SECURITY_VALIDATION -> R.string.purchase_error_card_security_validation_title
    AdyenErrorType.ALREADY_PROCESSED -> R.string.purchase_error_in_progress_title
    AdyenErrorType.OUTDATED_CARD -> R.string.payments_error_old_card_title
    AdyenErrorType.INVALID_COUNTRY_CODE -> null //R.string.error_message_generic_title
    AdyenErrorType.PAYMENT_NOT_SUPPORTED_ON_COUNTRY -> R.string.purchase_error_payment_rejected
    AdyenErrorType.CURRENCY_NOT_SUPPORTED -> R.string.purchase_card_error_general_title
    AdyenErrorType.CVC_LENGTH -> R.string.purchase_card_error_CVV_title
    AdyenErrorType.TRANSACTION_AMOUNT_EXCEEDED -> R.string.purchase_card_error_no_funds
    AdyenErrorType.CVC_REQUIRED -> R.string.purchase_card_error_CVV_title
  }

  /*Adyen refusals*/
  is AdyenRefusal -> when (e.reason) {
    AdyenRefusalReasonCode.REFUSED -> R.string.purchase_card_rejected_title
    AdyenRefusalReasonCode.REFERRAL -> R.string.purchase_card_error_general_title
    AdyenRefusalReasonCode.ACQUIRER_ERROR -> R.string.purchase_card_error_general_title
    AdyenRefusalReasonCode.BLOCKED_CARD -> R.string.purchase_card_rejected_title
    AdyenRefusalReasonCode.EXPIRED_CARD -> R.string.purchase_error_expired_card_title
    AdyenRefusalReasonCode.INVALID_AMOUNT -> R.string.purchase_card_error_no_funds
    AdyenRefusalReasonCode.INVALID_CARD_NUMBER -> R.string.purchase_card_error_invalid_details_title
    AdyenRefusalReasonCode.ISSUER_UNAVAILABLE -> R.string.purchase_card_error_general_title
    AdyenRefusalReasonCode.NOT_SUPPORTED -> R.string.purchase_card_error_not_supported_title
    AdyenRefusalReasonCode.THE_3D_NOT_AUTHENTICATED -> R.string.purchase_error_3d_title
    AdyenRefusalReasonCode.NOT_ENOUGH_BALANCE -> R.string.purchase_card_error_no_funds
    AdyenRefusalReasonCode.INVALID_PIN -> R.string.purchase_error_wrong_pin_title
    AdyenRefusalReasonCode.PIN_TRIES_EXCEEDED -> R.string.purchase_error_wrong_pin_title
    AdyenRefusalReasonCode.FRAUD -> R.string.purchase_card_rejected_title
    AdyenRefusalReasonCode.FRAUD_CANCELLED -> R.string.purchase_card_rejected_title
    AdyenRefusalReasonCode.TRANSACTION_NOT_PERMITTED -> R.string.purchase_card_rejected_title
    AdyenRefusalReasonCode.CVC_DECLINED -> R.string.purchase_card_error_CVV_title
    AdyenRefusalReasonCode.RESTRICTED_CARD -> R.string.purchase_card_error_no_funds
    AdyenRefusalReasonCode.REVOCATION_OF_AUTH -> R.string.purchase_card_rejected_title
    AdyenRefusalReasonCode.DECLINED_NON_GENERIC -> R.string.purchase_card_rejected_title
    AdyenRefusalReasonCode.WITHDRAW_AMOUNT_EXCEEDED -> R.string.purchase_card_error_no_funds
    AdyenRefusalReasonCode.ISSUER_SUSPECTED_FRAUD -> R.string.purchase_card_rejected_title
    else -> null
  }

  else -> null //R.string.error_message_generic_title
}

fun getAdyenErrorDescription(e: PaymentsResult.Error) = when (e) {
  /*Adyen errors*/
  is AdyenError -> when (e.type) {
    AdyenErrorType.FRAUD -> null //R.string.error_message_generic_title
    AdyenErrorType.BLOCKED -> null //R.string.error_message_generic_title
    AdyenErrorType.SUB_ALREADY_OWNED -> null //R.string.error_message_generic_title
    AdyenErrorType.CONFLICT -> null //R.string.error_message_generic_title
    AdyenErrorType.UNKNOWN -> null
    AdyenErrorType.INVALID_CARD -> R.string.purchase_error_check_it_body
    AdyenErrorType.PAYMENT_ERROR -> R.string.purchase_error_try_other_card_body
    AdyenErrorType.CARD_SECURITY_VALIDATION -> R.string.try_again
    AdyenErrorType.ALREADY_PROCESSED -> R.string.please_wait
    AdyenErrorType.OUTDATED_CARD -> R.string.payments_error_old_card_body
    AdyenErrorType.INVALID_COUNTRY_CODE -> null //R.string.try_again_or_contact_us_body
    AdyenErrorType.PAYMENT_NOT_SUPPORTED_ON_COUNTRY -> R.string.purchase_error_try_other_card_body
    AdyenErrorType.CURRENCY_NOT_SUPPORTED -> R.string.purchase_error_try_other_card_body
    AdyenErrorType.CVC_LENGTH -> R.string.purchase_error_check_it_body
    AdyenErrorType.TRANSACTION_AMOUNT_EXCEEDED -> R.string.purchase_error_try_other_payment_method_body
    AdyenErrorType.CVC_REQUIRED -> R.string.purchase_error_check_it_body
  }

  /*Adyen refusals*/
  is AdyenRefusal -> when (e.reason) {
    AdyenRefusalReasonCode.REFUSED -> R.string.purchase_error_try_other_card_body
    AdyenRefusalReasonCode.REFERRAL -> R.string.purchase_error_try_other_card_body
    AdyenRefusalReasonCode.ACQUIRER_ERROR -> R.string.purchase_error_try_other_card_body
    AdyenRefusalReasonCode.BLOCKED_CARD -> R.string.purchase_error_try_other_card_body
    AdyenRefusalReasonCode.EXPIRED_CARD -> R.string.purchase_error_try_other_payment_method_body
    AdyenRefusalReasonCode.INVALID_AMOUNT -> R.string.purchase_error_try_other_payment_method_body
    AdyenRefusalReasonCode.INVALID_CARD_NUMBER -> R.string.purchase_error_check_it_body
    AdyenRefusalReasonCode.ISSUER_UNAVAILABLE -> R.string.purchase_error_try_other_card_body
    AdyenRefusalReasonCode.NOT_SUPPORTED -> R.string.purchase_error_try_other_card_body
    AdyenRefusalReasonCode.THE_3D_NOT_AUTHENTICATED -> R.string.try_again
    AdyenRefusalReasonCode.NOT_ENOUGH_BALANCE -> R.string.purchase_error_try_other_payment_method_body
    AdyenRefusalReasonCode.INVALID_PIN -> R.string.try_again
    AdyenRefusalReasonCode.PIN_TRIES_EXCEEDED -> R.string.try_again
    AdyenRefusalReasonCode.FRAUD -> R.string.purchase_error_try_other_card_body
    AdyenRefusalReasonCode.FRAUD_CANCELLED -> R.string.purchase_error_try_other_card_body
    AdyenRefusalReasonCode.TRANSACTION_NOT_PERMITTED -> R.string.purchase_error_try_other_card_body
    AdyenRefusalReasonCode.CVC_DECLINED -> R.string.purchase_error_check_it_body
    AdyenRefusalReasonCode.RESTRICTED_CARD -> R.string.purchase_error_try_other_payment_method_body
    AdyenRefusalReasonCode.REVOCATION_OF_AUTH -> R.string.purchase_error_try_other_card_body
    AdyenRefusalReasonCode.DECLINED_NON_GENERIC -> R.string.purchase_error_try_other_card_body
    AdyenRefusalReasonCode.WITHDRAW_AMOUNT_EXCEEDED -> R.string.purchase_error_try_other_payment_method_body
    AdyenRefusalReasonCode.ISSUER_SUSPECTED_FRAUD -> R.string.purchase_error_try_other_card_body
    else -> null
  }

  else -> null //R.string.try_again_or_contact_us_body
}
