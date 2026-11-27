package cm.aptoide.pt.play_and_earn.sessions.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/** `POST /api/v1/installs/click` (AND-954): a Play & Earn install started. */
@Keep
internal data class InstallClickRequestJson(
  @SerializedName("package") val packageName: String,
  @SerializedName("click_id") val clickId: String,
)

/**
 * `POST /api/v1/installs/report` (AND-954): the install of that click finished. A null field is
 * left out of the body: the backend takes both as optional.
 */
@Keep
internal data class InstallReportRequestJson(
  @SerializedName("package") val packageName: String,
  @SerializedName("click_id") val clickId: String,
  @SerializedName("installer") val installer: String?,
  @SerializedName("signature") val signature: String?,
)

/** The answer of both install calls. A missing field tells nothing. */
@Keep
internal data class InstallAnswerJson(
  val eligible: Boolean?,
)
