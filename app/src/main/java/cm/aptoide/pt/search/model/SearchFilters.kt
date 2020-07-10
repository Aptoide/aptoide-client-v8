package cm.aptoide.pt.search.model

data class SearchFilters(val onlyFollowedStores: Boolean, val onlyTrustedApps: Boolean,
                         val onlyBetaApps: Boolean, val onlyAppcApps: Boolean) {

  fun isFiltersActive(): Boolean {
    return onlyFollowedStores || onlyTrustedApps || onlyBetaApps || onlyAppcApps
  }
}