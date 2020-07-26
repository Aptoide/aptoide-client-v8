package cm.aptoide.aptoideviews.filters

interface FiltersChangedEventListener {
  fun onFiltersChanged(filters: List<Filter>)
}