package cm.aptoide.pt.feature_search.data.network.service

import cm.aptoide.pt.feature_search.data.network.RemoteSearchRepository
import cm.aptoide.pt.feature_search.data.network.model.TopSearchAppJsonList
import cm.aptoide.pt.feature_search.data.network.response.SearchAutoCompleteSuggestionsResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

class SearchRetrofitService(private val autoCompleteSearchSuggestionsService: AutoCompleteSearchRetrofitService) :
  RemoteSearchRepository {

  override suspend fun getAutoCompleteSuggestions(keyword: String): Response<SearchAutoCompleteSuggestionsResponse> {
    return autoCompleteSearchSuggestionsService.getAutoCompleteSuggestions(keyword)
  }

  interface AutoCompleteSearchRetrofitService {
    @GET("/v1/suggestion/app/{query}")
    suspend fun getAutoCompleteSuggestions(
      @Path(value = "query", encoded = true) query: String
    ): Response<SearchAutoCompleteSuggestionsResponse>
  }

  override fun getTopSearchedApps(): Flow<List<TopSearchAppJsonList>> {
    val fakeList = arrayListOf(
      TopSearchAppJsonList("security breach game"),
      TopSearchAppJsonList("Mimicry: Online Horror Action"),
      TopSearchAppJsonList("Eyzacraft: Craft Master"),
      TopSearchAppJsonList("Blockman GO - Adventures"),
      TopSearchAppJsonList("Naughty Puzzle: Tricky Test"),
      TopSearchAppJsonList("Yu-Gi-Oh! Master Duel"),
      TopSearchAppJsonList("Cleaner"),
      TopSearchAppJsonList("DEEMO II"),
      TopSearchAppJsonList("Security Breach Game Helper"),
    )
    return flowOf(fakeList)
  }


}