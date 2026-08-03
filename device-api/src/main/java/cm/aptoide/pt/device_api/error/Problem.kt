package cm.aptoide.pt.device_api.error

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * RFC 9457 `application/problem+json` body returned on every non-2xx from the
 * device/editorial APIs. `detail` carries the public, user-showable reason on a
 * 410 (removed by serving policy).
 */
@Keep
data class Problem(
  val type: String? = null,
  val title: String? = null,
  val status: Int? = null,
  @SerializedName("detail") val detail: String? = null,
  val instance: String? = null,
)
