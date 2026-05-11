package cm.aptoide.pt.feature_gamegenie.presentation

import cm.aptoide.pt.feature_gamegenie.domain.ConversationInfo

sealed class ConversationHistoryUIState {
  object Loading : ConversationHistoryUIState()

  data class Idle(
    val pastConversations: List<ConversationInfo>,
    val onDeleteChat: (String) -> Unit,
  ) : ConversationHistoryUIState()
}
