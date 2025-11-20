package cm.aptoide.pt.wallet.gamification.data

import cm.aptoide.pt.wallet.datastore.CurrencyPreferencesDataSource
import cm.aptoide.pt.wallet.gamification.data.model.GamificationResponse
import cm.aptoide.pt.wallet.gamification.data.model.GamificationStatus
import cm.aptoide.pt.wallet.gamification.data.model.LevelResponse
import cm.aptoide.pt.wallet.gamification.data.model.LevelsResponse
import cm.aptoide.pt.wallet.gamification.data.model.PromotionsResponse
import cm.aptoide.pt.wallet.gamification.domain.GamificationLevelStatus
import cm.aptoide.pt.wallet.gamification.domain.GamificationStats
import cm.aptoide.pt.wallet.gamification.domain.Level
import cm.aptoide.pt.wallet.gamification.domain.Levels
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

internal class DefaultGamificationRepository @Inject constructor(
  private val gamificationApi: GamificationApi,
  private val currencyPreferencesDataSource: CurrencyPreferencesDataSource,
  private val dispatcher: CoroutineDispatcher
) : GamificationRepository {

  override suspend fun getGamificationStats(wallet: String, currency: String?) = try {
    getUserStats(wallet = wallet, currency = currency).let { userStats ->
      val gamificationResponse = userStats.promotions.firstOrNull { it.id == "GAMIFICATION" }

      val gamificationStats = gamificationResponse?.toDomainModel()
        ?: GamificationStats(gamificationStatus = GamificationLevelStatus.NONE)
      Result.success(gamificationStats)
    }
  } catch (e: Throwable) {
    e.printStackTrace()
    Result.failure(e)
  }

  override suspend fun getLevels(wallet: String?, currency: String?) =
    withContext(dispatcher) {
      try {
        val levels = gamificationApi.getLevels().toDomainModel()
        Result.success(levels)
      } catch (e: Throwable) {
        e.printStackTrace()
        Result.failure(e)
      }
    }

  private suspend fun getUserStats(wallet: String, currency: String?) = withContext(dispatcher) {
    gamificationApi.getUserStats(
      wallet,
      Locale.getDefault().language,
      currency ?: currencyPreferencesDataSource.getPreferredCurrency()
    )
  }
}

private fun GamificationResponse.toDomainModel(): GamificationStats = GamificationStats(
  level = level,
  bonus = bonus,
  totalSpend = totalSpend,
  nextLevelAmount = nextLevelAmount,
  isActive = status == PromotionsResponse.Status.ACTIVE,
  gamificationStatus = gamificationStatus?.toDomainModel() ?: GamificationLevelStatus.NONE
)

private fun LevelsResponse.toDomainModel(): Levels = Levels(
  levelList = levels.map(LevelResponse::toDomainModel),
  updateDate = updateDate
)

private fun LevelResponse.toDomainModel(): Level = Level(
  amount = amount,
  bonus = bonus,
  level = level
)

private fun GamificationStatus.toDomainModel(): GamificationLevelStatus {
  return when (this) {
    GamificationStatus.NONE -> GamificationLevelStatus.NONE
    GamificationStatus.STANDARD -> GamificationLevelStatus.STANDARD
    GamificationStatus.APPROACHING_NEXT_LEVEL -> GamificationLevelStatus.APPROACHING_NEXT_LEVEL
    GamificationStatus.APPROACHING_VIP -> GamificationLevelStatus.APPROACHING_VIP
    GamificationStatus.VIP -> GamificationLevelStatus.VIP
    GamificationStatus.APPROACHING_VIP_MAX -> GamificationLevelStatus.APPROACHING_VIP_MAX
    GamificationStatus.VIP_MAX -> GamificationLevelStatus.VIP_MAX
  }
}
