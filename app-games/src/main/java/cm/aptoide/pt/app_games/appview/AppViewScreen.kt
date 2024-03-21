package cm.aptoide.pt.app_games.appview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import cm.aptoide.pt.app_games.AptoideAsyncImage
import cm.aptoide.pt.app_games.AptoideFeatureGraphicImage
import cm.aptoide.pt.app_games.R.string
import cm.aptoide.pt.app_games.appview.AppViewVideoConstants.FEATURE_GRAPHIC_HEIGHT
import cm.aptoide.pt.app_games.home.GenericErrorView
import cm.aptoide.pt.app_games.home.NoConnectionView
import cm.aptoide.pt.app_games.installer.AppIcon
import cm.aptoide.pt.app_games.theme.AppTheme
import cm.aptoide.pt.aptoide_ui.animations.animatedComposable
import cm.aptoide.pt.download_view.presentation.DownloadViewScreen
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.presentation.AppUiState
import cm.aptoide.pt.feature_apps.presentation.appViewModel
import cm.aptoide.pt.feature_appview.presentation.AppViewTab
import coil.transform.RoundedCornersTransformation

private val tabsList = listOf(
  AppViewTab.DETAILS,
)

const val appViewRoute = "app/{packageName}"

fun NavGraphBuilder.appViewScreen(
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
) = animatedComposable(
  appViewRoute
) { it ->
  val packageName = it.arguments?.getString("packageName")!!
  AppViewScreen(
    packageName = packageName,
    navigateBack = navigateBack,
    navigate = { navigate(it) },
  )
}

fun buildAppViewRoute(
  packageName: String,
): String = "app/$packageName"

@Composable
fun AppViewScreen(
  packageName: String = "",
  navigateBack: () -> Unit = {},
  navigate: (String) -> Unit = {},
) {
  val appViewModel = appViewModel(packageName = packageName, adListId = "")
  val uiState by appViewModel.uiState.collectAsState()

  MainAppViewView(
    uiState = uiState,
    reload = appViewModel::reload,
    noNetworkReload = {
      appViewModel.reload()
    },
    navigateBack = {
      navigateBack()
    },
    tabsList = tabsList
  )
}

@Composable
fun MainAppViewView(
  uiState: AppUiState,
  reload: () -> Unit,
  noNetworkReload: () -> Unit,
  navigateBack: () -> Unit,
  tabsList: List<AppViewTab>,
) {
  when (uiState) {
    is AppUiState.Idle ->
      AppViewContent(
        app = uiState.app,
        tabsList = tabsList,
        navigateBack = navigateBack,
      )

    is AppUiState.NoConnection -> NoConnectionView(onRetryClick = noNetworkReload)
    is AppUiState.Error -> GenericErrorView(reload)
    is AppUiState.Loading -> LoadingView()
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
fun AppViewContent(
  app: App,
  tabsList: List<AppViewTab>,
  navigateBack: () -> Unit,
) {
  val selectedTab by rememberSaveable { mutableIntStateOf(0) }
  val appImageString = stringResource(id = string.app_view_image_description_body, app.name)

  val scrollState = rememberScrollState()

  Column(
    modifier = Modifier.verticalScroll(scrollState)
  ) {
    Box {
      AptoideFeatureGraphicImage(
        modifier = Modifier
          .clearAndSetSemantics { contentDescription = appImageString }
          .graphicsLayer {
            translationY = scrollState.value * 0.8f
          }
          .height(FEATURE_GRAPHIC_HEIGHT.dp)
          .fillMaxWidth(),
        data = app.featureGraphic,
        contentDescription = null
      )
      Image(
        imageVector = AppTheme.icons.LeftArrow,
        contentDescription = stringResource(id = string.button_back_title),
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .clickable(onClick = navigateBack)
          .padding(horizontal = 16.dp, vertical = 12.dp)
          .size(32.dp)
      )
    }
    Column(
      modifier = Modifier.background(AppTheme.colors.background)
    ) {

      AppPresentationView(app)

      InstallButton(app)

      ViewPagerContent(
        app = app,
        selectedTab = tabsList[selectedTab],
      )
    }
  }
}

@Composable
fun ViewPagerContent(
  app: App,
  selectedTab: AppViewTab,
) {
  when (selectedTab) {
    AppViewTab.DETAILS -> DetailsView(app = app)
    else -> {}
  }
}

@Composable
fun DetailsView(app: App) {
  Column(
    modifier = Modifier.padding(top = 16.dp)
  ) {
    app.screenshots?.let { ScreenshotsList(it) }
    app.description?.let {
      Text(
        text = it,
        modifier = Modifier.padding(top = 12.dp, bottom = 26.dp, start = 16.dp, end = 16.dp),
        style = AppTheme.typography.bodyCopyXS,
        color = AppTheme.colors.greyText
      )
    }
  }
}

@Composable
fun ScreenshotsList(screenshots: List<String>) {
  LazyRow(
    modifier = Modifier
      .fillMaxWidth()
      .semantics {
        collectionInfo = CollectionInfo(1, screenshots.size)
      },
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    contentPadding = PaddingValues(start = 18.dp)
  ) {
    itemsIndexed(screenshots) { index, screenshot ->
      val stringResource = stringResource(id = string.app_view_screenshot_number, index + 1)
      AptoideAsyncImage(
        modifier = Modifier
          .clearAndSetSemantics {
            contentDescription = stringResource
          }
          .size(268.dp, 152.dp)
          .clip(RoundedCornerShape(24.dp)),
        data = screenshot,
        contentDescription = null,
        transformations = RoundedCornersTransformation(24f)
      )
    }
  }
}

@Composable
fun AppPresentationView(
  app: App,
) {

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .offset(0.dp, (-24).dp)
      .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
      .background(AppTheme.colors.background)
  ) {
    Row(
      modifier = Modifier
        .padding(start = 16.dp, top = 16.dp, bottom = 24.dp)
        .fillMaxWidth()
        .wrapContentHeight(),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      val appIconString = stringResource(id = string.app_view_icon_description_body, app.name)
      AppIcon(
        modifier = Modifier
          .clearAndSetSemantics { contentDescription = appIconString }
          .padding(end = 16.dp)
          .size(88.dp)
          .clip(RoundedCornerShape(16.dp)),
        app = app,
        contentDescription = null,
      )
      Column(
        modifier = Modifier
          .padding(end = 16.dp)
          .weight(1f),
        horizontalAlignment = Alignment.Start,
      ) {
        Text(
          text = app.name,
          maxLines = 2,
          style = AppTheme.typography.gameTitleTextCondensed,
          fontWeight = FontWeight.Bold,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.padding(bottom = 4.dp)
        )
        app.developerName?.let {
          Text(
            text = it,
            maxLines = 1,
            style = AppTheme.typography.gameTitleTextCondensed,
            overflow = TextOverflow.Ellipsis,
          )
        }
      }
    }
  }
}

@Composable
fun InstallButton(app: App) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .offset(0.dp, (-24).dp)
      .background(AppTheme.colors.background)
  ) {
    DownloadViewScreen(app = app)
  }
}

private object AppViewVideoConstants {
  const val FEATURE_GRAPHIC_HEIGHT = 208
}
