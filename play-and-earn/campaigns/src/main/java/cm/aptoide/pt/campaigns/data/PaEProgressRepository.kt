package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.domain.PaELeaderboard
import cm.aptoide.pt.campaigns.domain.PaEUserProgress

interface PaEProgressRepository {
  suspend fun getMyProgress(): Result<PaEUserProgress>

  suspend fun getWeeklyLeaderboard(limit: Int = 10): Result<PaELeaderboard>
}
