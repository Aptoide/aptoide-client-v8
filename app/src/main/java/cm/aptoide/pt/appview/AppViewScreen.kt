package cm.aptoide.pt.appview

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraphBuilder
import cm.aptoide.pt.aptoide_ui.AptoideAsyncImage
import cm.aptoide.pt.aptoide_ui.R
import cm.aptoide.pt.aptoide_ui.animations.animatedComposable
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.aptoide_ui.toolbar.AppViewTopBar
import cm.aptoide.pt.download_view.presentation.DownloadViewScreen
import cm.aptoide.pt.editorial.buildEditorialRoute
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.presentation.AppUiState
import cm.aptoide.pt.feature_apps.presentation.AppsRowView
import cm.aptoide.pt.feature_apps.presentation.appViewModel
import cm.aptoide.pt.feature_appview.presentation.AppViewTab
import cm.aptoide.pt.feature_appview.presentation.CustomScrollableTabRow
import cm.aptoide.pt.feature_editorial.presentation.relatedEditorialsCardViewModel

private val tabsList = listOf(
  AppViewTab.DETAILS,
  AppViewTab.REVIEWS,
  AppViewTab.RELATED,
  AppViewTab.VERSIONS,
  AppViewTab.INFO,
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
){
  val appViewModel = appViewModel(packageName = packageName, adListId = "")
  val uiState by appViewModel.uiState.collectAsState()

  val editorialsCardViewModel = relatedEditorialsCardViewModel(packageName = packageName)
  val relatedEditorialsUiState by editorialsCardViewModel.uiState.collectAsState()

  val tabsList by remember {
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
    onSelectReportApp = {
      navigate(buildReportAppRoute(it.name, it.icon, it.versionName, it.malware))
    },
    onAppClick = {
      navigate(buildAppViewRoute(it))
    },
    onRelatedContentClick = {
      navigate(buildEditorialRoute(it))
    },
    onNavigateBack = navigateBack,
    tabsList = tabsList
  )
}
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainAppViewView(
  uiState: AppUiState,
  onSelectReportApp: (App) -> Unit,
  onAppClick: (String) -> Unit,
  onRelatedContentClick: (String) -> Unit,
  onNavigateBack: () -> Unit,
  tabsList: List<AppViewTab>,
) {
  Scaffold(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()
  ) { paddingValues ->
    if (uiState is AppUiState.Idle) {
      AppViewContent(
        app = uiState.app,
        tabsList = tabsList,
        onSelectReportApp = onSelectReportApp,
        onAppClick = onAppClick,
        onRelatedContentClick = onRelatedContentClick,
        paddingValues = paddingValues,
        onNavigateBack = onNavigateBack,
      )
    }
  }
}

@Composable
fun AppViewContent(
  app: App,
  tabsList: List<AppViewTab>,
  onSelectReportApp: (App) -> Unit,
  onAppClick: (String) -> Unit,
  onRelatedContentClick: (String) -> Unit,
  paddingValues: PaddingValues,
  onNavigateBack: () -> Unit,
) {
  val selectedTab = rememberSaveable { mutableStateOf(0) }
  val lazyListState = rememberLazyListState()
  var scrolledY = 0f
  var previousOffset = 0

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(paddingValues), lazyListState
  ) {

    item {
      Box {
        AptoideAsyncImage(
          data = app.featureGraphic,
          contentDescription = "App Feature Graphic",
          placeholder = ColorPainter(AppTheme.colors.placeholderColor),
          modifier = Modifier
            .graphicsLayer {
              scrolledY += lazyListState.firstVisibleItemScrollOffset - previousOffset
              translationY = scrolledY * 0.8f
              previousOffset = lazyListState.firstVisibleItemScrollOffset
            }
            .height(208.dp)
            .fillMaxWidth()
        )
        AppViewTopBar(onBackPressed = onNavigateBack)
      }
    }
    item { AppPresentationView(app) }
    item { AppStatsView(app) }
    item { InstallButton(app) }
    item {
      AppInfoViewPager(
        selectedTab = selectedTab.value,
        tabsList = tabsList,
        onSelectTab = { selectedTab.value = it }
      )
    }

    item {
      ViewPagerContent(
        app = app,
        selectedTab = tabsList[selectedTab.value],
        onSelectReportApp = onSelectReportApp,
        onAppClick = onAppClick,
        onRelatedContentClick = onRelatedContentClick
      )
    }
  }
}

