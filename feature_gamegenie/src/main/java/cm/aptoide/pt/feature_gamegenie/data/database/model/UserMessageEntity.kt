package cm.aptoide.pt.feature_gamegenie.data.database.model

import androidx.annotation.Keep

@Keep
data class UserMessageEntity(
  val text: String,
  val image: String? = null,
)
