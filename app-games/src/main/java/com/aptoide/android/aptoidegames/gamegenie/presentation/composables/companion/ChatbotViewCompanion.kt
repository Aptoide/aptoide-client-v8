package com.aptoide.android.aptoidegames.gamegenie.presentation.composables.companion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.error_views.NoConnectionView
import com.aptoide.android.aptoidegames.gamegenie.domain.Suggestion
import com.aptoide.android.aptoidegames.gamegenie.domain.GameCompanion
import com.aptoide.android.aptoidegames.gamegenie.presentation.GameGenieUIState
import com.aptoide.android.aptoidegames.gamegenie.presentation.GameGenieUIStateType
import com.aptoide.android.aptoidegames.home.LoadingView

@Composable
fun ChatbotViewCompanion(
  selectedGame: GameCompanion,
  firstLoad: Boolean,
  navigateBack: () -> Unit,
  uiState: GameGenieUIState,
  navigateTo: (String) -> Unit,
  onError: () -> Unit,
  onMessageSend: (String) -> Unit,
  setFirstLoadDone: () -> Unit,
  modifier: Modifier = Modifier,
  suggestions: List<Suggestion> = emptyList(),
  onSuggestionClick: (String, Int) -> Unit = { _, _ -> },
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .wrapContentSize(Alignment.TopCenter)
      .imePadding(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    val isLoading = uiState.type == GameGenieUIStateType.LOADING

    when (uiState.type) {
      GameGenieUIStateType.LOADING_CHAT -> LoadingView()
      GameGenieUIStateType.NO_CONNECTION -> NoConnectionView(onRetryClick = onError)
      GameGenieUIStateType.ERROR -> GenericErrorView(onError)
      else -> ChatScreenCompanion(
        uiState = uiState,
        firstLoad = firstLoad,
        setFirstLoadDone = setFirstLoadDone,
        navigateTo = navigateTo,
        onMessageSend = onMessageSend,
        selectedGame = selectedGame,
        navigateBack = navigateBack,
        isLoading = isLoading,
        suggestions = suggestions,
        onSuggestionClick = onSuggestionClick
      )
    }
  }
}
