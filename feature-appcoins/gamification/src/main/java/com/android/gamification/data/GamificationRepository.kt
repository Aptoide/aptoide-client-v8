package com.android.gamification.data

import com.android.gamification.domain.GamificationStats
import com.android.gamification.domain.Levels

interface GamificationRepository {

  suspend fun getGamificationStats(wallet: String): GamificationStats

  suspend fun getLevels(wallet: String? = null, currency: String? = null): Levels
}
