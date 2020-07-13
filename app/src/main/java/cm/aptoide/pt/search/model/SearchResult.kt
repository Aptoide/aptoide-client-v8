package cm.aptoide.pt.search.model

import cm.aptoide.pt.search.SearchResultDiffModel
import java.util.*

data class SearchResult @JvmOverloads constructor(
    val query: String,
    val searchResultDiffModel: SearchResultDiffModel = SearchResultDiffModel(null,
        Collections.emptyList()),
    val filters: SearchFilters? = null, val currentOffset: Int = -1,
    val nextOffset: Int = -1, val total: Int = -1, val loading: Boolean = false,
    val error: SearchResultError? = null) {

  constructor(query: String, error: SearchResultError) : this(query,
      SearchResultDiffModel(null, Collections.emptyList()), null,
      -1, -1, -1, false, error)

  fun hasError(): Boolean {
    return error != null
  }

  fun hasMore(): Boolean {
    return nextOffset < total && !loading && !hasError()
  }
}

enum class SearchResultError {
  NO_NETWORK, GENERIC
}