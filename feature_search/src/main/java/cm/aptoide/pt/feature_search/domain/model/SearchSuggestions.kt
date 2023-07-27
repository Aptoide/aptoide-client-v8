package cm.aptoide.pt.feature_search.domain.model

data class SearchSuggestions(
  val suggestionType: SearchSuggestionType,
  val suggestionsList: List<String>,
  val popularSearchList: List<String>,
)
