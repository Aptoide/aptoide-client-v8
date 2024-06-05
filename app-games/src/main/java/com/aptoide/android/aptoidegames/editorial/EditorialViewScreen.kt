package com.aptoide.android.aptoidegames.editorial

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navDeepLink
import cm.aptoide.pt.aptoide_ui.textformatter.DateUtils
import cm.aptoide.pt.aptoide_ui.video.YoutubePlayer
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.animatedComposable
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_editorial.data.model.Media
import cm.aptoide.pt.feature_editorial.domain.Article
import cm.aptoide.pt.feature_editorial.domain.randomArticle
import cm.aptoide.pt.feature_editorial.presentation.EditorialUiState
import cm.aptoide.pt.feature_editorial.presentation.editorialViewModel
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.BundleSource.MANUAL
import cm.aptoide.pt.feature_home.domain.Type.EDITORIAL
import com.aptoide.android.aptoidegames.AppIconImage
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.UrlActivity
import com.aptoide.android.aptoidegames.design_system.SecondaryButton
import com.aptoide.android.aptoidegames.drawables.icons.getLeftArrow
import com.aptoide.android.aptoidegames.home.GenericErrorView
import com.aptoide.android.aptoidegames.home.NoConnectionView
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

const val editorialRoute = "editorial/{articleId}"

fun NavGraphBuilder.editorialScreen(
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
) = animatedComposable(
  editorialRoute,
  deepLinks = listOf(navDeepLink { uriPattern = BuildConfig.DEEP_LINK_SCHEMA + editorialRoute })
) { it ->
  val articleId = it.arguments?.getString("articleId")!!

  val viewModel = editorialViewModel(articleId)
  val uiState by viewModel.uiState.collectAsState()

  EditorialViewScreen(
    state = uiState,
    navigateBack = {
      navigateBack()
    },
    navigate = { navigate(it) },
    onAppLoaded = { app -> viewModel.onAppLoaded(app) },
    onRetryNetwork = viewModel::reload,
    onRetryError = viewModel::reload
  )
}

fun buildEditorialRoute(articleId: String): String = "editorial/$articleId"

@Composable
private fun EditorialViewScreen(
  state: EditorialUiState,
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
  onAppLoaded: (App) -> Unit,
  onRetryNetwork: () -> Unit,
  onRetryError: () -> Unit,

  ) {
  when (state) {
    is EditorialUiState.Loading -> LoadingView()
    is EditorialUiState.NoConnection -> NoConnectionView(onRetryClick = onRetryNetwork)
    is EditorialUiState.Error -> GenericErrorView(onRetryError)
    is EditorialUiState.Idle -> ArticleViewContent(
      article = state.article,
      onAppLoaded = onAppLoaded,
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
    CircularProgressIndicator()
  }
}

@Composable
private fun ArticleViewContent(
  article: Article?,
  onAppLoaded: (App) -> Unit,
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
        .height(208.dp)
        .fillMaxWidth(),
      data = article?.image,
      contentDescription = "Background Image"
    )
    LazyColumn(
      state = lazyListState
    ) {
      item {
        Box(
          modifier = Modifier
            .height(height = 208.dp)
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
                  .aspectRatio(ratio = 1.81f)
                  .background(color = Palette.Black)
                  .padding(vertical = 8.dp, horizontal = 16.dp),
                media = it
              )
            }
          }
          content.app?.let {
            item {
              LaunchedEffect(true) { onAppLoaded(it) }

              AppBannerView(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(color = Palette.Black)
                  .padding(16.dp),
                app = it,
                type = caption.uppercase(),
              )
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
            modifier = Modifier
              .fillMaxWidth()
              .background(color = Palette.Black),
            bundle = Bundle(
              title = stringResource(R.string.editorial_more_articles_title),
              actions = emptyList(),
              type = EDITORIAL,
              tag = article.relatedTag,
              view = "",
              bundleSource = MANUAL,
              timestamp = "0"
            ),
            navigate = navigate,
            filterId = article.id,
            subtype = subtype.toString(),
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
        AptoideAsyncImage(
          modifier = Modifier.fillMaxSize(),
          data = media.image,
          contentDescription = "Background Image",
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

@Composable
private fun AppBannerView(
  modifier: Modifier = Modifier,
  app: App,
  type: String,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
  ) {
    AppIconImage(
      modifier = Modifier.size(88.dp),
      data = app.icon,
      contentDescription = "App Icon",
    )
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(horizontal = 16.dp)
    ) {
      Text(
        modifier = Modifier.padding(bottom = 4.dp),
        text = app.name,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        style = AGTypography.DescriptionGames,
        color = Palette.White,
      )
      Text(
        modifier = Modifier.padding(top = 4.dp),
        text = type,
        style = AGTypography.InputsS,
        color = Palette.GreyLight,
        textAlign = TextAlign.Center,
      )
    }
    InstallViewShort(
      app = app,
      cancelable = false
    )
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
      onAppLoaded = {},
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
