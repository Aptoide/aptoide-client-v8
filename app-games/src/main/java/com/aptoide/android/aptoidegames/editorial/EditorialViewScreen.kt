package com.aptoide.android.aptoidegames.editorial

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
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
import cm.aptoide.pt.aptoide_ui.animations.animatedComposable
import cm.aptoide.pt.aptoide_ui.textformatter.DateUtils
import cm.aptoide.pt.aptoide_ui.video.YoutubePlayer
import cm.aptoide.pt.extensions.PreviewAll
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_editorial.data.model.Media
import cm.aptoide.pt.feature_editorial.domain.Article
import cm.aptoide.pt.feature_editorial.domain.randomArticle
import cm.aptoide.pt.feature_editorial.presentation.EditorialUiState
import cm.aptoide.pt.feature_editorial.presentation.editorialViewModel
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.BundleSource.MANUAL
import cm.aptoide.pt.feature_home.domain.Type.EDITORIAL
import coil.transform.RoundedCornersTransformation
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.UrlActivity
import com.aptoide.android.aptoidegames.home.GenericErrorView
import com.aptoide.android.aptoidegames.home.NoConnectionView
import com.aptoide.android.aptoidegames.installer.presentation.AppIcon
import com.aptoide.android.aptoidegames.installer.presentation.InstallView
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.darkGray2

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
fun EditorialViewScreen(
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
fun LoadingView() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    CircularProgressIndicator()
  }
}

