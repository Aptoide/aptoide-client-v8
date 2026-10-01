package cm.aptoide.pt.feature_search.data.deviceapi

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

/** Typo tolerant suggestions for what is being typed. */
interface DeviceApiSuggestService {

  @GET("android/v1/search/suggest")
  suspend fun suggest(
    @Query("q") q: String,
    @Query("limit") limit: Int?,
    @Query("variant") variant: String,
  ): SuggestResponse
}

@Keep
data class SuggestResponse(
  @SerializedName("suggestions") val suggestions: List<SuggestionResponse>? = null,
)

@Keep
data class SuggestionResponse(
  @SerializedName("term") val term: String? = null,
)
