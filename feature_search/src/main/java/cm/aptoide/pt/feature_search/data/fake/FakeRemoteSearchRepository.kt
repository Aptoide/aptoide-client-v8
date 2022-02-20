package cm.aptoide.pt.feature_search.data.fake

import cm.aptoide.pt.feature_search.data.network.RemoteSearchRepository
import cm.aptoide.pt.feature_search.data.network.model.TopSearchAppJsonList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRemoteSearchRepository : RemoteSearchRepository {

  override fun getTopSearchedApps(): Flow<List<TopSearchAppJsonList>> {
    val fakeList = arrayListOf(
      TopSearchAppJsonList("Top app 1"),
      TopSearchAppJsonList("Top app 2"),
      TopSearchAppJsonList("top app 3")
    )
    return flowOf(fakeList)
  }


}