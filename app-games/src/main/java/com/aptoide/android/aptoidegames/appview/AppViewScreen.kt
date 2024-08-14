package com.aptoide.android.aptoidegames.appview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.extensions.isYoutubeURL
import cm.aptoide.pt.extensions.openUrlInBrowser
import cm.aptoide.pt.extensions.parseDate
import cm.aptoide.pt.extensions.sendMail
import cm.aptoide.pt.extensions.toFormattedString
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_apps.presentation.AppUiState
import cm.aptoide.pt.feature_apps.presentation.rememberAppBySource
import cm.aptoide.pt.feature_apps.presentation.toAppIdParam
import cm.aptoide.pt.feature_apps.presentation.toPackageNameParam
import cm.aptoide.pt.feature_editorial.domain.ArticleMeta
import cm.aptoide.pt.feature_editorial.presentation.relatedEditorialsCardViewModel
import cm.aptoide.pt.feature_editorial.presentation.rememberRelatedEditorials
import com.aptoide.android.aptoidegames.AppIconImage
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.AptoideOutlinedText
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGenericAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.appview.AppViewHeaderConstants.FEATURE_GRAPHIC_HEIGHT
import com.aptoide.android.aptoidegames.appview.AppViewHeaderConstants.VIDEO_HEIGHT
import com.aptoide.android.aptoidegames.appview.permissions.buildAppPermissionsRoute
import com.aptoide.android.aptoidegames.drawables.icons.getBonusIconLeft
import com.aptoide.android.aptoidegames.drawables.icons.getForward
import com.aptoide.android.aptoidegames.drawables.icons.getLeftArrow
import com.aptoide.android.aptoidegames.editorial.EditorialsViewCard
import com.aptoide.android.aptoidegames.editorial.buildEditorialRoute
import com.aptoide.android.aptoidegames.feature_apps.presentation.SmallEmptyView
import com.aptoide.android.aptoidegames.home.GenericErrorView
import com.aptoide.android.aptoidegames.home.NoConnectionView
import com.aptoide.android.aptoidegames.installer.presentation.InstallView
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.videos.presentation.AppViewYoutubePlayer

private val tabsList = listOf(
  AppViewTab.DETAILS,
  AppViewTab.RELATED,
  AppViewTab.INFO
)

private const val SOURCE = "source"
private const val USE_STORE_NAME = "useStoreName"

const val appViewRoute = "app/{$SOURCE}?" +
  "$USE_STORE_NAME={$USE_STORE_NAME}"

private val appViewArguments = listOf(
  navArgument(USE_STORE_NAME) {
    type = NavType.BoolType
    defaultValue = true
  },
)

fun appViewScreen() = ScreenData.withAnalytics(
  route = appViewRoute,
  screenAnalyticsName = "AppView",
  arguments = appViewArguments,
  deepLinks = listOf(navDeepLink { uriPattern = BuildConfig.DEEP_LINK_SCHEMA + appViewRoute })
) { arguments, navigate, navigateBack ->
  val source = arguments?.getString(SOURCE)!!
  val useStoreName = arguments.getBoolean(USE_STORE_NAME, true)
  AppViewScreen(
    source = source,
    useStoreName = useStoreName,
    navigate = navigate,
    navigateBack = navigateBack
  )
}

fun buildAppViewRoute(packageName: String): String =
  buildAppViewRouteBySource(packageName.toPackageNameParam())

fun buildAppViewRouteByAppId(
  appId: Long,
  useStoreName: Boolean = true,
): String = buildAppViewRouteBySource(appId.toAppIdParam(), useStoreName)

fun buildAppViewRouteBySource(
  source: String,
  useStoreName: Boolean = true,
): String = appViewRoute
  .replace("{$SOURCE}", source)
  .replace("{$USE_STORE_NAME}", useStoreName.toString())

fun buildAppViewDeepLinkUri(source: String) =
  BuildConfig.DEEP_LINK_SCHEMA + buildAppViewRouteBySource(source)

