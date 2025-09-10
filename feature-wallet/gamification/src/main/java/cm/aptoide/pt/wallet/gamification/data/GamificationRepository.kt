package cm.aptoide.pt.wallet.gamification.data

import cm.aptoide.pt.wallet.gamification.domain.GamificationStats
import cm.aptoide.pt.wallet.gamification.domain.Levels

interface GamificationRepository {

  suspend fun getGamificationStats(wallet: String): Result<GamificationStats>

  suspend fun getLevels(wallet: String? = null, currency: String? = null): Result<Levels>
}
