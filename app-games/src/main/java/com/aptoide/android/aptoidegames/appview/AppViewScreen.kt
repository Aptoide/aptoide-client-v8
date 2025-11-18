package com.aptoide.android.aptoidegames.appview

import android.net.Uri.encode
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material.Icon
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import cm.aptoide.pt.aptoide_network.data.network.model.Screenshot
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.extensions.extractVideoId
import cm.aptoide.pt.extensions.formatDownloads
import cm.aptoide.pt.extensions.isYoutubeURL
import cm.aptoide.pt.extensions.openUrlInBrowser
import cm.aptoide.pt.extensions.parseDate
import cm.aptoide.pt.extensions.sendMail
import cm.aptoide.pt.extensions.toFormattedString
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_apps.domain.AppSource
import cm.aptoide.pt.feature_apps.domain.AppSource.Companion.appendIfRequired
import cm.aptoide.pt.feature_apps.domain.Rating
import cm.aptoide.pt.feature_apps.presentation.AppUiState
import cm.aptoide.pt.feature_apps.presentation.rememberApp
import cm.aptoide.pt.feature_editorial.domain.ArticleMeta
import cm.aptoide.pt.feature_editorial.presentation.relatedEditorialsCardViewModel
import cm.aptoide.pt.feature_editorial.presentation.rememberRelatedEditorials
import com.aptoide.android.aptoidegames.APP_LINK_HOST
import com.aptoide.android.aptoidegames.APP_LINK_SCHEMA
import com.aptoide.android.aptoidegames.AppIconImage
import com.aptoide.android.aptoidegames.AptoideAsyncImageWithFullscreen
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.AptoideOutlinedText
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.mapOfNonNull
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGeneralAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withParameter
import com.aptoide.android.aptoidegames.appview.AppViewHeaderConstants.FEATURE_GRAPHIC_HEIGHT
import com.aptoide.android.aptoidegames.appview.AppViewHeaderConstants.VIDEO_HEIGHT
import com.aptoide.android.aptoidegames.appview.permissions.buildAppPermissionsRoute
import com.aptoide.android.aptoidegames.design_system.IndeterminateCircularLoading
import com.aptoide.android.aptoidegames.drawables.icons.getBonusIconLeft
import com.aptoide.android.aptoidegames.drawables.icons.getBookmarkStar
import com.aptoide.android.aptoidegames.drawables.icons.getForward
import com.aptoide.android.aptoidegames.drawables.icons.getLeftArrow
import com.aptoide.android.aptoidegames.drawables.icons.getRatingStar
import com.aptoide.android.aptoidegames.editorial.EditorialsViewCardLarge
import com.aptoide.android.aptoidegames.editorial.buildEditorialRoute
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.error_views.NoConnectionView
import com.aptoide.android.aptoidegames.feature_apps.presentation.SmallEmptyView
import com.aptoide.android.aptoidegames.feature_apps.presentation.buildSeeMoreBonusRoute
import com.aptoide.android.aptoidegames.feature_apps.presentation.rememberBonusBundle
import com.aptoide.android.aptoidegames.feature_rtb.presentation.isRTB
import com.aptoide.android.aptoidegames.installer.presentation.InstallView
import com.aptoide.android.aptoidegames.play_and_earn.presentation.app_view.AppRewardsView
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.PaEInstallView
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.videos.presentation.AppViewYoutubePlayer

private val tabsList = listOf(
  AppViewTab.DETAILS,
  AppViewTab.REWARDS,
  AppViewTab.RELATED,
  AppViewTab.INFO,
)
private const val PACKAGE_UNAME = "package_uname"

private const val PATH = "path"
private const val APP_PATH = "app"

private const val SOURCE = "source"
private const val UTM_CAMPAIGN = "utm_campaign"
private const val UTM_MEDIUM = "utm_medium"
private const val UTM_SOURCE = "utm_source"
private const val UTM_CONTENT = "utm_content"
private const val IS_GAMIFIED = "is_gamified"

private val allAppViewArguments = listOf(
  navArgument(UTM_CAMPAIGN) {
    type = NavType.StringType
    nullable = true
  },
  navArgument(UTM_MEDIUM) {
    type = NavType.StringType
    nullable = true
  },
  navArgument(UTM_SOURCE) {
    type = NavType.StringType
    nullable = true
  },
  navArgument(UTM_CONTENT) {
    type = NavType.StringType
    nullable = true
  },
  navArgument(IS_GAMIFIED) {
    type = NavType.BoolType
    defaultValue = false
  },
)

const val appViewRoute =
  "${APP_PATH}/{$SOURCE}?"

