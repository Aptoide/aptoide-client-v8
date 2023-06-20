package cm.aptoide.pt.search

import cm.aptoide.pt.feature_search.domain.repository.SearchStoreManager
import cm.aptoide.pt.BuildConfig


class AptoideSearchStoreManager: SearchStoreManager {

  override fun shouldAddStore(): Boolean {
    return false
  }

  override fun getStore(): String {
    return BuildConfig.MARKET_NAME
  }
}
