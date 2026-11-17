package cm.aptoide.pt.campaigns.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

// Response of GET /api/v1/users/me/progress.
@Keep
internal data class PaEUserProgressJson(
  @SerializedName("lifetime_play_seconds") val lifetimePlaySeconds: Long?,
  @SerializedName("lifetime_units") val lifetimeUnits: Long?,
  val missions: List<PaEUserMissionJson>?,
  val streaks: List<PaEGameStreakJson>?,
)

@Keep
internal data class PaEUserMissionJson(
  val title: String?,
  val description: String?,
  val icon: String?,
  // Kept as text: a type this app doesn't know must drop one mission, not the whole response.
  val type: String?,
  val units: Int?,
  val period: String?,
  val progress: PaEUserMissionProgressJson?,
  @SerializedName("is_next") val isNext: Boolean?,
)

@Keep
internal data class PaEUserMissionProgressJson(
  val current: Int?,
  val target: Int?,
  val type: String?,
  val status: String?,
)

@Keep
internal data class PaEGameStreakJson(
  @SerializedName("package") val packageName: String?,
  val days: List<Boolean>?,
  @SerializedName("days_played") val daysPlayed: Int?,
)

// Response of GET /api/v1/leaderboard/weekly.
@Keep
internal data class PaELeaderboardJson(
  val entries: List<PaELeaderboardEntryJson>?,
  val me: PaELeaderboardEntryJson?,
)

@Keep
internal data class PaELeaderboardEntryJson(
  val rank: Int?,
  val player: String?,
  @SerializedName("play_seconds") val playSeconds: Long?,
  @SerializedName("is_me") val isMe: Boolean?,
)
