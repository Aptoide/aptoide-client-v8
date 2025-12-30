package com.aptoide.android.aptoidegames.editorial

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_campaigns.AptoideMMPCampaign
import cm.aptoide.pt.feature_campaigns.UTMInfo
import cm.aptoide.pt.feature_editorial.domain.ArticleMeta
import cm.aptoide.pt.feature_editorial.presentation.ArticleListUiState
import cm.aptoide.pt.feature_editorial.presentation.previewArticlesListIdleState
import cm.aptoide.pt.feature_editorial.presentation.rememberEditorialListState
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.analytics.dto.BundleMeta
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.OverrideAnalyticsBundleMeta
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withItemPosition
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.error_views.NoConnectionView
import com.aptoide.android.aptoidegames.home.LoadingView
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar

private const val seeMoreEditorialsRoute = "seeMoreEditorials/{title}/{tag}?subtype={subtype}"

fun seeMoreEditorialScreen() = ScreenData.withAnalytics(
  route = seeMoreEditorialsRoute,
  screenAnalyticsName = "SeeAll",
  arguments = listOf(
    navArgument("subtype") {
      type = NavType.StringType
      nullable = true
    },
  ),
  deepLinks = listOf(navDeepLink {
    uriPattern = BuildConfig.DEEP_LINK_SCHEMA + seeMoreEditorialsRoute
  }),

  ) { arguments, navigate, navigateBack ->
  val title = arguments?.getString("title")!!
  val tag = arguments.getString("tag")!!

  val (uiState, reload) = rememberEditorialListState(
    tag = tag,
    subtype = arguments.getString("subtype")
  )

  SeeMoreEditorialsScreen(
    title = title,
    tag = tag,
    uiState = uiState,
    onError = reload,
    navigateBack = navigateBack,
    navigate = navigate,
  )
}

fun buildSeeMoreEditorialsRoute(
  title: String,
  bundleTag: String,
  subtype: String?
) = "seeMoreEditorials/$title/$bundleTag?subtype=$subtype"

@Composable
fun SeeMoreEditorialsScreen(
  title: String?,
  tag: String,
  uiState: ArticleListUiState,
  onError: () -> Unit,
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    AppGamesTopBar(navigateBack = navigateBack, title = title)
    SeeMoreEditorialsContent(
      tag = tag,
      uiState = uiState,
      navigate = navigate,
      onError = onError
    )
  }
}

@Composable
fun SeeMoreEditorialsContent(
  tag: String,
  uiState: ArticleListUiState,
  navigate: (String) -> Unit,
  onError: () -> Unit
) {
  LaunchedEffect(tag) {
    if (!AptoideMMPCampaign.allowedBundleTags.keys.contains(tag)) {
      AptoideMMPCampaign.allowedBundleTags[tag] = UTMInfo(
        utmMedium = "editorial",
        utmCampaign = "editorial",
        utmContent = "editorial-seeall"
      )
    }
  }

  val analyticsContext = AnalyticsContext.current
  val bundleMeta = analyticsContext.bundleMeta ?: BundleMeta(tag = tag, bundleSource = "manual")

  OverrideAnalyticsBundleMeta(
    bundleMeta = bundleMeta,
    navigate = navigate
  ) { navigateTo ->
    when (uiState) {
      is ArticleListUiState.Loading -> LoadingView()
      is ArticleListUiState.Error -> GenericErrorView(onRetryClick = onError)
      is ArticleListUiState.NoConnection -> NoConnectionView(onRetryClick = onError)
      is ArticleListUiState.Empty -> ArticlesList(
        articleList = emptyList(),
        navigate = navigateTo,
      )

      is ArticleListUiState.Idle ->
        ArticlesList(
          articleList = uiState.articles,
          navigate = navigateTo
        )
    }
  }
}

@Composable
fun ArticlesList(
  articleList: List<ArticleMeta>,
  navigate: (String) -> Unit
) {
  Spacer(modifier = Modifier.fillMaxWidth())
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(20.dp),
    contentPadding = PaddingValues(vertical = 16.dp),
    modifier = Modifier
      .semantics { collectionInfo = CollectionInfo(articleList.size, 1) }
      .padding(start = 16.dp, end = 16.dp)
      .wrapContentSize(Alignment.TopCenter)
  ) {
    itemsIndexed(articleList) { index, editorialMeta ->
      EditorialsViewCardLarge(
        modifier = Modifier.fillMaxWidth(),
        articleMeta = editorialMeta,
        onClick = {
          navigate(
            buildEditorialRoute(editorialMeta.id)
              .withItemPosition(index)
          )
        },
      )
    }
  }
}

@Composable
fun EditorialsViewCardLarge(
  modifier: Modifier = Modifier,
  articleMeta: ArticleMeta,
  onClick: () -> Unit,
) = Column(
  modifier = modifier.clickable(onClick = onClick)
) {
  Box(
    modifier = Modifier
      .padding(bottom = 8.dp)
      .fillMaxWidth()
  ) {
    AptoideFeatureGraphicImage(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(328 / 160f),
      data = articleMeta.image,
      contentDescription = null
    )
    Text(
      text = articleMeta.caption,
      style = AGTypography.BodyBold,
      color = Palette.Primary,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier
        .padding(start = 8.dp, top = 8.dp)
        .background(color = Palette.Black)
        .padding(horizontal = 8.dp, vertical = 4.dp)
    )
  }
  Text(
    modifier = modifier.padding(bottom = 8.dp),
    text = articleMeta.title,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis,
    style = AGTypography.InputsL,
    color = Palette.White,
  )
  Text(
    modifier = modifier,
    text = articleMeta.summary,
    maxLines = 3,
    overflow = TextOverflow.Ellipsis,
    style = AGTypography.SmallGames,
    color = Palette.White
  )
}

@PreviewDark
@Composable
private fun EditorialMoreViewPreview() {
  SeeMoreEditorialsContent(
    tag = "editorial-preview",
    uiState = previewArticlesListIdleState,
    navigate = { },
    onError = { }
  )
}
