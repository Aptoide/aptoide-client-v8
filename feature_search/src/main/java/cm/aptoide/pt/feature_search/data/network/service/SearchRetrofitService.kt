package cm.aptoide.pt.feature_search.data.network.service

import cm.aptoide.pt.feature_search.data.network.RemoteSearchRepository
import cm.aptoide.pt.feature_search.data.network.model.TopSearchAppJsonList
import cm.aptoide.pt.feature_search.data.network.response.SearchSuggestionsResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path

class SearchRetrofitService : RemoteSearchRepository {


  interface AutoCompleteSearchRetrofitService {
    @GET("suggestion/app/{query}")
    suspend fun getAutoCompleteSuggestions(@Path("query") query: String): SearchSuggestionsResponse
  }

  override fun getTopSearchedApps(): Flow<List<TopSearchAppJsonList>> {
    TODO("Not yet implemented")
  }

}