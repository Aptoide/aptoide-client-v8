package com.aptoide.android.aptoidegames.appview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_apps.data.ReviewsRepository
import cm.aptoide.pt.feature_apps.domain.Review
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Read-only reviews list for the app-view Reviews tab. Anonymous, device-API only
 * (`GET /apps/{package}/reviews`); renders nothing when there are no reviews (v7
 * variants use [cm.aptoide.pt.feature_apps.data.EmptyReviewsRepository], and the
 * tab itself is hidden when empty).
 */
@Composable
fun AppReviewsSection(packageName: String) {
  val reviews = rememberReviews(packageName)
  if (reviews.isEmpty()) return

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    reviews.take(20).forEach { ReviewRow(it) }
  }
}

@Composable
private fun ReviewRow(review: Review) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Row {
      Text(
        text = review.authorName?.takeIf { it.isNotBlank() }
          ?: stringResource(R.string.appview_reviews_anonymous),
        style = AGTypography.BodyBold,
        color = Palette.White,
      )
      review.rating?.let {
        Spacer(Modifier.width(8.dp))
        Text(text = "★ $it", style = AGTypography.Body, color = Palette.Primary)
      }
    }
    review.title?.takeIf { it.isNotBlank() }?.let {
      Text(text = it, style = AGTypography.BodyBold, color = Palette.GreyLight)
    }
    Text(
      text = review.body,
      style = AGTypography.Body,
      color = Palette.GreyLight,
      maxLines = 6,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

@Composable
internal fun rememberReviews(packageName: String): List<Review> = runPreviewable(
  preview = { emptyList() },
  real = {
    val injectionsProvider = hiltViewModel<ReviewsInjectionsProvider>()
    val vm: ReviewsViewModel = viewModel(
      key = "reviews/$packageName",
      factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return ReviewsViewModel(packageName, injectionsProvider.reviewsRepository) as T
        }
      },
    )
    val reviews by vm.reviews.collectAsState()
    reviews
  },
)

@HiltViewModel
class ReviewsInjectionsProvider @Inject constructor(
  val reviewsRepository: ReviewsRepository,
) : ViewModel()

class ReviewsViewModel(
  packageName: String,
  reviewsRepository: ReviewsRepository,
) : ViewModel() {
  private val _reviews = MutableStateFlow<List<Review>>(emptyList())
  val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

  init {
    viewModelScope.launch {
      _reviews.value = runCatching { reviewsRepository.getReviews(packageName) }.getOrDefault(emptyList())
    }
  }
}
