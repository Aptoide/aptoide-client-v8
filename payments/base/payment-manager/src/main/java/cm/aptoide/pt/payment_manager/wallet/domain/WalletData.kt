package cm.aptoide.pt.payment_manager.wallet.domain

data class WalletData(
  val address: String,
  val ewt: String,
  val signature: String,
)
