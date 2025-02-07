package com.aptoide.android.aptoidegames.gamegenie.presentation

import com.aptoide.android.aptoidegames.gamegenie.domain.ConversationInfo

sealed class ConversationHistoryUIState {
  object Loading : ConversationHistoryUIState()

  data class Idle(
    val pastConversations: List<ConversationInfo>,
    val onDeleteChat: (String) -> Unit,
  ) : ConversationHistoryUIState()
}