@Composable
fun AppInfoViewPager(
  selectedTab: Int,
  tabsList: List<AppViewTab>,
  onSelectTab: (Int) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 16.dp)
  ) {
    CustomScrollableTabRow(
      tabs = tabsList,
      selectedTabIndex = selectedTab,
      onTabClick = onSelectTab,
      contentColor = AppTheme.colors.appViewTabRowColor,
      backgroundColor = Color.Transparent
    )
  }
}

@Composable
fun ViewPagerContent(
  app: App,
  selectedTab: AppViewTab,
  onSelectReportApp: (App) -> Unit,
  onAppClick: (String) -> Unit,
  onRelatedContentClick: (String) -> Unit
) {
  when (selectedTab) {
    AppViewTab.DETAILS -> DetailsView(
      app = app,
      onSelectReportApp = onSelectReportApp,
      onAppClick = onAppClick
    )

    AppViewTab.REVIEWS -> ReviewsView(app)
    AppViewTab.RELATED -> RelatedContentView(
      packageName = app.packageName,
      onRelatedContentClick = onRelatedContentClick
    )

    AppViewTab.VERSIONS -> OtherVersionsView(
      packageName = app.packageName
    )

    AppViewTab.INFO -> InfoView(
      app = app,
      onSelectReportApp = onSelectReportApp
    )
  }
}

@Composable
fun InfoView(
  app: App,
  onSelectReportApp: (App) -> Unit,
) {
  Column(modifier = Modifier.padding(top = 24.dp)) {
    StoreCard(app)
    AppInfoSection(app = app)
    CatappultPromotionCard()
    ReportAppCard(
      onSelectReportApp = onSelectReportApp,
      app = app
    )
  }
}

@Composable
fun CatappultPromotionCard() {
  Card(
    modifier = Modifier
      .padding(16.dp)
      .fillMaxWidth()
      .height(160.dp)
      .clip(RoundedCornerShape(24.dp)),
    backgroundColor = AppTheme.colors.catappultBackgroundColor
  ) {
    Column(
      verticalArrangement = Arrangement.SpaceEvenly,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Image(
        painter = painterResource(id = R.drawable.ic_catappult_white),
        contentDescription = "Catappult Icon",
        modifier = Modifier
          .padding(bottom = 18.dp)
          .width(125.dp)
          .height(13.dp)
      )
      Text(
        text = "Are you a developer ? Check the new way to distribute apps.",
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(start = 50.dp, end = 50.dp, bottom = 12.dp),
        style = AppTheme.typography.regular_M
      )
      Text(
        text = "KNOW MORE",
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = AppTheme.colors.appCoinsColor, style = AppTheme.typography.button_M
      )
    }
  }
}

@Composable
fun AppInfoSection(app: App) {
  Box(
    modifier = Modifier
      .padding(top = 24.dp, start = 32.dp, end = 32.dp)
      .fillMaxSize()
  ) {
    Column {
      AppInfoRow(infoCategory = "Package name", infoContent = app.packageName)
      app.releaseDate?.let {
        val dateFormatted = TextFormatter.formatDateToSystemLocale(
          LocalContext.current,
          it/*, outputPattern = "dd MMM yyyy"*/
        )
        AppInfoRow(infoCategory = "Release", infoContent = dateFormatted)
      }
      app.updateDate?.let {
        val dateFormatted = TextFormatter.formatDateToSystemLocale(
          LocalContext.current,
          it/*, outputPattern = "dd MMM yyyy"*/
        )
        AppInfoRow(infoCategory = "Updated on", infoContent = dateFormatted)
      }
      AppInfoRow(
        infoCategory = "Downloads",
        infoContent = "" + TextFormatter.withSuffix(app.downloads.toLong())
      )
      AppInfoRow(
        infoCategory = "Download size",
        infoContent = "" + TextFormatter.formatBytes(app.appSize)
      )
      app.website?.let { AppInfoRowWithButton(infoCategory = "Website", buttonUrl = it) }
      app.email?.let { AppInfoRowWithButton(infoCategory = "Email", buttonUrl = it) }
      app.privacyPolicy?.let {
        AppInfoRowWithButton(
          infoCategory = "Privacy Policy",
          buttonUrl = it
        )
      }
      AppInfoRowWithButton(infoCategory = "Permissions", buttonUrl = app.permissions.toString())
    }
  }
}

