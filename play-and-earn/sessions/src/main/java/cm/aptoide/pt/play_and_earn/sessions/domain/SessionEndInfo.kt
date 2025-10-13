package cm.aptoide.pt.play_and_earn.sessions.domain

data class SessionEndInfo(
  val appliedSeconds: Int,
  val missionsUpdated: Int,
  val lockCleared: Boolean?,
)
