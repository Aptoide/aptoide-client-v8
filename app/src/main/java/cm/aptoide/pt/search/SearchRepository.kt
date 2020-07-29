package cm.aptoide.pt.search

import android.content.SharedPreferences
import cm.aptoide.pt.app.DownloadStateParser
import cm.aptoide.pt.app.migration.AppcMigrationManager
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
import cm.aptoide.pt.download.view.DownloadStatusModel
import cm.aptoide.pt.install.InstallManager
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
import java.util.concurrent.TimeUnit


class SearchRepository(val storeRepository: RoomStoreRepository,
                       val bodyInterceptor: BodyInterceptor<BaseBody>,
                       val httpClient: OkHttpClient,
                       val converterFactory: Converter.Factory,
                       val tokenInvalidator: TokenInvalidator,
                       val sharedPreferences: SharedPreferences,
                       val appBundlesVisibilityManager: AppBundlesVisibilityManager,
                       val installManager: InstallManager,
                       val appcMigrationManager: AppcMigrationManager,
                       val downloadStateParser: DownloadStateParser,
                       val oemidProvider: OemidProvider) {

  private var cachedSearchResults: SearchResult? = null
  private val subject: PublishSubject<SearchResult> = PublishSubject.create()

  fun observeSearchResults(): Observable<SearchResult> {
    return subject
        .switchMap { result ->
          val list = result.searchResultsList
          if (list.isNotEmpty() && list[0].isHighlightedResult) {
            return@switchMap Observable.mergeDelayError(Observable.just(result),
                observeHighlightedSearchResult(result))
          }

          return@switchMap Observable.just(result)
        }
  }

  fun generalSearch(query: String, filters: SearchFilters,
                    matureEnabled: Boolean): Completable {
    return Single.just(query)
        .flatMapCompletable { search(query, filters, matureEnabled, null) }
        .subscribeOn(Schedulers.io())
  }

  fun searchInStore(query: String, filters: SearchFilters, matureEnabled: Boolean,
                    storeName: String): Completable {
    return Single.just(query)
        .flatMapCompletable { search(query, filters, matureEnabled, storeName) }
        .subscribeOn(Schedulers.io())
  }

  private fun getHighlightedSearchResultList(result: SearchResult): Observable<SearchResult> {
    return Observable.just(
        SearchResult(result.query, result.specificStore, result.searchResultsList,
            result.filters, result.currentOffset, result.nextOffset, result.total,
            result.loading, result.error, false))
  }

  private fun observeHighlightedSearchResult(ogResult: SearchResult): Observable<SearchResult> {
    val first = ogResult.searchResultsList[0]
    return Observable.combineLatest(getHighlightedSearchResultList(ogResult),
        loadDownloadModel(first.getMd5(), first.getPackageName(), first.getVersionCode(), null,
            first.storeId, first.hasAdvertising() || first.hasBilling())) { r, downloadModel ->
      val list = ArrayList(r.searchResultsList)
      list[0] = SearchAppResult(list[0], downloadModel)
      return@combineLatest SearchResult(r.query, r.specificStore, list, r.filters, r.currentOffset,
          r.nextOffset,
          r.total,
          r.loading, r.error, r.redrawList)
    }
  }

  private fun loadDownloadModel(md5: String, packageName: String,
                                versionCode: Int, signature: String?,
                                storeId: Long,
                                hasAppc: Boolean): Observable<DownloadStatusModel>? {
    return Observable.combineLatest(installManager.getInstall(md5, packageName, versionCode),
        appcMigrationManager.isMigrationApp(packageName, signature, versionCode, storeId,
            hasAppc)) { install, isMigration ->
      DownloadStatusModel(downloadStateParser.parseStatusDownloadType(install.type, isMigration),
          install.progress,
          downloadStateParser.parseStatusDownloadState(install.state, install.isIndeterminate))
    }.throttleLast(200, TimeUnit.MILLISECONDS)
  }


  private fun search(query: String, filters: SearchFilters,
                     matureEnabled: Boolean, specificStore: String?): Completable {
    cachedSearchResults?.let { activeResults ->
      if (activeResults.query == query && activeResults.specificStore == specificStore
          && filters == activeResults.filters && !activeResults.hasError()) {
        if (activeResults.hasMore()) {
          return requestSearchResults(query, filters, activeResults.nextOffset, matureEnabled,
              specificStore)
              .flatMapCompletable { results -> updateMemCache(results) }
        }
        return Completable.fromAction {
          subject.onNext(activeResults)
        }
      }
    }
    return requestSearchResults(query, filters, 0, matureEnabled, specificStore)
        .flatMapCompletable { results -> updateMemCache(results) }
  }

  private fun updateMemCache(results: SearchResult?): Completable {
    return Completable.fromAction {
      results?.let { r ->
        cachedSearchResults.let { cached ->
          var list = ArrayList(r.searchResultsList)
          var shouldRedrawList = true
          if (cached != null && cached.query == r.query && cached.filters == r.filters
              && cached.specificStore == r.specificStore) {
            list = ArrayList(cached.searchResultsList)
            list.addAll(r.searchResultsList)
            shouldRedrawList = false
          }

          subject.onNext(SearchResult(r.query, r.specificStore, list,
              r.filters,
              r.currentOffset, r.nextOffset, r.total,
              r.loading, r.error, shouldRedrawList))

          cachedSearchResults =
              SearchResult(r.query, r.specificStore, list, r.filters,
                  r.currentOffset,
                  r.nextOffset,
                  r.total,
                  r.loading, r.error, shouldRedrawList)
        }
      }
    }
  }

  private fun requestSearchResults(query: String, filters: SearchFilters,
                                   nextOffset: Int,
                                   matureEnabled: Boolean,
                                   specificStore: String?): Single<SearchResult> {
    val authMap = StoreUtils.getSubscribedStoresAuthMap(storeRepository)

    // General search
    var request = ListSearchAppsRequest.of(query, nextOffset, filters.onlyFollowedStores,
        filters.onlyTrustedApps,
        filters.onlyBetaApps, filters.onlyAppcApps, matureEnabled,
        StoreUtils.getSubscribedStoresIds(storeRepository), authMap, bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences,
        appBundlesVisibilityManager)

    // For specific store search
    specificStore?.let { store ->
      request = ListSearchAppsRequest.of(query, nextOffset, store,
          filters.onlyTrustedApps, filters.onlyBetaApps, filters.onlyAppcApps, matureEnabled,
          authMap, bodyInterceptor,
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
      return Observable.just(r.dataList.list)
          .flatMap { list -> mapToSearchAppResultList(list, query) }
          .first()
          .map { list ->
            SearchResult(query, specificStore, list, filters,
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

  private fun mapToSearchAppResultList(
      searchAppList: List<SearchApp>,
      query: String): Observable<List<SearchAppResult>> {
    val newList: ArrayList<SearchAppResult> = ArrayList()
    for ((i, app) in searchAppList.withIndex()) {
      newList.add(
          SearchAppResult(app, oemidProvider.oemid, isHighlightedResult(i, app, query)))
    }
    return Observable.just(newList)
  }

  private fun isHighlightedResult(i: Int,
                                  app: SearchApp,
                                  query: String): Boolean {
    return i == 0 && isQuerySameAsAppName(app.name,
        query) && app.file.malware.rank == Malware.Rank.TRUSTED && app.stats.pdownloads >= 1000000
  }

  private fun isQuerySameAsAppName(appName: String, query: String): Boolean {
    return appName.toLowerCase().replace(" ", "").replace("-", "") == query.toLowerCase()
        .replace(" ", "").replace("-", "")
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