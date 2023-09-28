package cm.aptoide.pt.feature_search.data

import cm.aptoide.pt.feature_apps.data.toDomainModel
import cm.aptoide.pt.feature_campaigns.CampaignRepository
import cm.aptoide.pt.feature_campaigns.data.CampaignUrlNormalizer
import cm.aptoide.pt.feature_search.data.database.SearchHistoryRepository
import cm.aptoide.pt.feature_search.data.database.model.SearchHistoryEntity
import cm.aptoide.pt.feature_search.data.network.RemoteSearchRepository
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.PopularAppSearchResult
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.SearchAppResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideSearchRepository @Inject constructor(
  private val campaignRepository: CampaignRepository,
  private val campaignUrlNormalizer: CampaignUrlNormalizer,
  private val searchHistoryRepository: SearchHistoryRepository,
  private val remoteSearchRepository: RemoteSearchRepository,
  private val autoCompleteSuggestionsRepository: AutoCompleteSuggestionsRepository,
) : SearchRepository {

  override fun searchApp(keyword: String): Flow<SearchAppResult> {
    return flow {
      val searchResponse = remoteSearchRepository.searchApp(keyword)
      if (searchResponse.isSuccessful) {
        searchResponse.body()?.datalist?.list?.let {
          val adListId = UUID.randomUUID().toString()
          emit(SearchAppResult.Success(it.map { appJSON ->
            appJSON.toDomainModel(
              campaignRepository = campaignRepository,
              campaignUrlNormalizer = campaignUrlNormalizer,
              adListId = adListId
            )
          }))
        }
      } else {
        emit(SearchAppResult.Error(IllegalStateException()))
      }
    }.flowOn(Dispatchers.IO)
  }

  override fun getSearchHistory(): Flow<List<String>> {
    return searchHistoryRepository.getSearchHistory()
      .map { it.map { historyApp -> historyApp.appName } }
  }

  override suspend fun addAppToSearchHistory(appName: String) {
    withContext(Dispatchers.IO) {
      searchHistoryRepository.addAppToSearchHistory(SearchHistoryEntity(appName))
    }
  }

  override suspend fun removeAppFromSearchHistory(appName: String) {
    withContext(Dispatchers.IO) {
      searchHistoryRepository.removeAppFromSearchHistory(appName)
    }
  }

  override fun getAutoCompleteSuggestions(keyword: String) =
    autoCompleteSuggestionsRepository.getAutoCompleteSuggestions(keyword)

  override fun getTopSearchedApps(): Flow<PopularAppSearchResult> {
    return flow {
      val topSearchAppsResponse = remoteSearchRepository.getTopSearchedApps()
      if (topSearchAppsResponse.isSuccessful) {
        topSearchAppsResponse.body()?.datalist?.list?.let {
          val randomAdListId = UUID.randomUUID().toString()
          emit(
            PopularAppSearchResult.Success(
              it.map { topSearchApp ->
                topSearchApp.toDomainModel(
                  campaignRepository = campaignRepository,
                  campaignUrlNormalizer = campaignUrlNormalizer,
                  adListId = randomAdListId
                ).name
              })
          )
        }
      } else {
        emit(PopularAppSearchResult.Error(IllegalStateException()))
      }
    }
  }
}
