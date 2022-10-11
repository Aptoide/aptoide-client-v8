package cm.aptoide.pt.feature_appview.presentation

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.aptoide_ui.theme.AptoideTheme
import cm.aptoide.pt.download_view.presentation.DownloadViewScreen
import cm.aptoide.pt.download_view.presentation.DownloadViewViewModel
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.DetailedApp
import cm.aptoide.pt.feature_apps.presentation.AppsListView
import cm.aptoide.pt.feature_appview.R
import cm.aptoide.pt.feature_appview.domain.model.RelatedCard
import cm.aptoide.pt.feature_report_app.presentation.ReportAppScreen
import cm.aptoide.pt.feature_report_app.presentation.ReportAppViewModel
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat

@Preview
@Composable
fun AppViewScreen(appViewViewModel: AppViewViewModel = hiltViewModel(), packageName: String?) {

  val uiState by appViewViewModel.uiState.collectAsState()

  AptoideTheme {
    val navController = rememberNavController()
    NavigationGraph(navController = navController,
      uiState,
      onSelectTab = { appViewViewModel.onSelectAppViewTab(it, uiState.app?.packageName) },
      onFinishedLoadingContent = { appViewViewModel.loadRecommendedApps(it) },
      onSelectReportApp = {
        navController.navigate(
          "reportApp/${it.name}/${
            URLEncoder.encode(
              it.icon,
              StandardCharsets.UTF_8.toString()
            )
          }/${it.versionName}/${it.malware}"
        )
      },
      onNavigateBack = {
        navController.popBackStack()
      })
  }
}

@Composable
fun MainAppViewView(
  uiState: AppViewUiState,
  onSelectTab: (AppViewTab) -> Unit,
  onFinishedLoadingContent: (String) -> Unit,
  onSelectReportApp: (DetailedApp) -> Unit,
  onNavigateBack: () -> Unit
) {
  Scaffold(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()
      .padding(bottom = 60.dp)
  ) { paddingValues ->
    if (!uiState.isLoading) {
      uiState.app?.let {
        AppViewContent(
          app = it,
          selectedTab = uiState.selectedTab,
          tabsList = uiState.tabsList,
          similarAppsList = uiState.similarAppsList,
          similarAppcAppsList = uiState.similarAppcAppsList,
          otherVersionsList = uiState.otherVersionsList,
          relatedContentList = uiState.relatedContent,
          onSelectTab = onSelectTab, onSelectReportApp,
          paddingValues, onNavigateBack
        )
        onFinishedLoadingContent(it.packageName)
      }

    }
  }
}

@Composable
fun AppViewContent(
  app: DetailedApp,
  selectedTab: AppViewTab,
  tabsList: List<AppViewTab>,
  similarAppsList: List<App>,
  similarAppcAppsList: List<App>,
  otherVersionsList: List<App>,
  relatedContentList: List<RelatedCard>,
  onSelectTab: (AppViewTab) -> Unit,
  onSelectReportApp: (DetailedApp) -> Unit,
  paddingValues: PaddingValues,
  onNavigateBack: () -> Unit,
) {

  val lazyListState = rememberLazyListState()
  var scrolledY = 0f
  var previousOffset = 0


  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(paddingValues), lazyListState
  ) {
    val listScope = this

    item {
      Box {
        Image(
          painter = rememberImagePainter(app.featureGraphic,
            builder = {
              placeholder(cm.aptoide.pt.feature_apps.R.drawable.ic_placeholder)
              transformations(RoundedCornersTransformation())
            }),
          contentDescription = "App Feature Graphic",
          modifier = Modifier
            .graphicsLayer {
              scrolledY += lazyListState.firstVisibleItemScrollOffset - previousOffset
              translationY = scrolledY * 0.8f
              previousOffset = lazyListState.firstVisibleItemScrollOffset
            }
            .height(208.dp)
            .fillMaxWidth()
        )
        TopAppBar(
          title = { }, backgroundColor = Color.Transparent.copy(alpha = 0.0f), elevation = 0.dp,
          navigationIcon = {
            IconButton(
              modifier = Modifier.alpha(ContentAlpha.medium),
              onClick = { onNavigateBack() }) {
              Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "AppViewBack",
                tint = Color.White
              )
            }
          },
        )
      }
    }

    item {
      AppPresentationView(app)
    }
    item {
      AppStatsView(app)
    }
    item {
      InstallButton(app)
    }

    item {
      AppInfoViewPager(
        selectedTab,
        tabsList,
        onSelectTab
      )
    }

    item {
      ViewPagerContent(
        app,
        selectedTab,
        similarAppsList,
        similarAppcAppsList,
        otherVersionsList,
        relatedContentList,
        onSelectReportApp,
        listScope
      )
    }
  }
}

