package cm.aptoide.pt.feature_search.data.fake

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.aptoide_network.data.network.base_response.DataList
import cm.aptoide.pt.aptoide_network.data.network.model.AppCoins
import cm.aptoide.pt.aptoide_network.data.network.model.File
import cm.aptoide.pt.aptoide_network.data.network.model.Malware
import cm.aptoide.pt.aptoide_network.data.network.model.Rating
import cm.aptoide.pt.aptoide_network.data.network.model.Signature
import cm.aptoide.pt.aptoide_network.data.network.model.Stats
import cm.aptoide.pt.aptoide_network.data.network.model.Votes
import cm.aptoide.pt.feature_apps.data.model.AppJSON
import cm.aptoide.pt.feature_apps.data.model.Appearance
import cm.aptoide.pt.feature_apps.data.model.CampaignUrls
import cm.aptoide.pt.feature_apps.data.model.Store
import cm.aptoide.pt.feature_search.data.network.RemoteSearchRepository
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

  override suspend fun searchApp(keyword: String): Response<BaseV7DataListResponse<AppJSON>> {
    val baseV7DataListResponse = BaseV7DataListResponse<AppJSON>()
    baseV7DataListResponse.datalist = createFakeDatalist()
    return Response.success(baseV7DataListResponse)
  }

  private fun createFakeDatalist(): DataList<AppJSON>? {
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

  private fun createSearchAppJsonList(): List<AppJSON> {
    return listOf(
      AppJSON(
        icon = "path to icon",
        name = "aptoide",
        packageName = "cm.aptoide.pt",
        store = Store(
          id = 0,
          name = "name",
          avatar = "",
          appearance = Appearance("", ""),
          stats = null,
        ),
        file = File(
          "vername",
          123,
          "12313123213",
          10212,
          "01-01-1994",
          "path",
          "path_alt",
          Signature("dasdas", "filipe"),
          Malware("trusted"),
          arrayListOf("dasdsa", "asdad"),
          arrayListOf("permission 1", "permission2")
        ),
        stats = Stats(
          10,
          10,
          Rating(2.3, 213, arrayListOf(Votes(1, 2), Votes(1, 2))),
          Rating(2.3, 23, arrayListOf(Votes(1, 2), Votes(1, 2)))
        ),
        urls = CampaignUrls(null, null),
        appcoins = AppCoins(true, true)
      ),
      AppJSON(
        icon = "path to icon",
        name = "aptoide",
        packageName = "cm.aptoide.pt",
        store = Store(
          id = 0,
          name = "name",
          avatar = "",
          appearance = Appearance("", ""),
          stats = null,
        ),
        file = File(
          "vername",
          123,
          "12313123213",
          10212,
          "01-01-1994",
          "path",
          "path_alt",
          Signature("dasdas", "filipe"),
          Malware("trusted"),
          arrayListOf("dasdsa", "asdad"),
          arrayListOf("permission 1", "permission2")
        ),
        stats = Stats(
          10,
          10,
          Rating(2.3, 213, arrayListOf(Votes(1, 2), Votes(1, 2))),
          Rating(2.3, 23, arrayListOf(Votes(1, 2), Votes(1, 2)))
        ),
        urls = CampaignUrls(null, null),
        appcoins = AppCoins(true, true)
      ),
    )
  }
}
