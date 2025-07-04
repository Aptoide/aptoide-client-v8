package com.aptoide.android.aptoidegames.search

import cm.aptoide.pt.feature_apps.data.AppMapper
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.feature_search.data.AutoCompleteSuggestionsRepository
import cm.aptoide.pt.feature_search.data.database.SearchHistoryRepository
import cm.aptoide.pt.feature_search.data.database.model.SearchHistoryEntity
import cm.aptoide.pt.feature_search.data.network.RemoteSearchRepository
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.PopularAppSearchResult
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.SearchAppResult
import com.aptoide.android.aptoidegames.gamegenie.presentation.GameGenieManager
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
class AppGamesSearchRepository @Inject constructor(
  private val mapper: AppMapper,
  private val searchHistoryRepository: SearchHistoryRepository,
  private val remoteSearchRepository: RemoteSearchRepository,
  private val autoCompleteSuggestionsRepository: AutoCompleteSuggestionsRepository,
  private val gameGenieManager: GameGenieManager,
  private val featureFlags: FeatureFlags,
) : SearchRepository {
  override fun searchApp(keyword: String): Flow<SearchAppResult> = flow {
    when (featureFlags.getFlagAsString("search_game_genie")) {
      "genie" -> {
        val apps = try {
          val result = gameGenieManager.searchApp(keyword)
          result.list.map { mapper.map(it) }
        } catch (e: Exception) {
          emptyList()
        }
        emit(SearchAppResult.Success(apps))
      }
      "genie_with_store" -> {
        val apps = try {
          val result = gameGenieManager.searchApp(keyword, "aptoide-games")
          result.list.map { mapper.map(it) }
        } catch (e: Exception) {
          emptyList()
        }
        emit(SearchAppResult.Success(apps))
      }
      else -> {
        val apps = try {
          val searchResponse = remoteSearchRepository.searchApp(keyword)
          if (searchResponse.isSuccessful) {
            searchResponse.body()?.datalist?.list?.map { mapper.map(it) } ?: emptyList()
          } else emptyList()
        } catch (e: Exception) {
          emptyList()
        }
        emit(SearchAppResult.Success(apps))
      }
    }
  }.flowOn(Dispatchers.IO)

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
              it.map { mapper.map(it, randomAdListId).name })
          )
        }
      } else {
        emit(PopularAppSearchResult.Error(IllegalStateException()))
      }
    }
  }
}
