package cm.aptoide.pt.feature_gamegenie.data

import cm.aptoide.pt.feature_gamegenie.io_models.CompanionSuggestionsResponse
import cm.aptoide.pt.feature_gamegenie.io_models.GameGenieCompanionRequest
import cm.aptoide.pt.feature_gamegenie.io_models.GameGenieRequest
import cm.aptoide.pt.feature_gamegenie.io_models.GameGenieResponse
import cm.aptoide.pt.feature_gamegenie.io_models.TokenResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface GameGenieApiService {
  @POST("v2/chat")
  suspend fun postMessage(
    @Header("Authorization") bearerToken: String,
    @Body request: GameGenieRequest,
  ): GameGenieResponse

  @POST("v2/chat/companion")
  suspend fun postMessageCompanion(
    @Header("Authorization") bearerToken: String,
    @Body request: GameGenieCompanionRequest,
  ): GameGenieResponse

  @GET("v2/chat/companion/suggestions")
  suspend fun getCompanionSuggestions(
    @Header("Authorization") bearerToken: String,
    @Query("selected_game") selectedGame: String,
    @Query("lang") lang: String,
  ): CompanionSuggestionsResponse

  @POST("token")
  suspend fun getToken(): TokenResponse
}
