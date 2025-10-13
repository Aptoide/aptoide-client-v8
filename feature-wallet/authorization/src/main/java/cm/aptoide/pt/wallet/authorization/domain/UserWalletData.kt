package cm.aptoide.pt.wallet.authorization.domain

data class UserWalletData(
  val address: String,
  val authToken: String,
  val email: String?,
  val addedTs: Long = System.currentTimeMillis()
) {
  fun isExpired(): Boolean {
    return (addedTs / 1000L) + TTL_IN_SECONDS < System.currentTimeMillis() / 1000L
  }

  companion object {
    private const val TTL_IN_SECONDS = 3600
  }
}
