package cm.aptoide.pt.feature_reactions.data.network

import androidx.annotation.Keep

@Keep
data class ReactionsJson(
  val total: Int,
  val top: List<TopReactionsJson>,
)


@Keep
data class TopReactionsJson(
  val type: String,
  val total: Int,
)
