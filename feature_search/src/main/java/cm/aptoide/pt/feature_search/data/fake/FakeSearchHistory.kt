package cm.aptoide.pt.feature_search.data.fake

import cm.aptoide.pt.feature_search.data.database.SearchHistoryRepository
import cm.aptoide.pt.feature_search.data.database.model.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber
import javax.inject.Singleton

@Singleton
class FakeSearchHistory : SearchHistoryRepository {
  override fun getSearchHistory(): Flow<List<SearchHistoryEntity>> {
    val fakeList = arrayListOf(
      SearchHistoryEntity("Ana of the Larenz"),
      SearchHistoryEntity("Joao of the Andreide"),
      SearchHistoryEntity("Filips of the gonc of alves")
    )
    return flowOf(fakeList)
  }

  override fun addAppToSearchHistory(searchHistory: SearchHistoryEntity) {
    Timber.d("Saved app " + searchHistory.appName)
  }

  override fun removeAppFromSearchHistory(searchHistory: SearchHistoryEntity) {
    Timber.d("Removed app : " + searchHistory.appName)
  }
}