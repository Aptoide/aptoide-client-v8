package cm.aptoide.pt.feature_payment.repository.wallet.domain

data class WalletData(
  val address: String,
  val ewt: String,
  val signature: String,
)
