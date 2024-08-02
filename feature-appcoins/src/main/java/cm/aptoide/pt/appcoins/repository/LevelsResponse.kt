package cm.aptoide.pt.appcoins.repository

import androidx.annotation.Keep
import cm.aptoide.pt.appcoins.Level

@Keep
data class LevelsResponse(
  val result: List<Level>,
  val status: String?,
  val update_date: String?,
)
