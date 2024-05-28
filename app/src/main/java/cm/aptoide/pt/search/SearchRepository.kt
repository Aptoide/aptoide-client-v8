package cm.aptoide.pt.search

import android.content.SharedPreferences
import cm.aptoide.pt.aab.Split
import cm.aptoide.pt.dataprovider.aab.AppBundlesVisibilityManager
import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator
import cm.aptoide.pt.dataprovider.model.v7.Malware
import cm.aptoide.pt.dataprovider.model.v7.search.ListSearchApps
import cm.aptoide.pt.dataprovider.model.v7.search.SearchApp
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody
import cm.aptoide.pt.dataprovider.ws.v7.ListSearchAppsRequest
import cm.aptoide.pt.download.OemidProvider
import cm.aptoide.pt.search.model.SearchAppResult
import cm.aptoide.pt.search.model.SearchFilters
import cm.aptoide.pt.search.model.SearchResult
import cm.aptoide.pt.search.model.SearchResultError
import cm.aptoide.pt.store.RoomStoreRepository
import cm.aptoide.pt.store.StoreUtils
import okhttp3.OkHttpClient
import retrofit2.Converter
import rx.Completable
import rx.Observable
import rx.Single
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject
import java.net.UnknownHostException
import java.util.Collections

class SearchRepository(
  val storeRepository: RoomStoreRepository,
  val bodyInterceptor: BodyInterceptor<BaseBody>,
  val httpClient: OkHttpClient,
  val converterFactory: Converter.Factory,
  val tokenInvalidator: TokenInvalidator,
  val sharedPreferences: SharedPreferences,
  val appBundlesVisibilityManager: AppBundlesVisibilityManager,
  val oemidProvider: OemidProvider
) {

  private var cachedSearchResults: SearchResult? = null
  private val resultsSubject: PublishSubject<SearchResult> = PublishSubject.create()

  private val cancelationSubject: PublishSubject<Void> = PublishSubject.create()
  private var loadingMore = false

  fun observeSearchResults(): Observable<SearchResult> {
    return resultsSubject
  }

  fun generalSearch(
    query: String, filters: SearchFilters,
    matureEnabled: Boolean,
    useCachedValues: Boolean
  ): Completable {
    return Single.just(query)
      .flatMapCompletable { search(query, filters, matureEnabled, null, useCachedValues) }
      .subscribeOn(Schedulers.io())
  }

  fun searchInStore(
    query: String, filters: SearchFilters,
    matureEnabled: Boolean,
    storeName: String,
    useCachedValues: Boolean
  ): Completable {
    return Single.just(query)
      .flatMapCompletable { search(query, filters, matureEnabled, storeName, useCachedValues) }
      .subscribeOn(Schedulers.io())
  }

  @Synchronized
  private fun search(
    query: String, filters: SearchFilters,
    matureEnabled: Boolean, specificStore: String?,
    useCachedValues: Boolean
  ): Completable {
    if (useCachedValues) {
      return Completable.fromAction {
        resultsSubject.onNext(cachedSearchResults)
      }
    } else {
      cachedSearchResults?.let { activeResults ->
        if (activeResults.query == query && activeResults.specificStore == specificStore
          && filters == activeResults.filters && !activeResults.hasError()
        ) {
          if (activeResults.hasMore()) {
            if (loadingMore) {
              return Completable.complete()
            }
            return requestSearchResults(
              query, filters, activeResults.nextOffset, matureEnabled,
              specificStore
            )
              .toObservable()
              .takeUntil(cancelationSubject)
              .defaultIfEmpty(null)
              .toSingle()
              .flatMapCompletable { results -> updateMemCache(results) }
              .doOnSubscribe { loadingMore = true }
              .doOnUnsubscribe { loadingMore = false }
              .doOnTerminate { loadingMore = false }
              .doOnError { loadingMore = false }
          }
          return Completable.fromAction {
            resultsSubject.onNext(activeResults)
          }
        }
      }
      // We immediately clear the cache on filter change to avoid any concurrency issues
      // E.g. Active Filters: F1 , then F1 & F2, then back to F1 before the 2nd response is retrieved
      // Without setting to null, the third request (last F1) will have the offsets of the first F1.
      cachedSearchResults = null
      // We also cancel emitting/storing any other responses from other requests if still ongoing
      cancelationSubject.onNext(null)

      return requestSearchResults(query, filters, 0, matureEnabled, specificStore)
        .toObservable()
        .takeUntil(cancelationSubject)
        .defaultIfEmpty(null)
        .toSingle()
        .flatMapCompletable { results -> updateMemCache(results) }
    }
  }

  @Synchronized
  private fun updateMemCache(results: SearchResult?): Completable {
    return Completable.fromAction {
      results?.let { r ->
        cachedSearchResults.let { cached ->
          var list = ArrayList(r.searchResultsList)
          var isFreshResponse = true
          if (cached != null && cached.query == r.query && cached.filters == r.filters
            && cached.specificStore == r.specificStore
          ) {
            list = ArrayList(cached.searchResultsList)
            list.addAll(r.searchResultsList)
            isFreshResponse = false
          }

          resultsSubject.onNext(
            SearchResult(
              r.query, r.specificStore, list,
              r.filters,
              r.currentOffset, r.nextOffset, r.total,
              r.loading, isFreshResponse, r.error
            )
          )

          cachedSearchResults =
            SearchResult(
              r.query, r.specificStore, list, r.filters,
              r.currentOffset,
              r.nextOffset,
              r.total,
              r.loading, isFreshResponse, r.error
            )
        }
      }
    }
  }

  private fun requestSearchResults(
    query: String, filters: SearchFilters,
    nextOffset: Int,
    matureEnabled: Boolean,
    specificStore: String?
  ): Single<SearchResult> {
    val authMap = StoreUtils.getSubscribedStoresAuthMap(storeRepository)

    // General search
    var request = ListSearchAppsRequest.of(
      query, nextOffset, filters.onlyFollowedStores,
      filters.onlyTrustedApps,
      filters.onlyBetaApps, filters.onlyAppcApps, matureEnabled,
      StoreUtils.getSubscribedStoresIds(storeRepository), authMap, bodyInterceptor, httpClient,
      converterFactory, tokenInvalidator, sharedPreferences,
      appBundlesVisibilityManager
    )

    // For specific store search
    specificStore?.let { store ->
      request = ListSearchAppsRequest.of(
        query, nextOffset, store,
        filters.onlyTrustedApps, filters.onlyBetaApps, filters.onlyAppcApps, matureEnabled,
        authMap, bodyInterceptor,
        httpClient,
        converterFactory, tokenInvalidator, sharedPreferences,
        appBundlesVisibilityManager
      )
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

  private fun mapToSearchResult(
    query: String, response: ListSearchApps?,
    filters: SearchFilters,
    specificStore: String?
  ): Single<SearchResult> {
    response?.let { r ->
      return Observable.just(r.dataList.list)
        .flatMap { list -> mapToSearchAppResultList(list, query) }
        .first()
        .map { list ->
          SearchResult(
            query, specificStore, list, filters,
            r.dataList.offset,
            r.dataList.next, r.dataList.total,
            !r.dataList.isLoaded, false, null
          )
        }
        .toSingle()
        .onErrorResumeNext { throwable ->
          throwable.printStackTrace()
          handleSearchError(query, specificStore, throwable)
        }
    }
    return Single.just(SearchResult(query, SearchResultError.GENERIC))
  }

  private fun mapToSearchAppResultList(
    searchAppList: List<SearchApp>,
    query: String
  ): Observable<List<SearchAppResult>> {
    val newList: ArrayList<SearchAppResult> = ArrayList()
    for ((i, app) in searchAppList.withIndex()) {
      newList.add(
        mapToSearchAppResult(app, oemidProvider.oemid, isHighlightedResult(i, app, query))
      )
    }
    return Observable.just(newList)
  }

  private fun mapToSearchAppResult(
    app: SearchApp, oemid: String,
    isHighlighted: Boolean
  ): SearchAppResult {
    var requiredSplits: List<String>? = Collections.emptyList()
    var splits: List<cm.aptoide.pt.dataprovider.model.v7.Split>? = Collections.emptyList()
    app.aab?.let { aab ->
      requiredSplits = aab.requiredSplits
      splits = aab.splits
    }

    return SearchAppResult(
      app.file.malware.rank.ordinal, app.icon, app.store.name,
      app.store
        .id,
      app.store.appearance.theme, app.modified.time, app.stats.rating.avg,
      app.stats.pdownloads.toLong(), app.name, app.packageName,
      app.file.md5sum, app.id, app.file.vercode, app.file.vername, app.file.path,
      app.file.pathAlt, app.file.malware, app.size, app.hasVersions(), app.hasBilling(),
      app.hasAdvertising(), oemid, isHighlighted, app.obb, requiredSplits,
      mapSplits(splits), null, null, app.store.name.equals("catappult"), ""
    )
  }

  private fun mapSplits(splits: List<cm.aptoide.pt.dataprovider.model.v7.Split>?): List<Split> {
    val splitsMapResult = ArrayList<Split>()
    if (splits == null) return splitsMapResult
    for (split in splits) {
      splitsMapResult.add(
        Split(
          split.name, split.type, split.path,
          split.filesize,
          split.md5sum
        )
      )
    }
    return splitsMapResult
  }

  private fun isHighlightedResult(
    i: Int,
    app: SearchApp,
    query: String
  ): Boolean {
    return i == 0 && isQuerySameAsAppName(
      app.name,
      query
    ) && app.file.malware.rank == Malware.Rank.TRUSTED && app.stats.pdownloads >= 1000000
  }

  private fun isQuerySameAsAppName(appName: String, query: String): Boolean {
    return appName.toLowerCase().replace(" ", "").replace("-", "") == query.toLowerCase()
      .replace(" ", "").replace("-", "")
  }

  private fun handleSearchError(
    query: String,
    specificStore: String?,
    throwable: Throwable
  ): Single<SearchResult?>? {
    return if (throwable is UnknownHostException
      || throwable is NoNetworkConnectionException
    ) {
      Single.just(
        SearchResult(query, specificStore, SearchResultError.NO_NETWORK)
      )
    } else Single.just(
      SearchResult(query, specificStore, SearchResultError.GENERIC)
    )
  }
}