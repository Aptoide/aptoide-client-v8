package cm.aptoide.pt.feature_search.domain.model

enum class SearchSuggestionType(val title: String) {
  SEARCH_HISTORY("Recent searches"), TOP_APTOIDE_SEARCH("Top Aptoide Searches"),
  AUTO_COMPLETE("Auto-complete")
}