@Composable
fun AppInfoViewPager(
  selectedTab: AppViewTab,
  tabsList: List<AppViewTab>,
  onSelectTab: (AppViewTab) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 16.dp, start = 16.dp, end = 16.dp)
  ) {
    ScrollableTabRow(
      selectedTabIndex = selectedTab.index,
      contentColor = Color(0xFFFE6446),
      backgroundColor = Color.Transparent,
      modifier = Modifier
        .fillMaxWidth(), edgePadding = 0.dp
    ) {
      tabsList.forEachIndexed { index, tab ->
        Tab(
          selected = selectedTab == tab,
          onClick = { onSelectTab(tab) },
        ) {
          Text(
            text = tab.tabName,
            modifier = Modifier.padding(bottom = 12.dp, start = 11.dp, end = 12.dp),
            fontSize = MaterialTheme.typography.subtitle1.fontSize
          )
        }
      }
    }
  }
}

@Composable
fun ViewPagerContent(
  app: DetailedApp,
  selectedTab: AppViewTab,
  similarAppsList: List<App>,
  similarAppcAppsList: List<App>,
  otherVersionsList: List<App>,
  relatedContentList: List<RelatedCard>,
  onSelectReportApp: (DetailedApp) -> Unit, listScope: LazyListScope?
) {

  when (selectedTab) {
    AppViewTab.DETAILS -> {
      DetailsView(app, similarAppsList, similarAppcAppsList, onSelectReportApp)
    }
    AppViewTab.REVIEWS -> {
      ReviewsView(app)
    }
    AppViewTab.RELATED -> {
      RelatedContentView(relatedContentList = relatedContentList, listScope)
    }
    AppViewTab.VERSIONS -> {
      OtherVersionsView(otherVersionsList = otherVersionsList, listScope)
    }
    AppViewTab.INFO -> {
      InfoView(app, onSelectReportApp)
    }
  }
}

@Composable
fun InfoView(app: DetailedApp, onSelectReportApp: (DetailedApp) -> Unit) {
  Column(modifier = Modifier.padding(top = 26.dp)) {
    StoreCard(app)
    AppInfoSection(app = app)
    CatappultPromotionCard()
    ReportAppCard(onSelectReportApp = onSelectReportApp, app = app)
  }
}


@Composable
fun CatappultPromotionCard() {
  Card(
    modifier = Modifier
      .padding(16.dp)
      .fillMaxWidth()
      .height(160.dp),
    backgroundColor = Color(0xFF190054),
    shape = MaterialTheme.shapes.medium
  ) {
    Column(
      verticalArrangement = Arrangement.SpaceEvenly,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Image(
        painter = rememberImagePainter(
          R.drawable.ic_catappult_white,
          builder = {
            placeholder(R.drawable.ic_catappult_white)
            transformations(RoundedCornersTransformation())
          }),
        contentDescription = "Catappult Icon",
        modifier = Modifier
          .width(125.dp)
          .height(13.dp), contentScale = ContentScale.Fit
      )
      Text(
        text = "Are you a developer ? Check the new way to distribute apps.",
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(start = 50.dp, end = 50.dp),
        fontSize = MaterialTheme.typography.body1.fontSize
      )
      Text(
        text = "KNOW MORE",
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = AppTheme.colors.appCoinsColor, fontSize = MaterialTheme.typography.caption.fontSize
      )

    }
  }
}

@Composable
fun AppInfoSection(app: DetailedApp) {
  Box(
    modifier = Modifier
      .padding(top = 26.dp, start = 32.dp, end = 32.dp)
      .fillMaxSize()
  ) {
    Column {
      AppInfoRow(infoCategory = "Package name", infoContent = app.packageName)
      app.releaseDate?.let {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(it)
        val dateFormatted = SimpleDateFormat("dd MMM yyyy").format(date!!)
        AppInfoRow(infoCategory = "Release", infoContent = dateFormatted)
      }
      app.updateDate?.let {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(it)
        val dateFormatted = SimpleDateFormat("dd MMM yyyy").format(date!!)
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
fun AppInfoRowWithButton(infoCategory: String, buttonUrl: String) {
  val localContext = LocalContext.current
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 12.dp)
  ) {
    Text(
      infoCategory,
      modifier = Modifier.align(Alignment.TopStart),
      fontSize = MaterialTheme.typography.body2.fontSize
    )
    Text(
      "MORE",
      modifier = Modifier
        .align(Alignment.TopEnd)
        .clickable {
          openTab(localContext, buttonUrl)
        },
      color = Color(0xFFFE6446),
      fontSize = MaterialTheme.typography.caption.fontSize
    )
  }
}


@Composable
fun AppInfoRow(infoCategory: String, infoContent: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      infoCategory,
      modifier = Modifier.padding(end = 16.dp),
      fontSize = MaterialTheme.typography.body2.fontSize,
      overflow = TextOverflow.Ellipsis
    )
    Text(
      infoContent,
      fontSize = MaterialTheme.typography.body2.fontSize,
      overflow = TextOverflow.Ellipsis
    )
  }
}

