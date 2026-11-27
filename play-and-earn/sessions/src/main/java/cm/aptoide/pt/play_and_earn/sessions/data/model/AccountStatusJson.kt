package cm.aptoide.pt.play_and_earn.sessions.data.model

import androidx.annotation.Keep

/** `GET /api/v1/account/status` (AND-951). A missing field tells nothing. */
@Keep
internal data class AccountStatusJson(
  val restricted: Boolean?,
)
