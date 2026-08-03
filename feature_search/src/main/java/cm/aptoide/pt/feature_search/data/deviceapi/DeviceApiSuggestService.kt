package cm.aptoide.pt.feature_search.data.deviceapi

import androidx.annotation.Keep
import retrofit2.http.GET
import retrofit2.http.Query

/** `GET /android/v1/search/suggest` — typo-tolerant autocomplete (degrades to empty). */
interface DeviceApiSuggestService {

  @GET("android/v1/search/suggest")
  suspend fun suggest(
    @Query("q") q: String,
    @Query("limit") limit: Int? = null,
  ): SuggestResponse
}

@Keep
data class SuggestResponse(val suggestions: List<SuggestionResponse>? = null)

@Keep
data class SuggestionResponse(val term: String? = null)
