package com.aptoide.android.aptoidegames.editorial

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.navDeepLink
import cm.aptoide.pt.aptoide_ui.textformatter.DateUtils
import cm.aptoide.pt.aptoide_ui.video.YoutubePlayer
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_editorial.data.model.Media
import cm.aptoide.pt.feature_editorial.domain.Article
import cm.aptoide.pt.feature_editorial.domain.randomArticle
import cm.aptoide.pt.feature_editorial.presentation.EditorialUiState
import cm.aptoide.pt.feature_editorial.presentation.editorialViewModel
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.BundleSource.MANUAL
import cm.aptoide.pt.feature_home.domain.Type.EDITORIAL
import cm.aptoide.pt.feature_home.domain.WidgetAction
import cm.aptoide.pt.feature_home.domain.WidgetActionType.BUTTON
import com.aptoide.android.aptoidegames.AptoideAsyncImageWithFullscreen
import com.aptoide.android.aptoidegames.APP_LINK_HOST
import com.aptoide.android.aptoidegames.APP_LINK_SCHEMA
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.UrlActivity
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGenericAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.design_system.IndeterminateCircularLoading
import com.aptoide.android.aptoidegames.design_system.SecondaryButton
import com.aptoide.android.aptoidegames.drawables.icons.getLeftArrow
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.error_views.NoConnectionView
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppItem
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

private const val ARTICLE_ID = "id"
private const val PATH = "editorial"
private const val SLUG = "slug"

const val editorialRoute = "editorial/{$ARTICLE_ID}"

fun editorialScreen() = ScreenData.withAnalytics(
  route = editorialRoute,
  screenAnalyticsName = "Editorial",
  deepLinks = listOf(
    navDeepLink { uriPattern = BuildConfig.DEEP_LINK_SCHEMA + editorialRoute },
    navDeepLink { uriPattern = "$APP_LINK_SCHEMA$APP_LINK_HOST/$PATH/{$SLUG}" }
  )
) { arguments, navigate, navigateBack ->
  val source = arguments?.getString(ARTICLE_ID)
    ?.let { "$ARTICLE_ID=$it" }
    ?: arguments?.getString(SLUG)!!
      .let { "$SLUG=$it" }

  val viewModel = editorialViewModel(source)
  val analyticsContext = AnalyticsContext.current
  val genericAnalytics = rememberGenericAnalytics()
  val uiState by viewModel.uiState.collectAsState()

  EditorialViewScreen(
    state = uiState,
    navigateBack = {
      genericAnalytics.sendBackButtonClick(analyticsContext)
      navigateBack()
    },
    navigate = { navigate(it) },
    onRetryNetwork = viewModel::reload,
    onRetryError = viewModel::reload
  )
}

fun buildEditorialRoute(articleId: String): String = "$PATH/$articleId"

@Composable
private fun EditorialViewScreen(
  state: EditorialUiState,
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
  onRetryNetwork: () -> Unit,
  onRetryError: () -> Unit,

  ) {
  when (state) {
    is EditorialUiState.Loading -> LoadingView()
    is EditorialUiState.NoConnection -> NoConnectionView(onRetryClick = onRetryNetwork)
    is EditorialUiState.Error -> GenericErrorView(onRetryError)
    is EditorialUiState.Idle -> ArticleViewContent(
      article = state.article,
      navigateBack = navigateBack,
      navigate = navigate,
    )
  }
}

@Composable
private fun LoadingView() {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    IndeterminateCircularLoading(color = Palette.Primary)
  }
}

