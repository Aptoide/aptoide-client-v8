package cm.aptoide.pt.search

import android.content.SharedPreferences
import androidx.recyclerview.widget.DiffUtil
import cm.aptoide.pt.dataprovider.aab.AppBundlesVisibilityManager
import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator
import cm.aptoide.pt.dataprovider.model.v7.search.ListSearchApps
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody
import cm.aptoide.pt.dataprovider.ws.v7.ListSearchAppsRequest
import cm.aptoide.pt.search.model.SearchAppResult
import cm.aptoide.pt.search.model.SearchFilters
import cm.aptoide.pt.search.model.SearchResult
import cm.aptoide.pt.search.model.SearchResultError
import cm.aptoide.pt.store.RoomStoreRepository
import cm.aptoide.pt.store.StoreUtils
import okhttp3.OkHttpClient
import retrofit2.Converter
import rx.Observable
import rx.Single
import rx.schedulers.Schedulers
import java.net.UnknownHostException
import java.util.*
import kotlin.collections.ArrayList


class SearchRepository(val storeRepository: RoomStoreRepository,
                       val bodyInterceptor: BodyInterceptor<BaseBody>,
                       val httpClient: OkHttpClient,
                       val converterFactory: Converter.Factory,
                       val tokenInvalidator: TokenInvalidator,
                       val sharedPreferences: SharedPreferences,
                       val appBundlesVisibilityManager: AppBundlesVisibilityManager) {

  private var cachedSearchResults: SearchResult? = null


  fun generalSearch(query: String, filters: SearchFilters,
                    matureEnabled: Boolean): Single<SearchResult> {
    return Single.just(query)
        .flatMap { search(query, filters, matureEnabled, null) }
        .subscribeOn(Schedulers.io())
  }

  fun searchInStore(query: String, filters: SearchFilters, matureEnabled: Boolean,
                    storeName: String): Single<SearchResult> {
    return Single.just(query)
        .flatMap { search(query, filters, matureEnabled, storeName) }
        .subscribeOn(Schedulers.io())
  }

  private fun search(query: String, filters: SearchFilters,
                     matureEnabled: Boolean, specificStore: String?): Single<SearchResult> {
    cachedSearchResults?.let { activeResults ->
      if (activeResults.query == query && activeResults.specificStore == specificStore
          && filters == activeResults.filters && !activeResults.hasError()) {
        if (activeResults.hasMore()) {
          return requestSearchResults(query, filters, activeResults.nextOffset, matureEnabled,
              specificStore)
              .flatMap { results -> Single.just(updateMemCache(results)) }
        }
        return Single.just(activeResults)
      }
    }
    return requestSearchResults(query, filters, 0, matureEnabled, specificStore)
        .flatMap { results -> Single.just(updateMemCache(results)) }
  }

  private fun updateMemCache(results: SearchResult?): SearchResult? {
    var res = results
    results?.let { r ->
      cachedSearchResults.let { cached ->
        var list = ArrayList(r.searchResultDiffModel.searchResultsList)
        if (cached != null && cached.query == r.query && cached.filters == r.filters
            && cached.specificStore == r.specificStore) {
          list = ArrayList(cached.searchResultDiffModel.searchResultsList)
          list.addAll(r.searchResultDiffModel.searchResultsList)
        }

        res = SearchResult(r.query, r.specificStore,
            calculateSearchListDifferences(list,
                cached?.searchResultDiffModel?.searchResultsList ?: Collections.emptyList()),
            r.filters,
            r.currentOffset, r.nextOffset, r.total,
            r.loading, r.error)

        cachedSearchResults =
            SearchResult(r.query, r.specificStore, SearchResultDiffModel(null, list), r.filters,
                r.currentOffset,
                r.nextOffset,
                r.total,
                r.loading, r.error)
      }
    }
    return res
  }

  private fun requestSearchResults(query: String, filters: SearchFilters,
                                   nextOffset: Int,
                                   matureEnabled: Boolean,
                                   specificStore: String?): Single<SearchResult> {
    val authMap = StoreUtils.getSubscribedStoresAuthMap(storeRepository)

    // General search
    var request = ListSearchAppsRequest.of(query, nextOffset, filters.onlyFollowedStores,
        filters.onlyTrustedApps,
        filters.onlyBetaApps, matureEnabled,
        StoreUtils.getSubscribedStoresIds(storeRepository), authMap, bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences,
        appBundlesVisibilityManager)

    // For specific store search
    specificStore?.let { store ->
      request = ListSearchAppsRequest.of(query, nextOffset, store,
          filters.onlyTrustedApps, filters.onlyBetaApps, matureEnabled, authMap, bodyInterceptor,
          httpClient,
          converterFactory, tokenInvalidator, sharedPreferences,
          appBundlesVisibilityManager)
    }
    return request
        .observe(false)
        .toSingle()
        .flatMap { response -> mapToSearchResult(query, response, filters, specificStore) }
        .onErrorResumeNext { throwable ->
          throwable.printStackTrace()
          handleSearchError(query, specificStore, throwable)
        }
  }

  private fun mapToSearchResult(query: String, response: ListSearchApps?,
                                filters: SearchFilters,
                                specificStore: String?): Single<SearchResult> {
    response?.let { r ->
      return Observable.just(r)
          .map { data -> data.dataList.list }
          .flatMapIterable { list -> list }
          .map { searchApp -> SearchAppResult(searchApp) }
          .toList()
          .first()
          .map { list ->
            SearchResult(query, specificStore, SearchResultDiffModel(null, list), filters,
                r.dataList.offset,
                r.dataList.next, r.dataList.total,
                !r.dataList.isLoaded, null)
          }
          .toSingle()
          .onErrorResumeNext { throwable ->
            throwable.printStackTrace()
            handleSearchError(query, specificStore, throwable)
          }
    }
    return Single.just(SearchResult(query, SearchResultError.GENERIC))
  }

  private fun calculateSearchListDifferences(
      newSearchList: List<SearchAppResult>,
      oldSearchList: List<SearchAppResult>): SearchResultDiffModel {

    val diffResult = DiffUtil.calculateDiff(
        SearchResultDiffCallback(oldSearchList, newSearchList))
    return SearchResultDiffModel(diffResult, newSearchList)

  }

  private fun handleSearchError(query: String,
                                specificStore: String?,
                                throwable: Throwable): Single<SearchResult?>? {
    return if (throwable is UnknownHostException
        || throwable is NoNetworkConnectionException) {
      Single.just(
          SearchResult(query, specificStore, SearchResultError.NO_NETWORK))
    } else Single.just(
        SearchResult(query, specificStore, SearchResultError.GENERIC))
  }

}