package com.aptoide.android.aptoidegames.gamegenie.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieLocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameGenieViewModelInjectionsProvider @Inject constructor(
  val gameGenieUseCase: GameGenieUseCase,
  val gameGenieLocalRepository: GameGenieLocalRepository,
) : ViewModel()

@Composable
fun rememberGameGenieHistoryUiState(): ConversationHistoryUIState =
  runPreviewable(
    preview = {
      ConversationHistoryUIState.Loading
    }, real = {
      val injectionsProvider = hiltViewModel<GameGenieViewModelInjectionsProvider>()
      val vm: ConversationHistoryViewModel = viewModel(
        key = "ConversationHistoryViewModel",
        factory = object : ViewModelProvider.Factory {
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ConversationHistoryViewModel(
              gameGenieUseCase = injectionsProvider.gameGenieUseCase,
            ) as T
          }
        }
      )
      val uiState by vm.uiState.collectAsState()
      uiState
    })