@Composable
private fun ArticleViewContent(
  article: Article?,
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
) {
  val lazyListState = rememberLazyListState()
  var scrolledY = 0f
  var previousOffset = 0
  Box(
    modifier = Modifier.background(color = Palette.Black)
  ) {
    AptoideFeatureGraphicImage(
      modifier = Modifier
        .graphicsLayer {
          scrolledY -= lazyListState.firstVisibleItemScrollOffset - previousOffset
          translationY = scrolledY * 0.2f
          previousOffset = lazyListState.firstVisibleItemScrollOffset
        }
        .aspectRatio(360 / 200f)
        .fillMaxWidth(),
      data = article?.image,
      contentDescription = "Background Image"
    )
    LazyColumn(
      state = lazyListState,
      contentPadding = PaddingValues(bottom = 32.dp)
    ) {
      item {
        Box(
          modifier = Modifier
            .aspectRatio(360 / 200f)
            .padding(horizontal = 16.dp)
        ) {
          TopAppBar(
            backgroundColor = Color.Transparent.copy(alpha = 0.0f),
            elevation = 0.dp
          ) {
            Image(
              imageVector = getLeftArrow(Palette.Primary, Palette.Black),
              contentDescription = stringResource(id = R.string.button_back_title),
              modifier = Modifier
                .clickable(onClick = navigateBack)
                .size(32.dp)
            )
          }
        }
      }
      article?.run {
        item {
          Text(
            text = title,
            style = AGTypography.Title,
            color = Palette.White,
            modifier = Modifier
              .fillMaxSize()
              .background(color = Palette.Black)
              .padding(top = 24.dp, bottom = 8.dp)
              .padding(horizontal = 16.dp)
          )
        }
        content.forEach { content ->
          content.title?.ifBlank { null }?.let {
            item {
              Text(
                text = it,
                style = AGTypography.InputsL,
                color = Palette.White,
                modifier = Modifier
                  .fillMaxWidth()
                  .background(color = Palette.Black)
                  .padding(vertical = 8.dp)
                  .padding(horizontal = 16.dp)
              )
            }
          }
          content.message?.ifBlank { null }?.let {
            item {
              Text(
                text = it,
                style = AGTypography.ArticleText,
                color = Palette.White,
                modifier = Modifier
                  .fillMaxWidth()
                  .background(color = Palette.Black)
                  .padding(vertical = 8.dp, horizontal = 16.dp)
              )
            }
          }
          content.media.firstOrNull()?.let {
            item {
              ContentMedia(
                modifier = Modifier
                  .fillMaxWidth()
                  .aspectRatio(328 / 192f)
                  .background(color = Palette.Black)
                  .padding(vertical = 8.dp, horizontal = 16.dp),
                media = it
              )
            }
          }
          content.app?.let {
            item {
              AppItem(
                app = it,
                onClick = { navigate(buildAppViewRoute(it)) },
                modifier = Modifier.padding(horizontal = 16.dp)
              ) {
                InstallViewShort(app = it)
              }
            }
          }
          content.action?.let {
            item {
              ActionButton(
                modifier = Modifier
                  .background(color = Palette.Black)
                  .padding(vertical = 8.dp, horizontal = 16.dp)
                  .fillMaxWidth(),
                title = it.title,
                url = it.url
              )
            }
          }
        }
        item {
          Text(
            modifier = Modifier
              .fillMaxWidth()
              .background(color = Palette.Black)
              .padding(16.dp),
            text = DateUtils.getTimeDiffString(LocalContext.current, date),
            style = AGTypography.SmallGames,
            color = Palette.White,
          )
        }
        item {
          EditorialBundle(
            modifier = Modifier.padding(top = 24.dp),
            bundle = Bundle(
              title = stringResource(R.string.editorial_more_articles_title),
              actions = listOf(WidgetAction(type = BUTTON, tag = "editorial-more", url = "null")),
              type = EDITORIAL,
              tag = article.relatedTag,
              view = "",
              bundleSource = MANUAL,
              timestamp = "0"
            ),
            listenForPosition = false,
            filterId = article.id,
            subtype = subtype.toString(),
            navigate = navigate,
          )
        }
      }
    }
  }
}

@Composable
private fun ActionButton(
  modifier: Modifier = Modifier,
  title: String,
  url: String,
) {
  val context = LocalContext.current
  SecondaryButton(
    onClick = { UrlActivity.open(context, url) },
    modifier = modifier,
    title = title,
  )
}

@Composable
private fun ContentMedia(
  modifier: Modifier = Modifier,
  media: Media,
) {
  val mediaImage = media.image.takeIf { media.type == "image" && it != null }
  val mediaUrl = media.url.takeIf { media.type == "video_webview" && it != null }

  if (mediaImage != null || mediaUrl != null) {
    Column(modifier = modifier) {
      mediaImage?.let {
        AptoideAsyncImageWithFullscreen(
          modifier = Modifier
            .fillMaxSize(),
          data = it,
          contentDescription = "Background Image",
          images = listOf(it)
        )
      }
      mediaUrl?.let {
        AndroidView(
          modifier = Modifier.fillMaxSize(),
          factory = { context ->
            YoutubePlayer(context).apply {
              //can potentially set listeners here.
            }
          },
          update = { it.loadVideo(mediaUrl, false) }
        )
      }
    }
  }
}

@PreviewDark
@Composable
private fun EditorialViewScreenPreview(
  @PreviewParameter(EditorialUiStateProvider::class) state: EditorialUiState,
) {
  AptoideTheme(darkTheme = isSystemInDarkTheme()) {
    EditorialViewScreen(
      state = state,
      navigateBack = {},
      navigate = {},
      onRetryNetwork = {},
      onRetryError = {}
    )
  }
}

private class EditorialUiStateProvider : PreviewParameterProvider<EditorialUiState> {
  override val values: Sequence<EditorialUiState> = sequenceOf(
    EditorialUiState.Idle(randomArticle),
    EditorialUiState.Error,
    EditorialUiState.NoConnection,
    EditorialUiState.Loading,
  )
}