@Composable
fun StoreCard(app: DetailedApp) {
  Card(
    modifier = Modifier
      .padding(start = 16.dp, end = 16.dp)
      .height(104.dp)
      .fillMaxWidth(), shape = MaterialTheme.shapes.medium
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
    ) {
      Column(
        modifier = Modifier
          .padding(top = 8.dp, start = 16.dp)
          .align(Alignment.TopStart)
      ) {
        Text(
          text = "App available in",
          modifier = Modifier.padding(bottom = 12.dp),
          fontSize = MaterialTheme.typography.body2.fontSize
        )
        Row(modifier = Modifier.fillMaxWidth()) {
          Image(
            painter = rememberImagePainter(app.store.icon,
              builder = {
                placeholder(cm.aptoide.pt.feature_apps.R.drawable.ic_placeholder)
                transformations(RoundedCornersTransformation())
              }),
            contentDescription = "Store Avatar",
            modifier = Modifier
              .width(48.dp)
              .height(48.dp)
              .padding(bottom = 8.dp)
          )
          Column(modifier = Modifier.padding(top = 2.dp, start = 8.dp)) {
            Text(
              text = app.store.storeName,
              modifier = Modifier.padding(bottom = 4.dp),
              fontSize = MaterialTheme.typography.body2.fontSize
            )
            Text(
              text = "" + app.store.apps?.let { TextFormatter.withSuffix(it) } + " Apps",
              modifier = Modifier.padding(bottom = 2.dp),
              fontSize = MaterialTheme.typography.overline.fontSize
            )
          }
        }
      }

      Button(
        onClick = { /*TODO*/ },
        shape = CircleShape,
        modifier = Modifier
          .height(48.dp)
          .width(120.dp)
          .align(Alignment.BottomEnd)
          .padding(bottom = 16.dp, end = 16.dp)
      ) {
        Text("Follow", maxLines = 1)
      }
    }
  }
}

@Composable
fun DetailsView(
  app: DetailedApp,
  similarAppsList: List<App>,
  similarAppcAppsList: List<App>,
  onSelectReportApp: (DetailedApp) -> Unit
) {
  Column(modifier = Modifier.padding(top = 16.dp)) {
    app.screenshots?.let { ScreenshotsList(it) }
    app.description?.let {
      Text(
        text = it,
        modifier = Modifier.padding(top = 12.dp, bottom = 26.dp, start = 16.dp, end = 16.dp),
        fontSize = MaterialTheme.typography.body2.fontSize
      )
    }
    if (app.isAppCoins && similarAppcAppsList.isNotEmpty()) {
      Column(modifier = Modifier.padding(start = 16.dp, bottom = 24.dp)) {
        Text(
          text = "AppCoins Apps",
          style = MaterialTheme.typography.h2,
          modifier = Modifier.padding(bottom = 12.dp)
        )
        AppsListView(appsList = similarAppcAppsList)
      }
    }
    if (similarAppsList.isNotEmpty()) {
      Column(modifier = Modifier.padding(start = 16.dp, bottom = 24.dp)) {
        Text(
          text = "Similar Apps",
          style = MaterialTheme.typography.h2,
          modifier = Modifier.padding(bottom = 12.dp)
        )
        AppsListView(appsList = similarAppsList)
      }
    }
    ReportAppCard(onSelectReportApp, app)
  }

}

