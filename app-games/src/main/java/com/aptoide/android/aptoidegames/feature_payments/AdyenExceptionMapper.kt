package com.aptoide.android.aptoidegames.feature_payments

import com.appcoins.payments.methods.adyen.AcquirerErrorException
import com.appcoins.payments.methods.adyen.AlreadyProcessedException
import com.appcoins.payments.methods.adyen.BlockedCardException
import com.appcoins.payments.methods.adyen.CancelledDueToFraudException
import com.appcoins.payments.methods.adyen.CardSecurityException
import com.appcoins.payments.methods.adyen.ConflictException
import com.appcoins.payments.methods.adyen.CurrencyNotSupportedException
import com.appcoins.payments.methods.adyen.CvcDeclinedException
import com.appcoins.payments.methods.adyen.CvcLengthException
import com.appcoins.payments.methods.adyen.DeclinedException
import com.appcoins.payments.methods.adyen.DeclinedNonGenericException
import com.appcoins.payments.methods.adyen.ExpiredCardException
import com.appcoins.payments.methods.adyen.ForbiddenException
import com.appcoins.payments.methods.adyen.FraudException
import com.appcoins.payments.methods.adyen.FraudRefusalException
import com.appcoins.payments.methods.adyen.IncorrectOnlinePinException
import com.appcoins.payments.methods.adyen.InvalidAmountException
import com.appcoins.payments.methods.adyen.InvalidCardException
import com.appcoins.payments.methods.adyen.InvalidCardNumberException
import com.appcoins.payments.methods.adyen.InvalidCountryCodeException
import com.appcoins.payments.methods.adyen.IssuerSuspectedFraudException
import com.appcoins.payments.methods.adyen.IssuerUnavailableException
import com.appcoins.payments.methods.adyen.MissingBillingAddressException
import com.appcoins.payments.methods.adyen.Not3DSAuthenticatedException
import com.appcoins.payments.methods.adyen.NotAllowedException
import com.appcoins.payments.methods.adyen.NotEnoughBalanceException
import com.appcoins.payments.methods.adyen.NotSupportedException
import com.appcoins.payments.methods.adyen.OutdatedCardException
import com.appcoins.payments.methods.adyen.PaymentErrorException
import com.appcoins.payments.methods.adyen.PaymentNotSupportedException
import com.appcoins.payments.methods.adyen.PinTriesExceededException
import com.appcoins.payments.methods.adyen.ReferralException
import com.appcoins.payments.methods.adyen.RestrictedCardException
import com.appcoins.payments.methods.adyen.RevocationOfAuthException
import com.appcoins.payments.methods.adyen.TransactionAmountExceededException
import com.appcoins.payments.methods.adyen.TransactionNotPermittedException
import com.appcoins.payments.methods.adyen.WithdrawAmountExceededException
import com.aptoide.android.aptoidegames.R

