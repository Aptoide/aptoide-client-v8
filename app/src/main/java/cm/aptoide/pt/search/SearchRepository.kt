package cm.aptoide.pt.search

import android.content.SharedPreferences
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

class SearchRepository(val searchFilterManager: SearchFilterManager,
                       val storeRepository: RoomStoreRepository,
                       val bodyInterceptor: BodyInterceptor<BaseBody>,
                       val httpClient: OkHttpClient,
                       val converterFactory: Converter.Factory,
                       val tokenInvalidator: TokenInvalidator,
                       val sharedPreferences: SharedPreferences,
                       val appBundlesVisibilityManager: AppBundlesVisibilityManager) {

  private var unfilteredSearchResults: SearchResult? = null
  private var activeSearchResults: SearchResult? = null


  fun generalSearch(query: String, filters: SearchFilters,
                    matureEnabled: Boolean): Single<SearchResult> {
    // TODO: This should be redone tu properly use schedulers.io without this ugly mess
    return Single.just(query)
        .flatMap { search(query, filters, matureEnabled) }
        .subscribeOn(Schedulers.io())
  }

  private fun search(query: String, filters: SearchFilters,
                     matureEnabled: Boolean): Single<SearchResult> {
    // If we have previous active search results, check if it has the same filters as this request
    // If it does, get next results for this search, or return the previous results if it's finished
    activeSearchResults?.let { activeResults ->
      if (activeResults.query == query && filters == activeResults.filters) {
        if (activeResults.hasMore()) {
          return requestSearchResults(query, filters, activeResults.nextOffset, matureEnabled)
              .flatMap { results -> Single.just(updateMemCache(results)) }
        }
        return Single.just(activeResults)
      }
    }
    // There's no active search results for these filters, so we take unfiltered search results and
    // filter them locally first, if possible.
    unfilteredSearchResults?.let { unfilteredResults ->
      // If it includes a beta filter, we don't filter locally
      if (unfilteredResults.query == query && !unfilteredResults.hasError() && !filters.onlyBetaApps) {
        val appsList = searchFilterManager.filterSearchResults(unfilteredResults.appsList, filters)
        activeSearchResults =
            SearchResult(query, appsList, filters, unfilteredResults.currentOffset,
                appsList.size, appsList.size + 1, false, null)
        // New object so there's no reference between our mem cache and the returned value
        return Single.just(SearchResult(query, appsList, filters, unfilteredResults.currentOffset,
            unfilteredResults.nextOffset, unfilteredResults.total, false, null))
      }
    }
    return requestSearchResults(query, filters, 0, matureEnabled)
        .flatMap { results -> Single.just(updateMemCache(results)) }
  }

  private fun updateMemCache(results: SearchResult?): SearchResult? {
    var res = results
    results?.let { r ->
      if (r.filters?.isFiltersActive() == false) {
        unfilteredSearchResults.let { uR ->
          var list = ArrayList(r.appsList)
          if (uR != null && uR.query == r.query) {
            list = ArrayList(uR.appsList)
            list.addAll(r.appsList)
          }
          unfilteredSearchResults =
              SearchResult(r.query, list, r.filters, r.currentOffset, r.nextOffset, r.total,
                  r.loading, r.error)
        }
      }
      activeSearchResults.let { aR ->
        var list = ArrayList(r.appsList)
        if (aR != null && aR.query == r.query && aR.filters == r.filters) {
          list = ArrayList(aR.appsList)
          list.addAll(r.appsList)
        }
        activeSearchResults =
            SearchResult(r.query, list, r.filters, r.currentOffset, r.nextOffset, r.total,
                r.loading, r.error)
        res = SearchResult(r.query, list, r.filters, r.currentOffset, r.nextOffset, r.total,
            r.loading, r.error)
      }
    }
    return res
  }

  private fun requestSearchResults(query: String, filters: SearchFilters,
                                   nextOffset: Int, matureEnabled: Boolean): Single<SearchResult> {
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
            SearchResult(query, list, filters, r.dataList.offset, r.dataList.next, r.dataList.total,
                !r.dataList.isLoaded, null)
          }
          .toSingle()
    }
    return Single.just(SearchResult(query, SearchResultError.GENERIC))
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