fun appViewScreen() = ScreenData.withAnalytics(
  route = "$appViewRoute$UTM_MEDIUM={$UTM_MEDIUM}&$UTM_CONTENT={$UTM_CONTENT}&$UTM_SOURCE={$UTM_SOURCE}&$UTM_CAMPAIGN={$UTM_CAMPAIGN}&$IS_GAMIFIED={$IS_GAMIFIED}",
  screenAnalyticsName = "AppView",
  arguments = allAppViewArguments,
  deepLinks = listOf(
    navDeepLink { uriPattern = BuildConfig.DEEP_LINK_SCHEMA + appViewRoute },
    navDeepLink {
      uriPattern =
        "$APP_LINK_SCHEMA{$SOURCE}.$APP_LINK_HOST/{$PATH}?$UTM_MEDIUM={$UTM_MEDIUM}&$UTM_CONTENT={$UTM_CONTENT}&$UTM_SOURCE={$UTM_SOURCE}&$UTM_CAMPAIGN={$UTM_CAMPAIGN}"
    },
  ),
) { arguments, navigate, navigateBack ->
  val path = arguments?.getString(PATH)
  val source = when (path) {
    "app" -> "$PACKAGE_UNAME=${arguments.getString(SOURCE)}"
    else -> arguments?.getString(SOURCE)!!
  }
  val isRtb = AnalyticsContext.current.isRTB()
  val utmsMap = mapOfNonNull(
    UTM_CAMPAIGN to arguments.getString(UTM_CAMPAIGN),
    UTM_MEDIUM to arguments.getString(UTM_MEDIUM),
    UTM_CONTENT to arguments.getString(UTM_CONTENT),
    UTM_SOURCE to arguments.getString(UTM_SOURCE),
  )

  val isGamified = arguments.getBoolean(IS_GAMIFIED, false)

  AppViewScreen(
    source = if (isRtb) source else source.appendIfRequired(BuildConfig.MARKET_NAME),
    navigate = navigate,
    navigateBack = navigateBack,
    utmsMap = utmsMap,
    isGamified = isGamified
  )
}

fun buildAppViewRoute(
  appSource: AppSource,
  utmCampaign: String? = "",
  utmMedium: String? = "",
  utmContent: String? = "",
  utmSource: String? = "",
  isGamified: Boolean = false
): String =
  appViewRoute.replace("{$SOURCE}", appSource.asSource())
    .withParameter(UTM_CAMPAIGN, utmCampaign)
    .withParameter(UTM_MEDIUM, utmMedium)
    .withParameter(UTM_CONTENT, utmContent)
    .withParameter(UTM_SOURCE, utmSource)
    .withParameter(IS_GAMIFIED, isGamified.toString())

fun buildAppViewDeepLinkUri(appSource: AppSource) =
  BuildConfig.DEEP_LINK_SCHEMA + buildAppViewRoute(appSource)

@Composable
fun AppViewScreen(
  source: String,
  navigate: (String) -> Unit,
  navigateBack: () -> Unit,
  utmsMap: Map<String, String>,
  isGamified: Boolean
) {
  val (uiState, reload) = rememberApp(source = source)
  val analyticsContext = AnalyticsContext.current
  val generalAnalytics = rememberGeneralAnalytics()

  val relatedEditorialsUiState = (uiState as? AppUiState.Idle)?.app?.packageName?.let {
    rememberRelatedEditorials(packageName = it)
  }

  val tabsList by remember(relatedEditorialsUiState, isGamified) {
    derivedStateOf {
      tabsList
        .let {
          if (relatedEditorialsUiState.isNullOrEmpty()) {
            it.filter { it != AppViewTab.RELATED }
          } else {
            it
          }
        }
        .let {
          if (isGamified) {
            it
          } else {
            it.filter { it != AppViewTab.REWARDS }
          }
        }
    }
  }

  MainAppViewView(
    uiState = uiState,
    reload = reload,
    noNetworkReload = reload,
    navigate = navigate,
    navigateBack = {
      generalAnalytics.sendBackButtonClick(analyticsContext)
      navigateBack()
    },
    tabsList = tabsList,
    utmsMap = utmsMap,
    isGamified = isGamified
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
  utmsMap: Map<String, String>,
  isGamified: Boolean,
) {
  when (uiState) {
    is AppUiState.Idle ->
      AppViewContent(
        app = uiState.app,
        tabsList = tabsList,
        navigate = navigate,
        navigateBack = navigateBack,
        utmsMap = utmsMap,
        isGamified = isGamified
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
    IndeterminateCircularLoading(color = Palette.Primary)
  }
}

@Composable
fun AppViewContent(
  app: App,
  tabsList: List<AppViewTab>,
  navigate: (String) -> Unit,
  navigateBack: () -> Unit,
  utmsMap: Map<String, String>,
  isGamified: Boolean
) {
  val bonusBundle = rememberBonusBundle()

  var selectedTab by rememberSaveable(key = tabsList.size.toString()) { mutableIntStateOf(0) }
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
      val videoId = app.videos[0].extractVideoId()
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
      app.campaigns?.deepLinkUtms = utmsMap

      AppPresentationView(app)

      if (isGamified) {
        PaEInstallView(
          modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp),
          app = app,
          navigate = navigate
        )
      } else {
        InstallView(
          modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp),
          app = app
        )
      }


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
          .clickable {
            val bonusTitle = bonusBundle.first
            val bonusTag = bonusBundle.second
            val route = buildSeeMoreBonusRoute(encode(bonusTitle), "${bonusTag}-more")

            navigate(route)
          }
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
          text = stringResource(
            id = R.string.bonus_banner_title,
            "20" //TODO Hardcoded value (should come from backend in the future)
          ),
          style = AGTypography.InputsM,
          outlineWidth = 10f,
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
    modifier = Modifier
      .padding(top = 24.dp, bottom = 4.dp)
      .height(40.dp),
    tabs = tabsList.map { it.getTabName() },
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

    AppViewTab.REWARDS -> AppRewardsView(packageName = app.packageName)

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
    app.news?.let {
      WhatsNew(app = app)
    }

    app.description?.let {
      Text(
        text = it,
        modifier = Modifier.padding(top = 16.dp, bottom = 32.dp, start = 16.dp, end = 16.dp),
        style = AGTypography.ArticleText,
        color = Palette.White
      )
    }
  }
}

