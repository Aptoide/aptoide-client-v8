package cm.aptoide.pt.feature_search.data.fake

import cm.aptoide.pt.aptoide_network.data.network.*
import cm.aptoide.pt.feature_search.data.network.RemoteSearchRepository
import cm.aptoide.pt.feature_search.data.network.model.SearchAppJsonList
import cm.aptoide.pt.feature_search.data.network.model.TopSearchAppJsonList
import cm.aptoide.pt.feature_search.data.network.response.SearchAutoCompleteSuggestionsResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response

class FakeRemoteSearchRepository : RemoteSearchRepository {

  override fun getTopSearchedApps(): Flow<List<TopSearchAppJsonList>> {
    val fakeList = arrayListOf(
      TopSearchAppJsonList("Top app 1"),
      TopSearchAppJsonList("Top app 2"),
      TopSearchAppJsonList("top app 3")
    )
    return flowOf(fakeList)
  }

  override suspend fun getAutoCompleteSuggestions(keyword: String): Response<SearchAutoCompleteSuggestionsResponse> {
    val fakeList = arrayListOf("Suggestion 1", "Suggestion 2", "Suggestion 3")
    val autoCompleteSuggestionsResponse = SearchAutoCompleteSuggestionsResponse(fakeList)
    return Response.success(autoCompleteSuggestionsResponse)
  }

  override suspend fun searchApp(keyword: String): Response<BaseV7DataListResponse<SearchAppJsonList>> {
    val searchAppJsonList = SearchAppJsonList(
      "path to icon",
      "aptoide",
      File(Malware("trusted")),
      Stats(10, 10, Rating(2.3, 2.3), Rating(2.3, 2.3)), AppCoins(true, true)
    )
    val baseV7DataListResponse = BaseV7DataListResponse<SearchAppJsonList>()
    baseV7DataListResponse.datalist = createFakeDatalist()
    return Response.success(baseV7DataListResponse)
  }

  private fun createFakeDatalist(): DataList<SearchAppJsonList>? {
    return DataList(
      total = 100,
      count = 0,
      offset = 0,
      limit = 10,
      next = 10,
      hidden = 1,
      isLoaded = false,
      list = createSearchAppJsonList()
    )
  }

  private fun createSearchAppJsonList(): List<SearchAppJsonList> {
    return listOf(
      SearchAppJsonList(
        "path to icon",
        "aptoide",
        File(Malware("trusted")),
        Stats(10, 10, Rating(2.3, 2.3), Rating(2.3, 2.3)), AppCoins(true, true)
      ), SearchAppJsonList(
        "path to icon",
        "uploader",
        File(Malware("trusted")),
        Stats(10, 10, Rating(2.3, 2.3), Rating(2.3, 2.3)), AppCoins(true, true)
      )
    )
  }
}