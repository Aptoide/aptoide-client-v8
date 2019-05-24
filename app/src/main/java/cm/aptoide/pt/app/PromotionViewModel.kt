package cm.aptoide.pt.app

import android.util.SparseArray
import cm.aptoide.pt.promotions.Promotion
import cm.aptoide.pt.promotions.WalletApp
import cm.aptoide.pt.view.app.DetailedApp

data class PromotionViewModel(
    var walletApp: WalletApp = WalletApp(),
    var promotions: List<Promotion> = ArrayList(),
    var appDownloadModel: DownloadModel? = null,
    val app: DetailedApp? = null,
    var isAppMigrated: Boolean = false
) {

  private val claimablePromotions = SparseArray<Promotion>()

  /**
   * Retrieves the first claimable promotion for an action, if possible.
   *
   * @return Promotion
   * @return null, if no promotion for action was found
   * @return null, if any of the promotions was already claimed
   */
  fun getClaimablePromotion(action: Promotion.ClaimAction): Promotion? {
    if (hasCachedClaimableAction(action)) {
      val promotion = claimablePromotions[action.ordinal]
      if (!promotion.isClaimed)
        return promotion
    }
    var claimablePromotion: Promotion? = null
    for (promotion in promotions) {
      if (promotion.claimAction == action) {
        claimablePromotion = claimablePromotion ?: promotion
      }
      if (!promotion.isClaimable()) {
        claimablePromotion = null
        break
      }
    }
    claimablePromotions.put(action.ordinal, claimablePromotion)
    return claimablePromotion
  }

  private fun hasCachedClaimableAction(action: Promotion.ClaimAction): Boolean {
    return claimablePromotions.indexOfKey(action.ordinal) >= 0
  }
}