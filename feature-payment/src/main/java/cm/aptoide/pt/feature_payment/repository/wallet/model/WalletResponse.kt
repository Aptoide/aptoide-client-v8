package cm.aptoide.pt.feature_payment.repository.wallet.model

import androidx.annotation.Keep

@Keep
data class WalletResponse(
  val address: String?,
  val ewt: String?,
  val signature: String?,
)