@Composable
fun LatestBox() {
  Box(
    modifier = Modifier
      .defaultMinSize(minHeight = 24.dp, minWidth = 68.dp)
      .background(Palette.GreyLight),
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        modifier = Modifier
          .padding(start = 3.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
          .size(16.dp),
        imageVector = getBookmarkStar(Palette.Black),
        contentDescription = "bookmark star",
        tint = Color.Unspecified
      )
      Text(
        modifier = Modifier
          .padding(end = 8.dp),
        text = stringResource(id = R.string.latest_title),
        color = Palette.Black,
        style = AGTypography.InputsS,
        textAlign = TextAlign.Center,
      )
    }

  }
}

@Composable
fun WhatsNew(app: App) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp)
      .background(Palette.GreyDark)
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      LatestBox()
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 16.dp, end = 16.dp, top = 18.dp)
      ) {
        Text(
          modifier = Modifier
            .padding(end = 8.dp)
            .alignByBaseline(),
          text = app.versionName,
          color = Palette.Primary,
          style = AGTypography.InputsL,
        )
        Text(
          modifier = Modifier
            .alignByBaseline(),
          text = TextFormatter.formatBytes(app.appSize),
          color = Palette.GreyLight,
          style = AGTypography.InputsM
        )
        Spacer(modifier = Modifier.weight(1f))
        app.updateDate?.let {
          Text(
            modifier = Modifier
              .align(Alignment.CenterVertically),
            text = it.parseDate()?.toFormattedString(pattern = "dd.MM.yyyy") ?: "",
            color = Palette.GreyLight,
            style = AGTypography.SmallGames,
          )
        }
      }
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(
            start = 16.dp,
            end = 16.dp,
            top = 12.dp,
            bottom = 16.dp,
          )
      ) {
        Text(
          text = stringResource(id = R.string.whats_new_title),
          color = Palette.White,
          style = AGTypography.InputsM,
        )
        Text(
          text = app.news!!,
          color = Palette.White,
          style = AGTypography.SmallGames,
        )
      }
    }
  }
}

@Composable
fun ScreenshotsList(screenshots: List<Screenshot>) {
  val density = LocalDensity.current.density
  val densityDpi = LocalConfiguration.current.densityDpi

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
      AptoideAsyncImageWithFullscreen(
        modifier = Modifier
          .clearAndSetSemantics {
            contentDescription = stringResource
          }
          .height(152.dp)
          .aspectRatio(screenshot.width.toFloat() / screenshot.height.toFloat()),
        data = screenshot.url.toImageUrlByDensity(
          density = density,
          densityDpi = densityDpi,
          isPortrait = screenshot.height > screenshot.width
        ),
        contentDescription = null,
        selectedIndex = index,
        images = screenshots.map { it.url },
        contentScale = ContentScale.Fit,
      )
    }
  }
}

