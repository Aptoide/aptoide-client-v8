package cm.aptoide.pt.feature_gamegenie.domain

data class GameGenieChat(
  val id: String,
  val title: String,
  val conversation: List<ChatInteraction>,
)
