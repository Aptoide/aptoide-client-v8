package com.aptoide.android.aptoidegames.search

enum class SearchType(val type: String) {
  RECENT("recent"),
  POPULAR("popular"),
  AUTO_COMPLETE("suggested"),
  MANUAL("manual")
}
