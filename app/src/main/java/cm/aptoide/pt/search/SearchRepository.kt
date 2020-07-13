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


class SearchRepository(val searchFilterManager: SearchFilterManager,
                       val storeRepository: RoomStoreRepository,
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
        .flatMap { search(query, filters, matureEnabled) }
        .subscribeOn(Schedulers.io())
  }

  private fun search(query: String, filters: SearchFilters,
                     matureEnabled: Boolean): Single<SearchResult> {
    cachedSearchResults?.let { activeResults ->
      if (activeResults.query == query && filters == activeResults.filters) {
        if (activeResults.hasMore()) {
          return requestSearchResults(query, filters, activeResults.nextOffset, matureEnabled)
              .flatMap { results -> Single.just(updateMemCache(results)) }
        }
        return Single.just(activeResults)
      }
    }
    return requestSearchResults(query, filters, 0, matureEnabled)
        .flatMap { results -> Single.just(updateMemCache(results)) }
  }

  private fun updateMemCache(results: SearchResult?): SearchResult? {
    var res = results
    results?.let { r ->
      cachedSearchResults.let { cached ->
        var list = ArrayList(r.searchResultDiffModel.searchResultsList)
        if (cached != null && cached.query == r.query && cached.filters == r.filters) {
          list = ArrayList(cached.searchResultDiffModel.searchResultsList)
          list.addAll(r.searchResultDiffModel.searchResultsList)
        }

        res = SearchResult(r.query,
            calculateSearchListDifferences(list,
                cached?.searchResultDiffModel?.searchResultsList ?: Collections.emptyList()),
            r.filters,
            r.currentOffset, r.nextOffset, r.total,
            r.loading, r.error)

        cachedSearchResults =
            SearchResult(r.query, SearchResultDiffModel(null, list), r.filters, r.currentOffset,
                r.nextOffset,
                r.total,
                r.loading, r.error)
      }
    }
    return res
  }

  private fun requestSearchResults(query: String, filters: SearchFilters,
                                   nextOffset: Int,
                                   matureEnabled: Boolean): Single<SearchResult> {
    return ListSearchAppsRequest.of(query, nextOffset, filters.onlyFollowedStores,
        filters.onlyTrustedApps,
        filters.onlyBetaApps,
        StoreUtils.getSubscribedStoresIds(storeRepository), bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences, matureEnabled,
        appBundlesVisibilityManager)
        .observe(false)
        .toSingle()
        .flatMap { response -> mapToSearchResult(query, response, filters) }
        .onErrorResumeNext { throwable ->
          throwable.printStackTrace()
          handleSearchError(query, throwable)
        }
  }

  private fun mapToSearchResult(query: String, response: ListSearchApps?,
                                filters: SearchFilters): Single<SearchResult> {
    response?.let { r ->
      return Observable.just(r)
          .map { data -> data.dataList.list }
          .flatMapIterable { list -> list }
          .map { searchApp -> SearchAppResult(searchApp) }
          .toList()
          .first()
          .map { list ->
            SearchResult(query, SearchResultDiffModel(null, list), filters, r.dataList.offset,
                r.dataList.next, r.dataList.total,
                !r.dataList.isLoaded, null)
          }
          .toSingle()
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
                                throwable: Throwable): Single<SearchResult?>? {
    return if (throwable is UnknownHostException
        || throwable is NoNetworkConnectionException) {
      Single.just(
          SearchResult(query, SearchResultError.NO_NETWORK))
    } else Single.just(
        SearchResult(query, SearchResultError.GENERIC))
  }

//  fun searchInStore(query: String, filters: SearchFilters, matureEnabled: Boolean,
//                    storeName: String): Single<SearchResult> {
// TODO
//  }
}