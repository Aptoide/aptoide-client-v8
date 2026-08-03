package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.feature_apps.domain.Review

/**
 * Read-only reviews list for an app. Backed by the device API in aptoideGamesDev;
 * elsewhere there is no reviews source, so [EmptyReviewsRepository] returns empty.
 */
interface ReviewsRepository {
  suspend fun getReviews(packageName: String): List<Review>
}

/** No-op used on non-device variants (v7 has no reviews-list surface). */
class EmptyReviewsRepository : ReviewsRepository {
  override suspend fun getReviews(packageName: String): List<Review> = emptyList()
}
