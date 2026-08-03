package cm.aptoide.pt.feature_apps.domain

/**
 * A user review (device API `GET /apps/{package}/reviews`, merged corpus). Read-only,
 * anonymous. Only populated on the aptoideGamesDev device path.
 */
data class Review(
  val id: String,
  val authorName: String?,
  val authorAvatarUrl: String?,
  val rating: Int?,
  val title: String?,
  val body: String,
  val createdAt: String?,
  val helpfulVotes: Int,
)