@Composable
fun ReportAppCard(onSelectReportApp: (DetailedApp) -> Unit, app: DetailedApp) {
  Card(
    modifier = Modifier
      .height(48.dp)
      .padding(start = 16.dp, end = 16.dp)
      .fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Start
    ) {
      Image(
        painter = rememberImagePainter(R.drawable.ic_icon_report,
          builder = {
            placeholder(cm.aptoide.pt.feature_apps.R.drawable.ic_placeholder)
            transformations(RoundedCornersTransformation())
          }),
        contentDescription = "Report icon",
        modifier = Modifier
          .padding(start = 16.dp, end = 8.dp)
          .width(16.dp)
          .height(16.dp)
      )
      Text(
        text = "Have you noticed a problem with the app?",
        fontSize = MaterialTheme.typography.caption.fontSize,
        modifier = Modifier.padding(end = 12.dp), overflow = TextOverflow.Ellipsis
      )
      Text(
        text = "REPORT",
        color = Color(0xFFFE6446),
        fontSize = MaterialTheme.typography.caption.fontSize,
        modifier = Modifier.clickable { onSelectReportApp(app) },
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
fun ScreenshotsList(screenshots: List<String>) {
  LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    items(screenshots) { screenshot ->
      Image(
        painter = rememberImagePainter(screenshot,
          builder = {
            transformations(RoundedCornersTransformation(24f))
          }), contentDescription = "Screenshot",
        modifier = Modifier
          .size(268.dp, 152.dp)
      )
    }
  }
}

@Composable
fun InstallButton(app: DetailedApp) {
  val downloadViewViewModel = hiltViewModel<DownloadViewViewModel>()
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .offset(0.dp, (-24).dp)
      .background(AppTheme.colors.background)
  ) {
    DownloadViewScreen(downloadViewViewModel, app)
  }
}

@Composable
fun AppStatsView(app: DetailedApp) {
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
        .padding(start = 16.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier.padding(end = 40.dp, start = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "" + TextFormatter.withSuffix(app.downloads.toLong()),
          style = AppTheme.typography.medium_M
        )
        Text(text = "Downloads", style = AppTheme.typography.regular_XXS)
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
        Text(text = "Last Version", style = AppTheme.typography.regular_XXS)
      }

      Column(
        modifier = Modifier.padding(end = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Image(
            painter = rememberImagePainter(
              R.drawable.ic_icon_star,
              builder = {
                placeholder(R.drawable.ic_icon_star)
                transformations(RoundedCornersTransformation())
              }),
            contentDescription = "App Stats rating",
            modifier = Modifier
              .padding(end = 2.dp)
              .size(12.dp)
          )
          Text(
            text = "" + TextFormatter.formatDecimal(app.rating.avgRating),
            style = AppTheme.typography.medium_M
          )
        }
        Text(text = "Rating", style = AppTheme.typography.regular_XXS)
      }
    }

  }
}

@Composable
fun AppPresentationView(app: DetailedApp) {
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
      Image(
        painter = rememberImagePainter(app.icon,
          builder = {
            transformations(RoundedCornersTransformation(16f))
          }), contentDescription = "App icon",
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
              painter = painterResource(id = R.drawable.ic_icon_trusted),
              contentDescription = "Trusted icon",
              modifier = Modifier
                .size(16.dp, 16.dp)
                .wrapContentHeight(Alignment.CenterVertically)
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

@Composable
private fun NavigationGraph(
  navController: NavHostController,
  uiState: AppViewUiState,
  onSelectTab: (AppViewTab) -> Unit,
  onFinishedLoadingContent: (String) -> Unit,
  onSelectReportApp: (DetailedApp) -> Unit,
  onNavigateBack: () -> Unit
) {
  NavHost(
    navController = navController,
    startDestination = "appview"
  ) {
    composable(
      "reportApp/{appName}/{appIcon}/{versionName}/{malwareRank}"
    ) {

      val appName = it.arguments?.getString("appName")
      val appIcon = it.arguments?.getString("appIcon")
      val versionName = it.arguments?.getString("versionName")
      val malwareRank = it.arguments?.getString("malwareRank")

      val viewModel = hiltViewModel<ReportAppViewModel>()

      ReportAppScreen(
        reportAppViewModel = viewModel,
        appName = appName,
        appIcon = appIcon,
        versionName = versionName,
        malwareRank = malwareRank
      )
    }
    composable("appview") {
      MainAppViewView(
        uiState = uiState,
        onSelectTab = onSelectTab,
        onFinishedLoadingContent = onFinishedLoadingContent, onSelectReportApp, onNavigateBack
      )
    }
  }
}

fun openTab(context: Context, url: String) {
  val packageName = "com.android.chrome"

  val builder = CustomTabsIntent.Builder()
  builder.setShowTitle(true)
  builder.setInstantAppsEnabled(true)
  builder.setToolbarColor(ContextCompat.getColor(context, R.color.cardview_shadow_end_color))
  val customBuilder = builder.build()

  customBuilder.intent.setPackage(packageName)
  customBuilder.launchUrl(context, Uri.parse(url))

}