package cm.aptoide.pt.app_games.search.repository

import cm.aptoide.pt.app_games.BuildConfig
import cm.aptoide.pt.feature_search.domain.repository.SearchStoreManager

class AppGamesSearchStoreManager : SearchStoreManager {

  override fun shouldAddStore(): Boolean {
    return false
  }

  override fun getStore(): String {
    return BuildConfig.MARKET_NAME
  }
}
