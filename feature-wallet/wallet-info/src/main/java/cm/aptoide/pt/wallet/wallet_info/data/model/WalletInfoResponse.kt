package cm.aptoide.pt.wallet.wallet_info.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Date

@Keep
internal data class WalletInfoResponse(
  val wallet: String,
  @SerializedName("eth_balance") val ethBalanceWei: BigInteger,
  @SerializedName("appc_balance") val appcBalanceWei: BigInteger,
  @SerializedName("appc_c_balance") val appcCreditsBalanceWei: BigInteger,
  @SerializedName("eth_fiat_balance") val ethBalanceFiat: BigDecimal,
  @SerializedName("appc_fiat_balance") val appcBalanceFiat: BigDecimal,
  @SerializedName("appc_c_fiat_balance") val appcCreditsBalanceFiat: BigDecimal,
  @SerializedName("units_balance") val unitsBalance: Long,
  val currency: String,
  val symbol: String,
  val blocked: Boolean,
  val verified: Boolean,
  val logging: Boolean,
  @SerializedName("has_backup") val hasBackup: Boolean,
  @SerializedName("last_backup_date") val lastBackupDate: Date?,
  @SerializedName("sentry_breadcrumbs") val breadcrumbs: Int,
  @SerializedName("can_transfer") private val canTransferInt: Int
)