fun getAdyenErrorMessage(e: Throwable) = when (e) {
  /*Generic errors*/
  is FraudException,
  is MissingBillingAddressException,
  is ConflictException,
  is NotAllowedException,
  is ForbiddenException,
  -> null //R.string.error_message_generic_title

  /*Adyen errors*/
  is InvalidCardException -> R.string.purchase_error_invalid_credit_card_title
  is CvcLengthException -> R.string.purchase_card_error_CVV_title
  is CardSecurityException -> R.string.purchase_error_card_security_validation_title
  is CurrencyNotSupportedException -> R.string.purchase_card_error_general_title
  is InvalidCountryCodeException -> null //R.string.error_message_generic_title
  is OutdatedCardException -> R.string.payments_error_old_card_title
  is AlreadyProcessedException -> R.string.purchase_error_in_progress_title
  is PaymentErrorException -> R.string.purchase_error_payment_rejected
  is PaymentNotSupportedException -> R.string.purchase_error_payment_rejected
  is TransactionAmountExceededException -> R.string.purchase_card_error_no_funds

  /*Adyen refusals*/
  is DeclinedException -> R.string.purchase_card_rejected_title
  is ReferralException -> R.string.purchase_card_error_general_title
  is AcquirerErrorException -> R.string.purchase_card_error_general_title
  is BlockedCardException -> R.string.purchase_card_rejected_title
  is ExpiredCardException -> R.string.purchase_error_expired_card_title
  is InvalidAmountException -> R.string.purchase_card_error_no_funds
  is InvalidCardNumberException -> R.string.purchase_card_error_invalid_details_title
  is IssuerUnavailableException -> R.string.purchase_card_error_general_title
  is NotSupportedException -> R.string.purchase_card_error_not_supported_title
  is Not3DSAuthenticatedException -> R.string.purchase_error_3d_title
  is NotEnoughBalanceException -> R.string.purchase_card_error_no_funds
  is IncorrectOnlinePinException -> R.string.purchase_error_wrong_pin_title
  is PinTriesExceededException -> R.string.purchase_error_wrong_pin_title
  is FraudRefusalException -> R.string.purchase_card_rejected_title
  is CancelledDueToFraudException -> R.string.purchase_card_rejected_title
  is TransactionNotPermittedException -> R.string.purchase_card_rejected_title
  is CvcDeclinedException -> R.string.purchase_card_error_CVV_title
  is RestrictedCardException -> R.string.purchase_card_error_no_funds
  is RevocationOfAuthException -> R.string.purchase_card_rejected_title
  is DeclinedNonGenericException -> R.string.purchase_card_rejected_title
  is WithdrawAmountExceededException -> R.string.purchase_card_error_no_funds
  is IssuerSuspectedFraudException -> R.string.purchase_card_rejected_title
  else -> null //R.string.error_message_generic_title
}

fun getAdyenErrorDescription(e: Throwable) = when (e) {
  /*Generic errors*/
  is FraudException,
  is MissingBillingAddressException,
  is ConflictException,
  is NotAllowedException,
  is ForbiddenException,
  -> null //R.string.try_again_or_contact_us_body

  /*Adyen errors*/
  is InvalidCardException -> R.string.purchase_error_check_it_body
  is CvcLengthException -> R.string.purchase_error_check_it_body
  is CardSecurityException -> R.string.try_again
  is CurrencyNotSupportedException -> R.string.purchase_error_try_other_card_body
  is InvalidCountryCodeException -> null //R.string.try_again_or_contact_us_body
  is OutdatedCardException -> R.string.payments_error_old_card_body
  is AlreadyProcessedException -> R.string.please_wait
  is PaymentErrorException -> R.string.purchase_error_try_other_card_body
  is PaymentNotSupportedException -> R.string.purchase_error_try_other_card_body
  is TransactionAmountExceededException -> R.string.purchase_error_try_other_payment_method_body

  /*Adyen refusals*/
  is DeclinedException -> R.string.purchase_error_try_other_card_body
  is ReferralException -> R.string.purchase_error_try_other_card_body
  is AcquirerErrorException -> R.string.purchase_error_try_other_card_body
  is BlockedCardException -> R.string.purchase_error_try_other_card_body
  is ExpiredCardException -> R.string.purchase_error_try_other_payment_method_body
  is InvalidAmountException -> R.string.purchase_error_try_other_payment_method_body
  is InvalidCardNumberException -> R.string.purchase_error_check_it_body
  is IssuerUnavailableException -> R.string.purchase_error_try_other_card_body
  is NotSupportedException -> R.string.purchase_error_try_other_card_body
  is Not3DSAuthenticatedException -> R.string.try_again
  is NotEnoughBalanceException -> R.string.purchase_error_try_other_payment_method_body
  is IncorrectOnlinePinException -> R.string.try_again
  is PinTriesExceededException -> R.string.try_again
  is FraudRefusalException -> R.string.purchase_error_try_other_card_body
  is CancelledDueToFraudException -> R.string.purchase_error_try_other_card_body
  is TransactionNotPermittedException -> R.string.purchase_error_try_other_card_body
  is CvcDeclinedException -> R.string.purchase_error_check_it_body
  is RestrictedCardException -> R.string.purchase_error_try_other_payment_method_body
  is RevocationOfAuthException -> R.string.purchase_error_try_other_card_body
  is DeclinedNonGenericException -> R.string.purchase_error_try_other_card_body
  is WithdrawAmountExceededException -> R.string.purchase_error_try_other_payment_method_body
  is IssuerSuspectedFraudException -> R.string.purchase_error_try_other_card_body

  else -> null //R.string.try_again_or_contact_us_body
}
