package cm.aptoide.pt.feature_search.data.network.service

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.aptoide_network.di.RetrofitBuzz
import cm.aptoide.pt.aptoide_network.di.RetrofitV7
import cm.aptoide.pt.feature_search.data.network.RemoteSearchRepository
import cm.aptoide.pt.feature_search.data.network.model.SearchAppJsonList
import cm.aptoide.pt.feature_search.data.network.model.TopSearchAppJsonList
import cm.aptoide.pt.feature_search.data.network.response.SearchAutoCompleteSuggestionsResponse
import cm.aptoide.pt.feature_search.domain.repository.SearchStoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRetrofitService @Inject constructor(
  @RetrofitBuzz private val autoCompleteSearchSuggestionsService: AutoCompleteSearchRetrofitService,
  @RetrofitV7 private val searchAppRetrofitService: SearchAppRetrofitService,
  private val searchStoreManager: SearchStoreManager,
) :
  RemoteSearchRepository {

  override suspend fun getAutoCompleteSuggestions(keyword: String): Response<SearchAutoCompleteSuggestionsResponse> {
    return autoCompleteSearchSuggestionsService.getAutoCompleteSuggestions(keyword)
  }

  override suspend fun searchApp(keyword: String): Response<BaseV7DataListResponse<SearchAppJsonList>> {
    return if (searchStoreManager.shouldAddStore()) {
      searchAppRetrofitService.searchApp(keyword, 15, searchStoreManager.getStore())
    } else {
      searchAppRetrofitService.searchApp(keyword, 15, null)
    }
  }

  interface AutoCompleteSearchRetrofitService {
    @GET("/v1/suggestion/app/{query}")
    suspend fun getAutoCompleteSuggestions(
      @Path(value = "query", encoded = true) query: String,
    ): Response<SearchAutoCompleteSuggestionsResponse>
  }

  interface SearchAppRetrofitService {
    @GET("listSearchApps")
    suspend fun searchApp(
      @Query(value = "query", encoded = true) query: String,
      @Query(value = "limit") limit: Int,
      @Query(value = "store_name") storeName: String? = null,
    ): Response<BaseV7DataListResponse<SearchAppJsonList>>
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