@Composable
fun AppViewScreen(
  source: String,
  useStoreName: Boolean,
  navigate: (String) -> Unit,
  navigateBack: () -> Unit,
) {
  val (uiState, reload) = rememberAppBySource(
    source = source,
    useStoreName = useStoreName,
  )
  val analyticsContext = AnalyticsContext.current
  val genericAnalytics = rememberGenericAnalytics()

  val relatedEditorialsUiState = (uiState as? AppUiState.Idle)?.app?.packageName?.let {
    rememberRelatedEditorials(packageName = it)
  }

  val tabsList by remember(relatedEditorialsUiState) {
    derivedStateOf {
      if (relatedEditorialsUiState.isNullOrEmpty()) {
        tabsList.filter { it != AppViewTab.RELATED }
      } else {
        tabsList
      }
    }
  }

  MainAppViewView(
    uiState = uiState,
    reload = reload,
    noNetworkReload = reload,
    navigate = navigate,
    navigateBack = {
      genericAnalytics.sendBackButtonClick(analyticsContext)
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
  navigate: (String) -> Unit,
  navigateBack: () -> Unit,
  tabsList: List<AppViewTab>,
) {
  when (uiState) {
    is AppUiState.Idle ->
      AppViewContent(
        app = uiState.app,
        tabsList = tabsList,
        navigate = navigate,
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
  navigate: (String) -> Unit,
  navigateBack: () -> Unit,
) {
  var selectedTab by rememberSaveable { mutableIntStateOf(0) }
  val appImageString = stringResource(id = R.string.app_view_image_description_body, app.name)

  val scrollState = rememberScrollState()
  val localDensity = LocalDensity.current

  val showYoutubeVideo = app.videos.isNotEmpty()
    && app.videos[0].let { it.isNotEmpty() && it.isYoutubeURL() }

  Box(
    modifier = Modifier.verticalScroll(scrollState)
  ) {
    val featureGraphicContent: @Composable () -> Unit = {
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
    }

    if (showYoutubeVideo) {
      val videoId = app.videos[0].split("embed/").getOrElse(1) { "" }
      val videoHeightPx = with(localDensity) { VIDEO_HEIGHT.dp.toPx() }
      val contentDesc = String.format("Video of %1s", app.name)

      val shouldVideoPause by remember {
        derivedStateOf { scrollState.value > (videoHeightPx * 1.2f) }
      }

      AppViewYoutubePlayer(
        modifier = Modifier
          .semantics { contentDescription = contentDesc }
          .graphicsLayer {
            translationY = scrollState.value * 0.8f
          }
          .height(VIDEO_HEIGHT.dp)
          .fillMaxWidth(),
        videoId = videoId,
        shouldPause = shouldVideoPause,
        contentDesc = contentDesc,
        onErrorContent = featureGraphicContent
      )
    } else {
      featureGraphicContent()
    }
    Image(
      imageVector = getLeftArrow(Palette.Primary, Palette.Black),
      contentDescription = stringResource(id = R.string.button_back_title),
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .clickable(onClick = navigateBack)
        .padding(top = 4.dp, start = 16.dp)
        .size(32.dp)
    )
    Column(
      modifier = Modifier
        .padding(top = if (showYoutubeVideo) VIDEO_HEIGHT.dp else FEATURE_GRAPHIC_HEIGHT.dp)
        .background(Palette.Black)
    ) {
      AppPresentationView(app)

      InstallView(
        modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp),
        app = app
      )

      AppInfoViewPager(
        selectedTab = selectedTab,
        tabsList = tabsList,
        onSelectTab = { selectedTab = it }
      )

      ViewPagerContent(
        app = app,
        selectedTab = tabsList[selectedTab],
        navigate = navigate
      )
    }
    if (app.isAppCoins) {
      Row(
        modifier = Modifier
          .padding(top = 160.dp)
          .align(Alignment.TopStart)
      ) {
        Image(
          imageVector = getBonusIconLeft(
            iconColor = Palette.Primary,
            outlineColor = Palette.Black,
            backgroundColor = Palette.Secondary
          ),
          contentDescription = null,
          modifier = Modifier
            .size(40.dp)
            .graphicsLayer {
              this.translationY = 6.dp.toPx()
            }
        )
        AptoideOutlinedText(
          text = "Up to 20% Bonus", //TODO Hardcoded String,
          style = AGTypography.InputsM,
          outlineWidth = 15f,
          outlineColor = Palette.Black,
          textColor = Palette.Primary,
          modifier = Modifier
            .align(Alignment.Bottom)
            .background(color = Palette.Secondary)
            .padding(start = 16.dp, end = 92.dp)
        )
      }
    }
  }
}

@Composable
fun AppInfoViewPager(
  selectedTab: Int,
  tabsList: List<AppViewTab>,
  onSelectTab: (Int) -> Unit,
) {
  CustomScrollableTabRow(
    tabs = tabsList,
    selectedTabIndex = selectedTab,
    onTabClick = onSelectTab,
    contentColor = Palette.Primary,
    backgroundColor = Color.Transparent
  )
}

@Composable
fun ViewPagerContent(
  app: App,
  selectedTab: AppViewTab,
  navigate: (String) -> Unit,
) {
  when (selectedTab) {
    AppViewTab.DETAILS -> DetailsView(app = app)

    AppViewTab.RELATED -> RelatedContentView(
      packageName = app.packageName,
      navigate = navigate
    )

    AppViewTab.INFO -> AppInfoSection(
      app = app,
      navigate = navigate
    )
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
        modifier = Modifier.padding(top = 24.dp, bottom = 32.dp, start = 16.dp, end = 16.dp),
        style = AGTypography.ArticleText,
        color = Palette.White
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
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(horizontal = 16.dp)
  ) {
    itemsIndexed(screenshots) { index, screenshot ->
      val stringResource = stringResource(id = R.string.app_view_screenshot_number, index + 1)
      AptoideAsyncImage(
        modifier = Modifier
          .clearAndSetSemantics {
            contentDescription = stringResource
          }
          .size(268.dp, 152.dp),
        data = screenshot,
        contentDescription = null,
      )
    }
  }
}

@Composable
fun AppInfoSection(
  app: App,
  navigate: (String) -> Unit,
) {
  val context = LocalContext.current
  Column(
    modifier = Modifier.padding(top = 12.dp, bottom = 48.dp, start = 16.dp, end = 16.dp),
  ) {
    AppInfoRow(
      infoCategory = "Version Name",
      infoContent = app.versionName
    )
    AppInfoRow(
      infoCategory = "Package Name",
      infoContent = app.packageName
    )
    AppInfoRow(
      infoCategory = "Release",
      infoContent = app.releaseDate
        ?.parseDate()
        ?.toFormattedString(pattern = "d MMM yyyy") ?: ""
    )
    AppInfoRow(
      infoCategory = "Updated on",
      infoContent = app.updateDate
        ?.parseDate(pattern = "yyyy-MM-dd")
        ?.toFormattedString(pattern = "d MMM yyyy") ?: ""
    )
    AppInfoRow(
      infoCategory = "Download size",
      infoContent = TextFormatter.formatBytes(app.appSize)
    )
    app.website?.let {
      AppInfoRowWithAction(
        infoCategory = "Website",
        onClick = { context.openUrlInBrowser(it) }
      )
    }
    app.email?.let {
      val subject = stringResource(R.string.app_info_send_email_subject)
      AppInfoRowWithAction(
        infoCategory = "Email",
        onClick = { context.sendMail(it, subject) }
      )
    }
    app.privacyPolicy?.let {
      AppInfoRowWithAction(
        infoCategory = "Privacy Policy",
        onClick = { context.openUrlInBrowser(it) }
      )
    }
    app.permissions?.let {
      AppInfoRowWithAction(
        infoCategory = "Permissions",
        onClick = {
          navigate(buildAppPermissionsRoute(app.packageName))
        }
      )
    }
  }
}

@Composable
fun AppInfoRow(
  infoCategory: String,
  infoContent: String,
) {
  Row(
    modifier = Modifier
      .padding(vertical = 12.dp)
      .fillMaxWidth()
      .clearAndSetSemantics {
        contentDescription = "$infoCategory $infoContent"
      },
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = infoCategory,
      modifier = Modifier.padding(end = 16.dp),
      style = AGTypography.InputsM,
      overflow = TextOverflow.Ellipsis,
      color = Palette.White
    )
    Text(
      text = infoContent,
      style = AGTypography.DescriptionGames,
      overflow = TextOverflow.Ellipsis,
      color = Palette.GreyLight
    )
  }
}

@Composable
fun AppInfoRowWithAction(
  infoCategory: String,
  onClick: () -> Unit,
) {
  Row(
    modifier = Modifier
      .clickable { onClick() }
      .padding(vertical = 12.dp)
      .fillMaxWidth()
      .clearAndSetSemantics {
        contentDescription = infoCategory
      },
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = infoCategory,
      style = AGTypography.InputsM,
      color = Palette.White
    )
    Image(
      modifier = Modifier.size(32.dp),
      imageVector = getForward(Palette.Primary),
      contentDescription = null,
    )
  }
}

@Composable
fun RelatedContentView(
  packageName: String,
  navigate: (String) -> Unit,
) {
  val editorialsCardViewModel = relatedEditorialsCardViewModel(packageName = packageName)
  val uiState by editorialsCardViewModel.uiState.collectAsState()

  ShowRelatedContentView(
    state = uiState,
    navigate = navigate,
  )
}

@Composable
private fun ShowRelatedContentView(
  state: List<ArticleMeta>?,
  navigate: (String) -> Unit,
) {
  if (state == null) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 44.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      CircularProgressIndicator()
    }
  } else if (state.isEmpty()) {
    SmallEmptyView(
      padding = PaddingValues(vertical = 64.dp, horizontal = 48.dp),
      title = "Oops, we couldn\\'t find related content yet!"
    )
  } else {
    Column(
      modifier = Modifier.padding(all = 16.dp),
      verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
      state.forEach { articleMeta ->
        EditorialsViewCard(
          articleMeta = articleMeta,
          onClick = {
            navigate(
              buildEditorialRoute(articleMeta.id)
            )
          }
        )
      }
    }
    if (state.isNotEmpty()) {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
fun AppPresentationView(app: App) {
  Row(
    modifier = Modifier
      .padding(start = 16.dp, top = 16.dp)
      .fillMaxWidth()
      .wrapContentHeight(),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    val appIconString = stringResource(id = R.string.app_view_icon_description_body, app.name)
    AppIconImage(
      modifier = Modifier
        .clearAndSetSemantics { contentDescription = appIconString }
        .padding(end = 16.dp)
        .size(88.dp),
      data = app.icon,
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
        style = AGTypography.TitleGames,
        fontWeight = FontWeight.Bold,
        overflow = TextOverflow.Ellipsis,
      )
      app.developerName?.let {
        Text(
          text = it,
          maxLines = 1,
          style = AGTypography.SmallGames,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }
  }
}

private object AppViewHeaderConstants {
  const val VIDEO_HEIGHT = 200
  const val FEATURE_GRAPHIC_HEIGHT = 200
}

@PreviewDark
@Composable
fun AppViewScreenPreview() {
  AptoideTheme {
    AppViewContent(
      app = randomApp,
      tabsList = listOf(
        AppViewTab.DETAILS,
        AppViewTab.RELATED,
        AppViewTab.INFO,
      ),
      navigateBack = {},
      navigate = {},
    )
  }
}

@PreviewDark
@Composable
fun DetailsViewPreview() {
  AptoideTheme {
    DetailsView(app = randomApp)
  }
}

@PreviewDark
@Composable
fun AppInfoSectionPreview() {
  AptoideTheme {
    AppInfoSection(
      app = randomApp,
      navigate = {}
    )
  }
}
