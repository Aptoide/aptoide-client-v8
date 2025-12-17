package com.aptoide.android.aptoidegames.gamegenie.domain

data class Suggestion(
  val suggestion: String,
  val emoji: String?,
)

data class CompanionSuggestions(
  val suggestions: List<Suggestion>,
  val language: String,
  val selectedGame: String,
)
