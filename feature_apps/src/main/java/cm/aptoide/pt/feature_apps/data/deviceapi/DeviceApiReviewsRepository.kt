package cm.aptoide.pt.feature_apps.data.deviceapi

import androidx.annotation.Keep
import cm.aptoide.pt.device_api.error.deviceApiCall
import cm.aptoide.pt.feature_apps.data.ReviewsRepository
import cm.aptoide.pt.feature_apps.domain.Review
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Device-API reviews (aptoideGamesDev only). `GET /apps/{package}/reviews` — merged
 * corpus, newest first. Single-shot first page (UI has no load-more).
 */
class DeviceApiReviewsRepository(
  private val service: Service,
  private val scope: CoroutineScope,
) : ReviewsRepository {

  override suspend fun getReviews(packageName: String): List<Review> =
    withContext(scope.coroutineContext) {
      deviceApiCall { service.getReviews(packageName) }.items.orEmpty().mapNotNull { it.toReview() }
    }

  private fun ReviewResponse.toReview(): Review? {
    val id = id ?: return null
    return Review(
      id = id,
      authorName = authorName,
      authorAvatarUrl = authorAvatarUrl,
      rating = rating,
      title = title,
      body = body.orEmpty(),
      createdAt = createdAt,
      helpfulVotes = helpfulVotes ?: 0,
    )
  }

  interface Service {
    @GET("android/v1/apps/{package_name}/reviews")
    suspend fun getReviews(
      @Path("package_name") packageName: String,
      @Query("cursor") cursor: String? = null,
      @Query("limit") limit: Int? = null,
    ): ReviewsResponse
  }
}

@Keep
data class ReviewsResponse(
  val items: List<ReviewResponse>? = null,
  val nextCursor: String? = null,
)

@Keep
data class ReviewResponse(
  val id: String? = null,
  val authorName: String? = null,
  val authorAvatarUrl: String? = null,
  val rating: Int? = null,
  val title: String? = null,
  val body: String? = null,
  val createdAt: String? = null,
  val helpfulVotes: Int? = null,
)
