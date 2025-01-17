package com.aptoide.android.aptoidegames.promotions.domain

import cm.aptoide.pt.extensions.compatVersionCode
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsListRepository
import cm.aptoide.pt.install_manager.InstallManager
import com.aptoide.android.aptoidegames.promotions.data.PromotionsRepository
import com.aptoide.android.aptoidegames.promotions.data.database.SkippedPromotionsRepository
import javax.inject.Inject

class CompatiblePromotionsUseCase @Inject constructor(
  private val promotionsRepository: PromotionsRepository,
  private val skippedPromotionsRepository: SkippedPromotionsRepository,
  private val appsRepository: AppsListRepository,
  private val installManager: InstallManager,
) {

  /**
   * Gets the most compatible promotion according to the following criteria:
   *  1 - Promotion with highest bonus
   *  2 - In case of a tie, choose the one that comes first in the promotion list
   */
  suspend fun getTopPromotion(): Pair<Promotion, App>? {
    val skippedPromotions = skippedPromotionsRepository.getSkippedPromotions()
      .toSet()
    val promotions = promotionsRepository.getAllPromotions()
      //used to filter out all previously skipped promotions
      .filter { it.packageName !in skippedPromotions }
      .sortedBy(Promotion::userBonus)
    val promotionApps = appsRepository
      .getAppsList(
        packageNames = promotions.joinToString(
          separator = ",",
          transform = Promotion::packageName
        )
      )
      .associateBy(App::packageName)
    return promotions
      .mapNotNull { promo -> promotionApps[promo.packageName]?.let { promo to it } }
      .firstNotNullOfOrNull { it.takeIfPromotable() }
  }

  private fun Pair<Promotion, App>.takeIfPromotable(): Pair<Promotion, App>? = installManager
    .getApp(first.packageName)
    .packageInfo
    ?.takeIf { it.compatVersionCode < second.versionCode }
    ?.let { this }
    ?: first.aliases
      .firstNotNullOfOrNull {
        installManager.getApp(it)
          .packageInfo
          ?.takeIf { it.compatVersionCode <= second.versionCode }
      }
      ?.let { this }
}
