package com.appcoins.payment_method.adyen.repository.model

import com.appcoins.payment_method.adyen.repository.AcquirerErrorException
import com.appcoins.payment_method.adyen.repository.AdyenRefusalException
import com.appcoins.payment_method.adyen.repository.BlockedCardException
import com.appcoins.payment_method.adyen.repository.CancelledDueToFraudException
import com.appcoins.payment_method.adyen.repository.CvcDeclinedException
import com.appcoins.payment_method.adyen.repository.DeclinedException
import com.appcoins.payment_method.adyen.repository.DeclinedNonGenericException
import com.appcoins.payment_method.adyen.repository.ExpiredCardException
import com.appcoins.payment_method.adyen.repository.FraudRefusalException
import com.appcoins.payment_method.adyen.repository.IncorrectOnlinePinException
import com.appcoins.payment_method.adyen.repository.InvalidAmountException
import com.appcoins.payment_method.adyen.repository.InvalidCardNumberException
import com.appcoins.payment_method.adyen.repository.IssuerSuspectedFraudException
import com.appcoins.payment_method.adyen.repository.IssuerUnavailableException
import com.appcoins.payment_method.adyen.repository.Not3dAuthenticatedException
import com.appcoins.payment_method.adyen.repository.NotEnoughBalanceException
import com.appcoins.payment_method.adyen.repository.NotSupportedException
import com.appcoins.payment_method.adyen.repository.PinTriesExceededException
import com.appcoins.payment_method.adyen.repository.ReferralException
import com.appcoins.payment_method.adyen.repository.RestrictedCardException
import com.appcoins.payment_method.adyen.repository.RevocationOfAuthException
import com.appcoins.payment_method.adyen.repository.TransactionNotPermittedException
import com.appcoins.payment_method.adyen.repository.WithdrawAmountExceededException
import com.appcoins.payments.arch.TransactionStatus
import com.appcoins.payments.arch.TransactionStatus.CANCELED
import com.appcoins.payments.arch.TransactionStatus.DUPLICATED
import com.appcoins.payments.arch.TransactionStatus.FAILED
import com.appcoins.payments.json.Json
import org.json.JSONObject

@Json
data class TransactionResponse(
  val uid: String,
  val hash: String?,
  @Json("reference") val orderReference: String?,
  val status: TransactionStatus,
  val payment: PaymentResponse?,
  val metadata: TransactionMetadata?,
)

@Json
data class PaymentResponse(
  val pspReference: String?,
  val resultCode: String,
  val action: JSONObject?,
  val refusalReason: String?,
  val refusalReasonCode: Int?,
  val fraudResult: FraudResultResponse?,
)

@Json
data class FraudResultResponse(
  val accountScore: String,
  val results: List<FraudResult>,
)

@Json
data class FraudResult(
  @Json("FraudCheckResult") val fraudCheckResult: FraudCheckResult,
)

@Json
data class FraudCheckResult(
  val accountScore: Int,
  val checkId: Int,
  val name: String,
)

@Json
data class TransactionMetadata(
  @Json("error_message") val errorMessage: String?,
  @Json("error_code") val errorCode: Int?,
  @Json("purchase_uid") val purchaseUid: String?,
)

fun TransactionResponse.mapAdyenRefusalCode(): AdyenRefusalException? {
  if (status == FAILED || status == CANCELED
    || status == DUPLICATED
  ) {
    val refusalReasonCode = payment?.refusalReasonCode
    val refusalReason = payment?.refusalReason

    if (refusalReasonCode != null && refusalReason != null) {
      return when (refusalReasonCode) {
        2 -> DeclinedException(refusalReason)
        3 -> ReferralException(refusalReason)
        4 -> AcquirerErrorException(refusalReason)
        5 -> BlockedCardException(refusalReason)
        6 -> ExpiredCardException(refusalReason)
        7 -> InvalidAmountException(refusalReason)
        8 -> InvalidCardNumberException(refusalReason)
        9 -> IssuerUnavailableException(refusalReason)
        10 -> NotSupportedException(refusalReason)
        11 -> Not3dAuthenticatedException(refusalReason)
        12 -> NotEnoughBalanceException(refusalReason)
        17 -> IncorrectOnlinePinException(refusalReason)
        18 -> PinTriesExceededException(refusalReason)
        20 -> FraudRefusalException(refusalReason)
        22 -> CancelledDueToFraudException(refusalReason)
        23 -> TransactionNotPermittedException(refusalReason)
        24 -> CvcDeclinedException(refusalReason)
        25 -> RestrictedCardException(refusalReason)
        26 -> RevocationOfAuthException(refusalReason)
        27 -> DeclinedNonGenericException(refusalReason)
        28 -> WithdrawAmountExceededException(refusalReason)
        31 -> IssuerSuspectedFraudException(refusalReason)
        else -> AdyenRefusalException(refusalReason)
      }
    } else {
      return AdyenRefusalException()
    }
  } else {
    return null
  }
}
