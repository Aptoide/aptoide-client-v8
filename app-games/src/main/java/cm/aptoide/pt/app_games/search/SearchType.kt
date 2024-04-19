package cm.aptoide.pt.app_games.search

enum class SearchType(val type: String) {
  RECENT("recent"),
  POPULAR("popular"),
  AUTO_COMPLETE("suggested"),
  MANUAL("manual")
}
