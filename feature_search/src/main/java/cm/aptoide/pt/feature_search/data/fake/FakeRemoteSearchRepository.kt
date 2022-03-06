package cm.aptoide.pt.feature_search.data.fake

import cm.aptoide.pt.feature_apps.data.network.model.BaseV7DataListResponse
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
    TODO("Not yet implemented")
  }

  override suspend fun searchApp(keyword: String): Response<BaseV7DataListResponse<SearchAppJsonList>> {
    TODO("Not yet implemented")
  }


}