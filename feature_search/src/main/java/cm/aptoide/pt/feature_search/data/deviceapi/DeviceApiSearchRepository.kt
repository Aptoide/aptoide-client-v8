package cm.aptoide.pt.feature_search.data.deviceapi

import cm.aptoide.pt.device_api.error.deviceApiCall
import cm.aptoide.pt.device_api.network.DeviceProfileProvider
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppsService
import cm.aptoide.pt.feature_apps.data.deviceapi.toApp
import cm.aptoide.pt.feature_search.data.database.SearchHistoryRepository
import cm.aptoide.pt.feature_search.data.database.model.SearchHistoryEntity
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.AutoCompleteResult
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.PopularAppSearchResult
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.SearchAppResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Device-API search (aptoideGamesDev only). Results via `GET /apps?q=` + device
 * params; autocomplete via `GET /search/suggest` (degrades to empty). Local search
 * history reuses the same Room DAO as v7. `getTopSearchedApps` (popular-search) has
 * no device endpoint (leftover) → empty.
 */
class DeviceApiSearchRepository(
  private val appsService: DeviceApiAppsService,
  private val suggestService: DeviceApiSuggestService,
  private val searchHistoryRepository: SearchHistoryRepository,
  private val storeName: String,
  private val variant: String,
  private val deviceProfileProvider: DeviceProfileProvider,
) : SearchRepository {

  override fun searchApp(keyword: String): Flow<SearchAppResult> = flow {
    val p = deviceProfileProvider.get()
    runCatching {
      deviceApiCall {
        appsService.searchOrBrowse(
          q = keyword, variant = variant,
          sdk = p.sdk, abi = p.abi, tv = p.tv, density = p.density,
        )
      }.items.orEmpty().map { it.toApp(storeName) }
    }.fold(
      onSuccess = { emit(SearchAppResult.Success(it)) },
      onFailure = { emit(SearchAppResult.Error(it)) },
    )
  }.flowOn(Dispatchers.IO)

  override fun getAutoCompleteSuggestions(keyword: String): Flow<AutoCompleteResult> = flow {
    val terms = runCatching {
      deviceApiCall { suggestService.suggest(q = keyword) }.suggestions.orEmpty().mapNotNull { it.term }
    }.getOrDefault(emptyList()) // suggest never surfaces an error state
    emit(AutoCompleteResult.Success(terms))
  }.flowOn(Dispatchers.IO)

  override fun getTopSearchedApps(): Flow<PopularAppSearchResult> = flow {
    emit(PopularAppSearchResult.Success(emptyList()))
  }

  override fun getSearchHistory(): Flow<List<String>> =
    searchHistoryRepository.getSearchHistory().map { list -> list.map { it.appName } }

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
}
