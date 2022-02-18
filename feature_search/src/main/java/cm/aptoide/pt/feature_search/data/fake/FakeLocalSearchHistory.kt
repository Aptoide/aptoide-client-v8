package cm.aptoide.pt.feature_search.data.fake

import cm.aptoide.pt.feature_search.data.database.LocalSearchHistoryRepository
import cm.aptoide.pt.feature_search.data.database.model.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeLocalSearchHistory: LocalSearchHistoryRepository {
  override fun getSearchHistory(): Flow<List<SearchHistoryEntity>> {
    val fakeList = arrayListOf(
      SearchHistoryEntity("Ana of the Larenz"),
      SearchHistoryEntity("Joao of the Andreide"),
      SearchHistoryEntity("Filips of the gonc of alves")
    )
    return flowOf(fakeList)
  }

  override suspend fun addAppToSearchHistory(searchHistory: SearchHistoryEntity) {
    TODO("Not yet implemented")
  }
}