package cm.aptoide.pt.device_api.error

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * RFC 9457 `application/problem+json` body returned on every non-2xx from the device API.
 * `detail` carries the public, showable reason on a 410.
 */
@Keep
data class Problem(
  @SerializedName("type") val type: String? = null,
  @SerializedName("title") val title: String? = null,
  @SerializedName("status") val status: Int? = null,
  @SerializedName("detail") val detail: String? = null,
  @SerializedName("instance") val instance: String? = null,
  // Only on the age-restricted problem
  @SerializedName("age_rating") val ageRating: Int? = null,
)