fun String.toImageUrlByDensity(density: Float, densityDpi: Int, isPortrait: Boolean): String {
  if (this.contains("_screen")) {
    val densityMultiplier = getDensityMultiplier(density)
    val dpi = getDensityDpi(densityDpi)
    val defaultSize = if (isPortrait) 96 else 256
    val splitUrl = this.split("_screen")
    return splitUrl[0] + "_screen_" + (defaultSize * densityMultiplier.toInt()).toString() + "x" + dpi + splitUrl[1]
  } else {
    return this
  }
}

private fun getDensityMultiplier(density: Float): Float {
  return if (density <= 0.75f) {
    0.75f
  } else if (density <= 1f) {
    1f
  } else if (density <= 1.333f) {
    1.3312500f
  } else if (density <= 1.5f) {
    1.5f
  } else if (density <= 2f) {
    2f
  } else if (density <= 3f) {
    3f
  } else {
    4f
  }
}

private fun getDensityDpi(densityDpi: Int): Int {
  return if (densityDpi <= 120) {
    120
  } else if (densityDpi <= 160) {
    160
  } else if (densityDpi <= 213) {
    213
  } else if (densityDpi <= 240) {
    240
  } else if (densityDpi <= 320) {
    320
  } else if (densityDpi <= 480) {
    480
  } else {
    640
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
      infoCategory = stringResource(R.string.appview_info_version_name_title),
      infoContent = app.versionName
    )
    AppInfoRow(
      infoCategory = stringResource(R.string.appview_info_package_name_title),
      infoContent = app.packageName
    )
    AppInfoRow(
      infoCategory = stringResource(R.string.appview_info_release_title),
      infoContent = app.releaseDate
        ?.parseDate()
        ?.toFormattedString(pattern = "d MMM yyyy") ?: ""
    )
    AppInfoRow(
      infoCategory = stringResource(R.string.appview_info_update_title),
      infoContent = app.updateDate
        ?.parseDate(pattern = "yyyy-MM-dd")
        ?.toFormattedString(pattern = "d MMM yyyy") ?: ""
    )
    AppInfoRow(
      infoCategory = stringResource(R.string.appview_info_download_size_title),
      infoContent = TextFormatter.formatBytes(app.appSize)
    )
    app.website?.let {
      AppInfoRowWithAction(
        infoCategory = stringResource(R.string.appview_info_website_title),
        onClick = { context.openUrlInBrowser(it) }
      )
    }
    app.email?.let {
      val subject = stringResource(R.string.app_info_send_email_subject)
      AppInfoRowWithAction(
        infoCategory = stringResource(R.string.appview_info_email_title),
        onClick = { context.sendMail(it, subject) }
      )
    }
    app.privacyPolicy?.let {
      AppInfoRowWithAction(
        infoCategory = stringResource(R.string.overflow_menu_privacy_policy),
        onClick = { context.openUrlInBrowser(it) }
      )
    }
    app.permissions?.let {
      AppInfoRowWithAction(
        infoCategory = stringResource(R.string.appview_info_permissions_title),
        onClick = {
          navigate(buildAppPermissionsRoute(app))
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
      IndeterminateCircularLoading(color = Palette.Primary)
    }
  } else if (state.isEmpty()) {
    SmallEmptyView(
      padding = PaddingValues(vertical = 64.dp, horizontal = 48.dp),
      title = stringResource(id = R.string.error_no_content_body)
    )
  } else {
    Column(
      modifier = Modifier.padding(all = 16.dp),
      verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
      state.forEach { articleMeta ->
        EditorialsViewCardLarge(
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
      AppRatingAndDownloads(
        rating = app.pRating,
        downloads = app.pDownloads
      )
    }
  }
}

@Composable
fun AppRatingAndDownloads(
  modifier: Modifier = Modifier,
  rating: Rating,
  downloads: Int? = null
) {
  Row(
    modifier = modifier
      .wrapContentHeight(
        unbounded = true,
        align = Alignment.Top
      ),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = getRatingStar(Palette.Black),
      contentDescription = null,
      modifier = Modifier
        .padding(end = 2.dp)
        .size(12.dp),
    )
    Text(
      modifier = modifier
        .padding(end = 8.dp),
      text = if (rating.avgRating == 0.0) {
        "--"
      } else {
        TextFormatter.formatDecimal(rating.avgRating)
      },
      maxLines = 1,
      style = AGTypography.InputsXS,
      overflow = TextOverflow.Ellipsis,
    )
    downloads?.let {
      Text(
        text = stringResource(R.string.downloads_number_title, it.formatDownloads()),
        maxLines = 1,
        style = AGTypography.InputsXS,
        overflow = TextOverflow.Ellipsis,
      )
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
      utmsMap = emptyMap(),
      isGamified = false
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
fun WhatsNewViewPreview() {
  AptoideTheme {
    WhatsNew(app = randomApp)
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