@Composable
fun AppInfoRowWithButton(
  infoCategory: String,
  buttonUrl: String,
) {
  val localContext = LocalContext.current
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 12.dp)
  ) {
    Text(
      text = infoCategory,
      modifier = Modifier.align(Alignment.TopStart),
      style = AppTheme.typography.regular_S
    )
    Text(
      text = "MORE",
      modifier = Modifier
        .align(Alignment.TopEnd)
        .clickable { openTab(localContext, buttonUrl) },
      color = AppTheme.colors.primary,
      style = AppTheme.typography.button_M
    )
  }
}

@Composable
fun AppInfoRow(
  infoCategory: String,
  infoContent: String,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      infoCategory,
      modifier = Modifier.padding(end = 16.dp),
      style = AppTheme.typography.regular_S,
      overflow = TextOverflow.Ellipsis
    )
    Text(
      infoContent,
      style = AppTheme.typography.regular_S,
      overflow = TextOverflow.Ellipsis,
      color = AppTheme.colors.greyText
    )
  }
}

@Composable
fun StoreCard(app: App) {
  Card(
    modifier = Modifier
      .padding(start = 16.dp, end = 16.dp)
      .height(104.dp)
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(color = AppTheme.colors.storeCardBackgroundColor)
    ) {
      Column(
        modifier = Modifier
          .padding(top = 8.dp, start = 16.dp)
          .align(Alignment.TopStart)
      ) {
        Text(
          text = "App available in",
          modifier = Modifier.padding(bottom = 12.dp),
          style = AppTheme.typography.regular_S
        )
        Row(modifier = Modifier.fillMaxWidth()) {
          AptoideAsyncImage(
            data = app.store.icon,
            contentDescription = "Store Avatar",
            placeholder = ColorPainter(AppTheme.colors.placeholderColor),
            modifier = Modifier
              .padding(bottom = 16.dp)
              .width(48.dp)
              .height(48.dp)
              .clip(RoundedCornerShape(16.dp))
          )
          Column(modifier = Modifier.padding(top = 2.dp, start = 8.dp)) {
            Text(
              text = app.store.storeName,
              modifier = Modifier.padding(bottom = 4.dp),
              style = AppTheme.typography.medium_S
            )
            Text(
              text = "" + app.store.apps?.let { TextFormatter.withSuffix(it) } + " Apps",
              style = AppTheme.typography.regular_XXS,
              color = AppTheme.colors.storeNumberOfApps
            )
          }
        }
      }

      OutlinedButton(
        onClick = { /*TODO*/ },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, AppTheme.colors.primary),
        modifier = Modifier
          .padding(bottom = 16.dp, end = 16.dp)
          .height(48.dp)
          .width(120.dp)
          .align(Alignment.BottomEnd)
      ) {
        Text(
          text = "FOLLOW",
          maxLines = 1,
          color = AppTheme.colors.primary
        )
      }
    }
  }
}

@Composable
fun DetailsView(
  app: App,
  similarAppsList: List<App> = emptyList(),
  similarAppcAppsList: List<App> = emptyList(),
  onSelectReportApp: (App) -> Unit,
  onAppClick: (String) -> Unit,
) {
  Column(
    modifier = Modifier
      .padding(top = 16.dp)
      .background(color = AppTheme.colors.background)
  ) {
    app.screenshots?.let { ScreenshotsList(it) }
    app.description?.let {
      Text(
        text = it,
        modifier = Modifier.padding(top = 12.dp, bottom = 26.dp, start = 16.dp, end = 16.dp),
        style = AppTheme.typography.regular_S
      )
    }
    if (app.isAppCoins && similarAppcAppsList.isNotEmpty()) {
      Column(modifier = Modifier.padding(start = 16.dp, bottom = 24.dp)) {
        Text(
          text = "AppCoins Apps",
          style = AppTheme.typography.medium_M,
          modifier = Modifier.padding(bottom = 12.dp)
        )
        AppsRowView(appsList = similarAppcAppsList, onAppClick = onAppClick)
      }
    }
    if (similarAppsList.isNotEmpty()) {
      Column(modifier = Modifier.padding(start = 16.dp, bottom = 24.dp)) {
        Text(
          text = "Similar Apps",
          style = AppTheme.typography.medium_M,
          modifier = Modifier.padding(bottom = 12.dp)
        )
        AppsRowView(appsList = similarAppsList, onAppClick = onAppClick)
      }
    }
    ReportAppCard(onSelectReportApp, app)
  }
}

