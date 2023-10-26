package cm.aptoide.pt.payment_manager.wallet

data class WalletData(
  val address: String,
  val ewt: String,
  val signature: String,
)
