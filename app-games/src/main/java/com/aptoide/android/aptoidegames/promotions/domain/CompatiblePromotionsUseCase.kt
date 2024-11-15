package com.aptoide.android.aptoidegames.promotions.domain

import cm.aptoide.pt.feature_apps.data.AppsListRepository
import cm.aptoide.pt.install_manager.InstallManager
import com.aptoide.android.aptoidegames.promotions.data.PromotionsRepository
import javax.inject.Inject

class CompatiblePromotionsUseCase @Inject constructor(
  private val promotionsRepository: PromotionsRepository,
  private val appsRepository: AppsListRepository,
  private val installManager: InstallManager,
) {

  /**
   * Gets the most compatible promotion according to the following criteria:
   *  1 - Promotion with highest bonus
   *  2 - In case of a tie, choose the one that comes first in the promotion list
   */
  suspend fun getTopPromotion() = getCompatiblePromotions().maxByOrNull { it.first.userBonus }

  /**
   * Gets the promotions of apps that the user has installed and that have an outdated version
   */
  private suspend fun getCompatiblePromotions() = promotionsRepository.getAllPromotions()
    .run {
      val promotionApps = appsRepository.getAppsList(
        packageNames = this.joinToString(separator = ",") { it.packageName }
      )
      mapNotNull { promotion ->
        val promoApp = promotionApps.find { it.packageName == promotion.packageName }
        val localAppInfo = installManager.getApp(promotion.packageName).packageInfo

        if (promoApp != null && localAppInfo != null
          && localAppInfo.versionCode < promoApp.versionCode
        ) {
          promotion to promoApp
        } else null
      }
    }
}
