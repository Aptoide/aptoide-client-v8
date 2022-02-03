package cm.aptoide.pt.feature_apps.data

data class DataList<T>(
  var total: Int = 0,
  var count: Int = 0,
  var offset: Int = 0,
  var limit: Int? = null,
  var next: Int = 0,
  var hidden: Int = 0,
  var isLoaded: Boolean = false,
  var list: List<T>? = null
)