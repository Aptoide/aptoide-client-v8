package cm.aptoide.pt.feature_reactions.data.network

data class ReactionsJson(
  val total: Int,
  val top: List<TopReactionsJson>,
)


data class TopReactionsJson(
  val type: String,
  val total: Int,
)