@Composable
fun ArticleViewContent(
  article: Article?,
  onAppLoaded: (App) -> Unit,
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
) {
  val lazyListState = rememberLazyListState()
  var scrolledY = 0f
  var previousOffset = 0
  Box {
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
      modifier = Modifier
        .fillMaxHeight(),
      state = lazyListState
    ) {
      item {
        Box(modifier = Modifier.height(height = 184.dp)) {
          TopAppBar(
            backgroundColor = Color.Transparent.copy(alpha = 0.0f),
            elevation = 0.dp,
            content = {
              Image(
                imageVector = AppTheme.icons.LeftArrow,
                contentDescription = stringResource(id = R.string.button_back_title),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                  .clickable(onClick = navigateBack)
                  .padding(horizontal = 16.dp, vertical = 12.dp)
                  .size(32.dp)
              )
            }
          )
        }
      }
      article?.run {
        item {
          Text(
            text = title,
            style = AppTheme.typography.headlineTitleText,
            modifier = Modifier
              .fillMaxWidth()
              .wrapContentHeight()
              .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
              .background(color = AppTheme.colors.background)
              .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 12.dp)
          )
        }
        content.forEach { content ->
          content.title?.ifBlank { null }?.let {
            item { ContentTitle(it) }
          }
          content.message?.ifBlank { null }?.let {
            item { ContentMessage(it) }
          }
          content.media.firstOrNull()?.let {
            item { ContentMedia(it) }
          }
          content.app?.let {
            item {
              LaunchedEffect(true) { onAppLoaded(it) }

              AppBannerView(
                app = it,
                type = caption.uppercase(),
              )
            }
          }
          content.action?.let {
            item {
              ActionButton(it.title, it.url)
            }
          }
        }
        item {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .fillMaxWidth()
              .wrapContentHeight()
              .background(color = AppTheme.colors.background)
              .padding(start = 16.dp, end = 16.dp, bottom = 40.dp)
          ) {
            Text(
              text = DateUtils.getTimeDiffString(LocalContext.current, date),
              style = AppTheme.typography.gameTitleTextCondensedSmall,
            )
            Spacer(modifier = Modifier.weight(weight = 1f))
          }
        }
        item {
          EditorialBundle(
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
fun ActionButton(
  title: String,
  url: String,
) {
  val context = LocalContext.current
  Box(
    modifier = Modifier
      .background(color = AppTheme.colors.background)
      .padding(bottom = 16.dp)
      .wrapContentHeight()
      .fillMaxWidth()
  ) {
    Button(
      onClick = { UrlActivity.open(context, url) },
      shape = RoundedCornerShape(30.dp),
      modifier = Modifier
        .padding(start = 16.dp, end = 16.dp)
        .fillMaxWidth()
        .height(40.dp),
      elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
      contentPadding = PaddingValues(),
      colors = ButtonDefaults.buttonColors(backgroundColor = darkGray2)
    ) {
      Text(
        text = title,
        maxLines = 1,
        style = AppTheme.typography.buttonTextLight,
        color = Color.White
      )
    }
  }
}

@Composable
fun ContentTitle(title: String) = Text(
  text = title,
  style = AppTheme.typography.headlineTitleText,
  modifier = Modifier
    .fillMaxWidth()
    .wrapContentHeight()
    .background(color = AppTheme.colors.background)
    .padding(start = 16.dp, top = 24.dp, bottom = 12.dp, end = 16.dp)
)

@Composable
fun ContentMessage(message: String) = Text(
  text = message,
  style = AppTheme.typography.bodyCopy,
  modifier = Modifier
    .fillMaxWidth()
    .wrapContentHeight()
    .background(color = AppTheme.colors.background)
    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
)

@Composable
fun ContentMedia(media: Media) {
  if (media.type == "image") {
    media.image?.let {
      AptoideAsyncImage(
        modifier = Modifier
          .aspectRatio(ratio = 1.81f)
          .background(color = AppTheme.colors.background)
          .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp)),
        data = it,
        contentDescription = "Background Image",
        transformations = RoundedCornersTransformation(12f)
      )
    }
  } else if (media.type == "video_webview") {
    media.url?.let { VideoView(it) }
  }
}

@Composable
private fun AppBannerView(
  app: App,
  type: String,
) {

  Column(
    modifier = Modifier
      .wrapContentHeight()
      .fillMaxWidth()
      .background(color = AppTheme.colors.background)
      .padding(vertical = 24.dp, horizontal = 16.dp),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(bottom = 16.dp)
    ) {
      AppIcon(
        modifier = Modifier
          .height(88.dp)
          .width(88.dp)
          .clip(RoundedCornerShape(16.dp)),
        app = app,
        contentDescription = "App Icon",
      )
      Column(
        modifier = Modifier
          .padding(start = 16.dp)
      ) {
        Text(
          text = app.name,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          style = AppTheme.typography.gameTitleTextCondensedXL,
          modifier = Modifier.padding(bottom = 12.dp)
        )
        Text(
          text = type,
          style = AppTheme.typography.buttonTextSmall,
          color = AppTheme.colors.editorialViewTextLabelColor,
          textAlign = TextAlign.Center,
          modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(16.dp))
            .background(color = AppTheme.colors.editorialViewLabelColor)
            .padding(horizontal = 15.dp, vertical = 5.dp)
        )
      }
    }
    InstallView(
      app = app,
      onInstallStarted = {},
    )
  }
}

@Composable
private fun VideoView(videoUrl: String) {
  Column(
    modifier = Modifier
      .aspectRatio(ratio = 1.81f)
      .background(color = AppTheme.colors.background)
      .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp)),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    AndroidView(
      modifier = Modifier.fillMaxSize(),
      factory = { context ->
        YoutubePlayer(context).apply {
          //can potentially set listeners here.
        }
      },
      update = { it.loadVideo(videoUrl, false) }
    )
  }
}

@PreviewAll
@Composable
fun EditorialViewScreenPreview(
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

class EditorialUiStateProvider : PreviewParameterProvider<EditorialUiState> {
  override val values: Sequence<EditorialUiState> = sequenceOf(
    EditorialUiState.Loading,
    EditorialUiState.Error,
    EditorialUiState.NoConnection,
    EditorialUiState.Idle(randomArticle)
  )
}
