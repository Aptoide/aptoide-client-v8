package com.aptoide.android.aptoidegames.gamesfeed.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GamesFeedVisibilityViewModel @Inject constructor(private val gamesFeedManager: GamesFeedManager) :
  ViewModel() {

  private val viewModelState = MutableStateFlow<GamesFeedVisibilityState?>(null)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      gamesFeedManager.shouldShowGamesFeed()
        .catch { throwable ->
          Timber.e(throwable, "Error while loading gamesfeed visibility.")
          viewModelState.update { GamesFeedVisibilityState(shouldShow = false) }
        }
        .collect { state ->
          viewModelState.update { state }
        }
    }
  }
}
