package cm.aptoide.pt.payment_method.adyen.repository.model

import androidx.annotation.Keep
import cm.aptoide.pt.payment_manager.transaction.TransactionStatus
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
  val action: JSONObject?,
  val refusalReason: String?,
  val refusalReasonCode: String?,
  val fraudResult: FraudResultResponse?,
)

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