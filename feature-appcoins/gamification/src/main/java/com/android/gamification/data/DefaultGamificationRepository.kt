package com.android.gamification.data

import cm.aptoide.pt.datastore.CurrencyPreferencesDataSource
import com.android.gamification.data.model.GamificationResponse
import com.android.gamification.data.model.GamificationStatus
import com.android.gamification.data.model.LevelResponse
import com.android.gamification.data.model.LevelsResponse
import com.android.gamification.data.model.PromotionsResponse
import com.android.gamification.domain.GamificationStats
import com.android.gamification.domain.Level
import com.android.gamification.domain.Levels
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

class DefaultGamificationRepository @Inject constructor(
  private val gamificationApi: GamificationApi,
  private val currencyPreferencesDataSource: CurrencyPreferencesDataSource,
  private val dispatcher: CoroutineDispatcher
) : GamificationRepository {

  override suspend fun getGamificationStats(wallet: String) = getUserStats(wallet = wallet).let {
    val gamificationResponse = it.promotions.firstOrNull { it.id == "GAMIFICATION" }

    gamificationResponse?.toDomainModel()
      ?: GamificationStats(gamificationStatus = GamificationStatus.NONE)
  }

  override suspend fun getLevels(wallet: String?, currency: String?) =
    withContext(dispatcher) {
      gamificationApi.getLevels().toDomainModel()
    }

  private suspend fun getUserStats(wallet: String) = withContext(dispatcher) {
    gamificationApi.getUserStats(
      wallet,
      Locale.getDefault().language,
      currencyPreferencesDataSource.getPreferredCurrency()
    )
  }
}

fun GamificationResponse.toDomainModel(): GamificationStats = GamificationStats(
  level = level,
  bonus = bonus,
  totalSpend = totalSpend,
  nextLevelAmount = nextLevelAmount,
  isActive = status == PromotionsResponse.Status.ACTIVE,
  gamificationStatus = gamificationStatus ?: GamificationStatus.NONE
)

fun LevelsResponse.toDomainModel(): Levels = Levels(
  levelList = levels.map(LevelResponse::toDomainModel),
  updateDate = updateDate
)

fun LevelResponse.toDomainModel(): Level = Level(
  amount = amount,
  bonus = bonus,
  level = level
)
