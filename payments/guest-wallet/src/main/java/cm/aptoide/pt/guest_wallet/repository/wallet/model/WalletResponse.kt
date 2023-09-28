package cm.aptoide.pt.guest_wallet.repository.wallet.model

import androidx.annotation.Keep

@Keep
data class WalletResponse(
  val address: String?,
  val ewt: String?,
  val signature: String?,
)
