package com.aptoide.android.aptoidegames.search.repository

import com.aptoide.android.aptoidegames.BuildConfig
import cm.aptoide.pt.feature_search.domain.repository.SearchStoreManager

class AppGamesSearchStoreManager : SearchStoreManager {

  override fun shouldAddStore(): Boolean {
    return false
  }

  override fun getStore(): String {
    return BuildConfig.MARKET_NAME
  }

  override fun searchResultLimit(): Int = 60
}
