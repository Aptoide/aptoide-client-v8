package cm.aptoide.pt.promotions

data class Promotion(var isClaimed: Boolean = false,
                     val appc: Float = -1f,
                     val promotionDescription: String = "",
                     val promotionId: String = "",
                     var claimAction: ClaimAction = ClaimAction.NONE) {

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
    INSTALL, MIGRATE, NONE
  }
}