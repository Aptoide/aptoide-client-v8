package cm.aptoide.pt.feature_search.data.network

import cm.aptoide.pt.feature_search.data.network.model.TopSearchAppJsonList
import cm.aptoide.pt.feature_search.data.network.response.SearchAutoCompleteSuggestionsResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RemoteSearchRepository {
  fun getTopSearchedApps(): Flow<List<TopSearchAppJsonList>>

  suspend fun getAutoCompleteSuggestions(keyword: String): Response<SearchAutoCompleteSuggestionsResponse>
}