package com.aptoide.android.aptoidegames.gamegenie.io_models

import androidx.annotation.Keep
import com.aptoide.android.aptoidegames.gamegenie.domain.Suggestion
import com.aptoide.android.aptoidegames.gamegenie.domain.CompanionSuggestions
import com.google.gson.annotations.SerializedName

@Keep
data class CompanionSuggestionResponse(
  @SerializedName("suggestion") val suggestion: String,
  @SerializedName("emoji") val emoji: String,
)

@Keep
data class CompanionSuggestionsResponse(
  @SerializedName("suggestions") val suggestions: List<CompanionSuggestionResponse>,
  @SerializedName("language") val language: String,
  @SerializedName("selected_game") val selectedGame: String,
)

fun CompanionSuggestionsResponse.toDomain(): CompanionSuggestions {
  return CompanionSuggestions(
    suggestions = suggestions.map {
      Suggestion(
        suggestion = it.suggestion,
        emoji = it.emoji
      )
    },
    language = language,
    selectedGame = selectedGame
  )
}
