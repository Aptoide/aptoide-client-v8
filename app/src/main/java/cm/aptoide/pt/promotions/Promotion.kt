package cm.aptoide.pt.promotions

data class Promotion(var isClaimed: Boolean = false,
                     val appc: Float = -1f,
                     val packageName: String = "",
                     val promotionId: String = "",
                     var claimActions: List<ClaimAction> = emptyList()) {

  fun exists(): Boolean {
    return !promotionId.isEmpty()
  }

  fun isClaimable(): Boolean {
    return exists() && !isClaimed
  }

  /**
   * Represents what action is necessary to claim the promotions
   */
  enum class ClaimAction {
    INSTALL, MIGRATE
  }
}