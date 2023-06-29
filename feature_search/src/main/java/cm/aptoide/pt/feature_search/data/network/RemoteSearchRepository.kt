package cm.aptoide.pt.feature_search.data.network

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.feature_apps.data.model.AppJSON
import cm.aptoide.pt.feature_search.data.network.model.TopSearchAppJsonList
import cm.aptoide.pt.feature_search.data.network.response.SearchAutoCompleteSuggestionsResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RemoteSearchRepository {
  suspend fun getTopSearchedApps(): Response<BaseV7DataListResponse<AppJSON>>

  suspend fun getAutoCompleteSuggestions(keyword: String): Response<SearchAutoCompleteSuggestionsResponse>

  suspend fun searchApp(keyword: String): Response<BaseV7DataListResponse<AppJSON>>
}
