package cm.aptoide.pt.promotions

data class Promotion(var isClaimed: Boolean = false,
                     val appc: Float = -1f,
                     val promotionDescription: String = "",
                     val promotionId: String = "") {

  fun exists(): Boolean {
    return !promotionId.isEmpty()
  }

  fun isClaimable(): Boolean {
    return exists() && !isClaimed
  }
}