@Composable
fun ReportAppCard(
  onSelectReportApp: (App) -> Unit,
  app: App,
) {
  Card(
    modifier = Modifier
      .padding(bottom = 24.dp)
      .height(48.dp)
      .padding(start = 16.dp, end = 16.dp)
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(color = AppTheme.colors.reportAppCardBackgroundColor)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Start
    ) {
      Image(
        imageVector = AppTheme.icons.ReportIcon,
        contentDescription = "Report icon",
        modifier = Modifier
          .padding(start = 16.dp, end = 8.dp)
          .size(16.dp)
      )
      Text(
        text = "Have you noticed any problem with the app?",
        style = AppTheme.typography.regular_XS,
        modifier = Modifier.padding(end = 12.dp),
        overflow = TextOverflow.Ellipsis
      )
      Text(
        text = "REPORT",
        color = AppTheme.colors.reportAppButtonTextColor,
        style = AppTheme.typography.button_M,
        modifier = Modifier
          .clickable { onSelectReportApp(app) }
          .padding(end = 16.dp),
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

@Composable
fun ScreenshotsList(screenshots: List<String>) {
  LazyRow(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    items(screenshots) { screenshot ->
      AptoideAsyncImage(
        data = screenshot,
        contentDescription = "Screenshot",
        placeholder = ColorPainter(AppTheme.colors.placeholderColor),
        modifier = Modifier
          .size(268.dp, 152.dp)
          .clip(RoundedCornerShape(24.dp))
      )
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

@Composable
fun AppStatsView(app: App) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .offset(0.dp, (-24).dp)
      .background(AppTheme.colors.background)
      .padding(bottom = 20.dp)
  ) {
    Row(
      horizontalArrangement = Arrangement.SpaceEvenly,
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier.padding(end = 40.dp, start = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "" + TextFormatter.withSuffix(app.downloads.toLong()),
          style = AppTheme.typography.medium_M
        )
        Text(
          text = "Downloads",
          style = AppTheme.typography.regular_XXS
        )
      }

      Column(
        modifier = Modifier.padding(end = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "" + app.versionName,
          maxLines = 1,
          style = AppTheme.typography.medium_M,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = "Last Version",
          style = AppTheme.typography.regular_XXS
        )
      }

      Column(
        modifier = Modifier.padding(end = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Image(
            imageVector = Icons.Filled.Star,
            colorFilter = ColorFilter.tint(AppTheme.colors.iconColor),
            contentDescription = "App Stats rating",
            modifier = Modifier
              .padding(end = 2.dp)
              .size(12.dp)
          )
          Text(
            text = if (app.pRating.avgRating == 0.0) "--"
            else TextFormatter.formatDecimal(app.pRating.avgRating),
            style = AppTheme.typography.medium_M
          )
        }
        Text(
          text = "Rating",
          style = AppTheme.typography.regular_XXS
        )
      }
    }
  }
}

@Composable
fun AppPresentationView(app: App) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .offset(0.dp, (-24).dp)
      .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
      .background(AppTheme.colors.background)
  ) {
    Row(
      modifier = Modifier
        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp)
        .height(88.dp)
    ) {
      AptoideAsyncImage(
        data = app.icon,
        contentDescription = "App Icon",
        placeholder = ColorPainter(AppTheme.colors.placeholderColor),
        modifier = Modifier
          .padding(end = 16.dp)
          .size(88.dp)
          .clip(RoundedCornerShape(16.dp))
      )
      Column(
        modifier = Modifier.height(88.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = app.name,
          maxLines = 1,
          style = AppTheme.typography.medium_L,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier
            .padding(top = 12.dp)
        )
        if (app.malware == "TRUSTED") {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
              imageVector = AppTheme.icons.TrustedIcon,
              contentDescription = "Trusted icon",
              modifier = Modifier.size(16.dp, 16.dp)
            )
            Text(
              text = "Trusted",
              color = AppTheme.colors.trustedColor,
              modifier = Modifier.padding(start = 4.dp),
              style = AppTheme.typography.medium_XS
            )
          }
        }
      }
    }
  }
}

fun openTab(
  context: Context,
  url: String,
) {
  val packageName = "com.android.chrome"

  val builder = CustomTabsIntent.Builder()
  builder.setShowTitle(true)
  builder.setInstantAppsEnabled(true)
  builder.setToolbarColor(
    ContextCompat.getColor(
      context,
      androidx.cardview.R.color.cardview_shadow_end_color
    )
  )
  val customBuilder = builder.build()

  customBuilder.intent.setPackage(packageName)
  customBuilder.launchUrl(context, Uri.parse(url))
}
