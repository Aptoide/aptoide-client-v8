package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

sealed class TextSegment {
  data class Plain(val text: String) : TextSegment()
  data class Bold(val text: String) : TextSegment()
  data class Link(
    val text: String,
    val url: String,
  ) : TextSegment()
}
