package cm.aptoide.pt.search.model

sealed class SearchResult {
  data class Success(val result: List<SearchAppResult>) : SearchResult()
  data class Error(val error: SearchResultError) : SearchResult()
}

enum class SearchResultError {
  NO_NETWORK, GENERIC
}