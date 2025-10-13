package cm.aptoide.pt.play_and_earn.sessions.domain

data class SessionInfo(
  val appliedSeconds: Int,
  val ttl: Int,
  val events: List<SessionEvent>
)

data class SessionEvent(
  val type: String,
  val missionTitle: String,
  val packageName: String
)
