package cm.aptoide.pt.payment_method.adyen.repository.model

import androidx.annotation.Keep
import cm.aptoide.pt.payment_manager.transaction.TransactionStatus
import cm.aptoide.pt.payment_manager.transaction.TransactionStatus.CANCELED
import cm.aptoide.pt.payment_manager.transaction.TransactionStatus.DUPLICATED
import cm.aptoide.pt.payment_manager.transaction.TransactionStatus.FAILED
import cm.aptoide.pt.payment_method.adyen.repository.AcquirerErrorException
import cm.aptoide.pt.payment_method.adyen.repository.AdyenRefusalException
import cm.aptoide.pt.payment_method.adyen.repository.BlockedCardException
import cm.aptoide.pt.payment_method.adyen.repository.CancelledDueToFraudException
import cm.aptoide.pt.payment_method.adyen.repository.CvcDeclinedException
import cm.aptoide.pt.payment_method.adyen.repository.DeclinedException
import cm.aptoide.pt.payment_method.adyen.repository.DeclinedNonGenericException
import cm.aptoide.pt.payment_method.adyen.repository.ExpiredCardException
import cm.aptoide.pt.payment_method.adyen.repository.FraudRefusalException
import cm.aptoide.pt.payment_method.adyen.repository.IncorrectOnlinePinException
import cm.aptoide.pt.payment_method.adyen.repository.InvalidAmountException
import cm.aptoide.pt.payment_method.adyen.repository.InvalidCardNumberException
import cm.aptoide.pt.payment_method.adyen.repository.IssuerSuspectedFraudException
import cm.aptoide.pt.payment_method.adyen.repository.IssuerUnavailableException
import cm.aptoide.pt.payment_method.adyen.repository.Not3dAuthenticatedException
import cm.aptoide.pt.payment_method.adyen.repository.NotEnoughBalanceException
import cm.aptoide.pt.payment_method.adyen.repository.NotSupportedException
import cm.aptoide.pt.payment_method.adyen.repository.PinTriesExceededException
import cm.aptoide.pt.payment_method.adyen.repository.ReferralException
import cm.aptoide.pt.payment_method.adyen.repository.RestrictedCardException
import cm.aptoide.pt.payment_method.adyen.repository.RevocationOfAuthException
import cm.aptoide.pt.payment_method.adyen.repository.TransactionNotPermittedException
import cm.aptoide.pt.payment_method.adyen.repository.WithdrawAmountExceededException
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

@Keep
data class TransactionResponse(
  val uid: String,
  val hash: String?,
  @SerializedName("reference") val orderReference: String?,
  val status: TransactionStatus,
  val payment: PaymentResponse?,
  val metadata: TransactionMetadata?,
)

@Keep
data class PaymentResponse(
  val pspReference: String,
  val resultCode: String,
  private val action: JsonObject?,
  val refusalReason: String?,
  val refusalReasonCode: Int?,
  val fraudResult: FraudResultResponse?,
) {
  val actionJson
    get() = action?.let { JSONObject(it.toString()) }
}

@Keep
data class FraudResultResponse(
  val accountScore: String,
  val results: List<FraudResult>,
)

@Keep
data class FraudResult(
  @SerializedName("FraudCheckResult") val fraudCheckResult: FraudCheckResult,
)

@Keep
data class FraudCheckResult(
  val accountScore: Int,
  val checkId: Int,
  val name: String,
)

@Keep
data class TransactionMetadata(
  @SerializedName("error_message") val errorMessage: String?,
  @SerializedName("error_code") val errorCode: Int?,
  @SerializedName("purchase_uid") val purchaseUid: String?,
)

fun TransactionResponse.mapAdyenRefusalCode(
): AdyenRefusalException? {
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
