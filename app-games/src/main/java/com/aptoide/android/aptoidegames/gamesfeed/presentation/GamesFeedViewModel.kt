package com.aptoide.android.aptoidegames.gamesfeed.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptoide.android.aptoidegames.gamesfeed.repository.GamesFeedItem
import com.aptoide.android.aptoidegames.gamesfeed.repository.GamesFeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

sealed class GamesFeedUiState {
  data class Idle(
    val items: List<GamesFeedItem>,
    val bundleGraphic: String? = null,
    val bundleIcon: String? = null
  ) : GamesFeedUiState()

  object Loading : GamesFeedUiState()
  object Empty : GamesFeedUiState()
  object NoConnection : GamesFeedUiState()
  object Error : GamesFeedUiState()
}

@HiltViewModel
class GamesFeedViewModel @Inject constructor(
  private val gamesFeedRepository: GamesFeedRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow<GamesFeedUiState>(GamesFeedUiState.Loading)

  val uiState = _uiState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      _uiState.value
    )

  init {
    loadGamesFeed()
  }

  fun loadGamesFeed() {
    viewModelScope.launch {
      _uiState.update { GamesFeedUiState.Loading }
      try {
        val data = gamesFeedRepository.getGamesFeed()
        _uiState.update {
          if (data.items.isEmpty()) {
            GamesFeedUiState.Empty
          } else {
            GamesFeedUiState.Idle(data.items, data.bundleGraphic, data.bundleIcon)
          }
        }
      } catch (e: Exception) {
        e.printStackTrace()
        _uiState.update {
          if (e is IOException) {
            GamesFeedUiState.NoConnection
          } else {
            GamesFeedUiState.Error
          }
        }
      }
    }
  }
}
