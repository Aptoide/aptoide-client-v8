package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.aptoide_network.domain.UrlsCache
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.BundleSource.AUTOMATIC
import cm.aptoide.pt.feature_home.domain.Type.APP_GRID
import cm.aptoide.pt.feature_home.domain.WidgetAction
import cm.aptoide.pt.feature_home.domain.WidgetActionType.BUTTON
import cm.aptoide.pt.feature_home.domain.randomBundle
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.di.DefaultTrendingUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TRENDING_TAG = "apps-group-trending"
private const val TRENDING_MORE_TAG = "apps-group-trending-more"

@HiltViewModel
class BundlesInjectionsProvider @Inject constructor(
  val urlsCache: UrlsCache,
  @DefaultTrendingUrl val defaultTrendingUrl: String,
) : ViewModel()

@Composable
fun rememberTrendingBundle(): Bundle? = runPreviewable(
  preview = { randomBundle },
  real = {
    val injectionsProvider = hiltViewModel<BundlesInjectionsProvider>()
    var url by remember { mutableStateOf<String?>(null) }
    var moreUrl by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(injectionsProvider) {
      url = injectionsProvider.urlsCache.get(id = TRENDING_TAG)
      if (url.isNullOrEmpty()) {
        url = injectionsProvider.defaultTrendingUrl
        injectionsProvider.urlsCache.putAll(mapOf(TRENDING_TAG to url!!))
      }
      moreUrl = injectionsProvider.urlsCache.get(id = TRENDING_MORE_TAG)
      isLoading = false
    }
    if (isLoading) {
      return@runPreviewable null
    }
    Bundle(
      title = stringResource(R.string.fixed_bundle_trending_title),
      actions = if (moreUrl.isNullOrEmpty()) emptyList() else listOf(
        WidgetAction(
          type = BUTTON,
          tag = TRENDING_MORE_TAG,
          url = moreUrl!!
        )
      ),
      type = APP_GRID,
      tag = TRENDING_TAG,
      view = "",
      bundleSource = AUTOMATIC,
      timestamp = "0"
    )
  }
)
