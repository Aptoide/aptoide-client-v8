package cm.aptoide.pt.feature_search.domain.repository

import cm.aptoide.pt.feature_search.domain.model.SearchApp

interface SearchRepository {
  suspend fun searchApp(keyword: String): List<SearchApp>
}