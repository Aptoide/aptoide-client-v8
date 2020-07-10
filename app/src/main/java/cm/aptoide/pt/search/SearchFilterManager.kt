package cm.aptoide.pt.search

import cm.aptoide.aptoideviews.filters.Filter
import cm.aptoide.pt.dataprovider.model.v7.Malware
import cm.aptoide.pt.search.model.SearchAppResult
import cm.aptoide.pt.search.model.SearchFilterType
import cm.aptoide.pt.search.model.SearchFilters
import cm.aptoide.pt.store.RoomStoreRepository
import cm.aptoide.pt.store.StoreUtils

class SearchFilterManager(val storeRepository: RoomStoreRepository) {


  fun filterSearchResults(list: List<SearchAppResult>,
                          filters: SearchFilters): List<SearchAppResult> {
    return list.filter { app ->
      if (filters.onlyFollowedStores && !StoreUtils.getSubscribedStoresIds(storeRepository)
              .contains(app.storeId)) {
        return@filter false
      }
      if (filters.onlyAppcApps && !app.isAppcApp) {
        return@filter false
      }
      if (filters.onlyTrustedApps && app.rank != Malware.Rank.TRUSTED.ordinal) {
        return@filter false
      }
      // TODO: Missing beta filter because of WS...
      return@filter true
    }
  }

  fun getSearchFilters(viewFilters: List<Filter>): SearchFilters {
    var onlyFollowedStores = false
    var onlyTrustedApps = false
    var onlyBetaApps = false
    var onlyAppcApps = false
    for (filter in viewFilters) {
      if (filter.identifier == null) continue
      when (filter.identifier) {
        SearchFilterType.FOLLOWED_STORES.name -> {
          onlyFollowedStores = filter.selected
        }
        SearchFilterType.TRUSTED.name -> {
          onlyTrustedApps = filter.selected
        }
        SearchFilterType.BETA.name -> {
          onlyBetaApps = filter.selected
        }
        SearchFilterType.APPC.name -> {
          onlyAppcApps = filter.selected
        }
      }
    }
    return SearchFilters(onlyFollowedStores, onlyTrustedApps, onlyBetaApps, onlyAppcApps)
  }
}