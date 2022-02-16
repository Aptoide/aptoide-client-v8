package cm.aptoide.pt.feature_search.data.network.service

import cm.aptoide.pt.feature_search.data.network.RemoteSearchRepository
import cm.aptoide.pt.feature_search.data.network.response.SearchSuggestionsResponse
import retrofit2.http.GET
import retrofit2.http.Path

class SearchRetrofitService : RemoteSearchRepository {


  interface AutoCompleteSearchRetrofitService {
    @GET("suggestion/app/{query}")
    suspend fun getAutoCompleteSuggestions(@Path("query") query: String): SearchSuggestionsResponse
  }

}