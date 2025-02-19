package com.aptoide.android.aptoidegames.gamegenie.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationHistoryViewModel @Inject constructor(
  private val gameGenieUseCase: GameGenieUseCase,
) : ViewModel() {

  private val viewModelState =
    MutableStateFlow<ConversationHistoryUIState>(ConversationHistoryUIState.Loading)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      gameGenieUseCase.getAllChats().map { chats ->
        val pastConversations = chats.reversed()
        viewModelState.update {
          ConversationHistoryUIState.Idle(pastConversations, onDeleteChat = { deleteChat(it) })
        }
      }.catch { cause: Throwable -> cause.printStackTrace() }
        .collect()
    }
  }

  private fun deleteChat(id: String) {
    viewModelScope.launch {
      gameGenieUseCase.deleteChat(id)
    }
  }
}
