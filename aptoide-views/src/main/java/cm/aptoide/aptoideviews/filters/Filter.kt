package cm.aptoide.aptoideviews.filters

data class Filter(val name: String, val selected: Boolean) {
  internal var id: Int = -1
}