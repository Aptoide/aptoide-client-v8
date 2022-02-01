package cm.aptoide.pt.feature_search.data.network.service

import cm.aptoide.pt.feature_search.data.network.response.SearchAppResponse

interface SearchRetrofitRepository {
  suspend fun searchApp(keyword: String): SearchAppResponse
}