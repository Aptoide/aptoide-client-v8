package cm.aptoide.pt.feature_search.data.network

import cm.aptoide.pt.feature_search.data.network.model.TopSearchAppJsonList
import kotlinx.coroutines.flow.Flow

interface RemoteSearchRepository {
  fun getTopSearchedApps(): Flow<List<TopSearchAppJsonList>>
}