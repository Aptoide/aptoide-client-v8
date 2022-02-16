package cm.aptoide.pt.feature_search.data.fake

import cm.aptoide.pt.feature_search.data.database.LocalSearchHistoryRepository
import cm.aptoide.pt.feature_search.data.database.model.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalSearchHistory : LocalSearchHistoryRepository {
  override fun getSearchHistory(): Flow<List<SearchHistoryEntity>> {
    val fakeList = arrayListOf<SearchHistoryEntity>(
      SearchHistoryEntity("Filipe"),
      SearchHistoryEntity("Joao"),
      SearchHistoryEntity("Ana Larenz")
    )
    return flowOf(fakeList)
  }

  override suspend fun addAppToSearchHistory(appName: String) {
    TODO("Not yet implemented")
  }
}