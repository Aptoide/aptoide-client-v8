package cm.aptoide.pt.appcoins.repository

import cm.aptoide.pt.appcoins.Gamification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GamificationRepository @Inject constructor(
  private val gamificationApi: GamificationApi,
) {

  suspend fun getGamification(): Gamification {
    return withContext(Dispatchers.IO) {
      return@withContext gamificationApi.getLevels()
        .takeIf { it.status.equals("ACTIVE") }
        ?.let { Gamification(it.result) }
        ?: throw IllegalStateException()
    }
  }

  interface GamificationApi {
    @GET("gamification/levels")
    suspend fun getLevels(): LevelsResponse
  }
}
