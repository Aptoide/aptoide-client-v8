package cm.aptoide.pt.play_and_earn.sessions.domain

data class SessionInfo(
  val appliedSeconds: Int,
  val ttl: Int,
  val events: List<SessionEvent>
)

data class SessionEvent(
  val type: String,
  val missionTitle: String,
  val packageName: String,
  val earnedUnits: Int = 0,
  // "user" for a mission that belongs to the player rather than to the game in [packageName].
  val scope: String? = null,
)
