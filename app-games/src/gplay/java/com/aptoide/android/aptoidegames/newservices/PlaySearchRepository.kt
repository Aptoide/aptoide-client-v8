package com.aptoide.android.aptoidegames.newservices

import cm.aptoide.pt.aptoide_network.di.V7Backend
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppsDataSource
import cm.aptoide.pt.feature_search.data.deviceapi.DeviceApiSuggestDataSource
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.AutoCompleteResult
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.PopularAppSearchResult
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.SearchAppResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The search of the Play build. Searching and its suggestions are answered by the new
 * services, over their whole catalog - they take no category along with a query. The history
 * is kept on the device and the popular searches only v7 knows, so both stay where they were.
 */
@Singleton
internal class PlaySearchRepository @Inject constructor(
  @V7Backend private val v7: SearchRepository,
  private val apps: DeviceApiAppsDataSource,
  private val suggestions: DeviceApiSuggestDataSource,
) : SearchRepository {

  override fun searchApp(keyword: String): Flow<SearchAppResult> = flow {
    val result = try {
      SearchAppResult.Success(apps.search(query = keyword))
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      SearchAppResult.Error(e)
    }
    emit(result)
  }

  override fun getAutoCompleteSuggestions(keyword: String): Flow<AutoCompleteResult> = flow {
    emit(AutoCompleteResult.Success(suggestions.suggest(query = keyword)))
  }

  override fun getTopSearchedApps(): Flow<PopularAppSearchResult> = v7.getTopSearchedApps()

  override fun getSearchHistory(): Flow<List<String>> = v7.getSearchHistory()

  override suspend fun addAppToSearchHistory(appName: String) =
    v7.addAppToSearchHistory(appName)

  override suspend fun removeAppFromSearchHistory(appName: String) =
    v7.removeAppFromSearchHistory(appName)
}
