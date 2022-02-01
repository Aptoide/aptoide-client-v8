package cm.aptoide.pt.feature_search.data.network.service

import cm.aptoide.pt.feature_search.data.network.response.SearchSuggestionsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface SearchSuggestionsRetrofitService {

  @GET("suggestion/app/{query}")
  suspend fun getSearchSuggestions(@Path("query") query: String): SearchSuggestionsResponse
}