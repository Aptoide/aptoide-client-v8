package cm.aptoide.pt.feature_search.data.network

import cm.aptoide.pt.aptoide_network.data.network.BaseV7DataListResponse
import cm.aptoide.pt.feature_search.data.network.model.SearchAppJsonList
import cm.aptoide.pt.feature_search.data.network.model.TopSearchAppJsonList
import cm.aptoide.pt.feature_search.data.network.response.SearchAutoCompleteSuggestionsResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RemoteSearchRepository {
  fun getTopSearchedApps(): Flow<List<TopSearchAppJsonList>>

  suspend fun getAutoCompleteSuggestions(keyword: String): Response<SearchAutoCompleteSuggestionsResponse>

  suspend fun searchApp(keyword: String): Response<BaseV7DataListResponse<SearchAppJsonList>>
}