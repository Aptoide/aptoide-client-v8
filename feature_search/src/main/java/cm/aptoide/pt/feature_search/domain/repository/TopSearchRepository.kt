package cm.aptoide.pt.feature_search.domain.repository

import cm.aptoide.pt.feature_search.domain.model.TopSearchApp

interface TopSearchRepository {
  suspend fun getTopSearchedApps(): List<TopSearchApp>
}