package cm.aptoide.pt.wallet.authorization.domain

data class UserWalletData(
  val address: String,
  val authToken: String,
  val email: String?
)
