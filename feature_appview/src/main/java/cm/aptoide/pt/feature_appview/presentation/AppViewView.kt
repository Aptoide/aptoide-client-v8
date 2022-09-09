package cm.aptoide.pt.feature_appview.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cm.aptoide.pt.download_view.presentation.DownloadViewScreen
import cm.aptoide.pt.download_view.presentation.DownloadViewViewModel
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.presentation.AppsListView
import cm.aptoide.pt.feature_appview.R
import cm.aptoide.pt.feature_appview.domain.model.RelatedCard
import cm.aptoide.pt.feature_report_app.presentation.ReportAppScreen
import cm.aptoide.pt.feature_report_app.presentation.ReportAppViewModel
import cm.aptoide.pt.theme.AppTheme
import cm.aptoide.pt.theme.AptoideTheme
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*

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
      })
  }
}

@Composable
fun MainAppViewView(
  uiState: AppViewUiState,
  onSelectTab: (AppViewTab) -> Unit,
  onFinishedLoadingContent: (String) -> Unit,
  onSelectReportApp: (App) -> Unit
) {
  Scaffold(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()
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
          paddingValues
        )
        onFinishedLoadingContent(it.packageName)
      }

    }
  }
}

@Composable
fun AppViewContent(
  app: App,
  selectedTab: AppViewTab,
  tabsList: List<AppViewTab>,
  similarAppsList: List<App>,
  similarAppcAppsList: List<App>,
  otherVersionsList: List<App>,
  relatedContentList: List<RelatedCard>,
  onSelectTab: (AppViewTab) -> Unit,
  onSelectReportApp: (App) -> Unit,
  paddingValues: PaddingValues,
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(paddingValues)
  ) {
    val listScope = this

    item {
      Image(
        painter = rememberImagePainter(app.featureGraphic,
          builder = {
            placeholder(cm.aptoide.pt.feature_apps.R.drawable.ic_placeholder)
            transformations(RoundedCornersTransformation())
          }),
        contentDescription = "App Feature Graphic",
        modifier = Modifier
          .fillMaxWidth()
          .height(181.dp)
          .padding(bottom = 19.dp)
      )
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
      .padding(top = 42.dp, start = 16.dp, end = 16.dp)
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
  app: App,
  selectedTab: AppViewTab,
  similarAppsList: List<App>,
  similarAppcAppsList: List<App>,
  otherVersionsList: List<App>,
  relatedContentList: List<RelatedCard>,
  onSelectReportApp: (App) -> Unit, listScope: LazyListScope?
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
fun InfoView(app: App, onSelectReportApp: (App) -> Unit) {
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

fun withSuffix(count: Long): String? {
  if (count < 1000) {
    return count.toString()
  }
  val exp = (Math.log(count.toDouble()) / Math.log(1000.0)).toInt()
  return String.format(
    Locale.ENGLISH, "%d %c",
    (count / Math.pow(1000.0, exp.toDouble())).toInt(),
    "kMBTPE"[exp - 1]
  )
}

fun formatBytes(bytes: Long): String? {
  val unit = 1024
  if (bytes < unit) {
    return "$bytes B"
  }
  val exp = (Math.log(bytes.toDouble()) / Math.log(unit.toDouble())).toInt()
  val pre = "KMGTPE"[exp - 1].toString() + ""
  val string = String.format(
    Locale.ENGLISH,
    "%.1f %sB",
    bytes / Math.pow(unit.toDouble(), exp.toDouble()),
    pre
  )
  return string
}

@Composable
fun AppInfoSection(app: App) {
  Box(
    modifier = Modifier
      .padding(top = 26.dp, start = 32.dp, end = 32.dp)
      .fillMaxSize()
  ) {
    Column {
      AppInfoRow(infoCategory = "Package name", infoContent = app.packageName)
      app.releaseDate?.let { AppInfoRow(infoCategory = "Release", infoContent = it) }
      app.updateDate?.let { AppInfoRow(infoCategory = "Updated on", infoContent = it) }
      AppInfoRow(infoCategory = "Downloads", infoContent = "" + withSuffix(app.downloads.toLong()))
      AppInfoRow(infoCategory = "Download size", infoContent = "" + formatBytes(app.appSize))
      app.website?.let { AppInfoRowWithButton(infoCategory = "Website", buttonText = it) }
      app.email?.let { AppInfoRowWithButton(infoCategory = "Email", buttonText = it) }
      app.privacyPolicy?.let {
        AppInfoRowWithButton(
          infoCategory = "Privacy Policy",
          buttonText = it
        )
      }
      AppInfoRowWithButton(infoCategory = "Permissions", buttonText = app.permissions.toString())
    }
  }
}

@Composable
fun AppInfoRowWithButton(infoCategory: String, buttonText: String) {
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
      modifier = Modifier.align(Alignment.TopEnd),
      color = Color(0xFFFE6446),
      fontSize = MaterialTheme.typography.caption.fontSize
    )
  }
}


@Composable
fun AppInfoRow(infoCategory: String, infoContent: String) {
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
      infoContent,
      modifier = Modifier.align(Alignment.TopEnd),
      fontSize = MaterialTheme.typography.body2.fontSize
    )
  }
}

@Composable
fun StoreCard(app: App) {
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
              text = "" + app.store.apps?.let { withSuffix(it) } + " Apps",
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
  app: App,
  similarAppsList: List<App>,
  similarAppcAppsList: List<App>,
  onSelectReportApp: (App) -> Unit
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
fun ReportAppCard(onSelectReportApp: (App) -> Unit, app: App) {
  Card(
    modifier = Modifier
      .height(48.dp)
      .padding(start = 16.dp, end = 16.dp)
      .fillMaxWidth()
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = "Have you noticed a problem with the app?",
        fontSize = MaterialTheme.typography.caption.fontSize,
        modifier = Modifier.padding(end = 13.dp, start = 39.dp)
        //missing icon
      )
      Text(
        text = "REPORT",
        color = Color(0xFFFE6446),
        fontSize = MaterialTheme.typography.caption.fontSize,
        modifier = Modifier.clickable { onSelectReportApp(app) }
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
fun InstallButton(app: App) {
  val downloadViewViewModel = hiltViewModel<DownloadViewViewModel>()
  DownloadViewScreen(downloadViewViewModel, app)
}

@Composable
fun AppStatsView(app: App) {
  Row(
    horizontalArrangement = Arrangement.SpaceEvenly,
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 20.dp, start = 16.dp, end = 16.dp)
  ) {
    Column(modifier = Modifier.padding(end = 40.dp, start = 22.dp)) {
      Text(
        text = "" + withSuffix(app.downloads.toLong()),
        fontSize = MaterialTheme.typography.body1.fontSize
      )
      Text(text = "Downloads", fontSize = MaterialTheme.typography.overline.fontSize)
    }

    Column(modifier = Modifier.padding(end = 40.dp)) {
      Text(
        text = "" + app.versionName,
        maxLines = 1,
        fontSize = MaterialTheme.typography.body1.fontSize,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.fillMaxWidth(0.33f)
      )
      Text(text = "Last Version", fontSize = MaterialTheme.typography.overline.fontSize)
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
            .width(12.dp)
            .height(12.dp)
            .padding(end = 2.dp)
        )
        Text(text = "" + app.rating.avgRating, fontSize = MaterialTheme.typography.body1.fontSize)
      }
      Text(text = "Rating", fontSize = MaterialTheme.typography.overline.fontSize)
    }
  }
}

@Composable
fun AppPresentationView(app: App) {
  Row(
    modifier = Modifier
      .height(88.dp)
      .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
  ) {
    Image(
      painter = rememberImagePainter(app.icon,
        builder = {
          transformations(RoundedCornersTransformation(16f))
        }), contentDescription = "App icon",
      modifier = Modifier
        .size(88.dp, 88.dp)
        .padding(end = 16.dp)
    )
    Column(
      modifier = Modifier.height(88.dp),
      horizontalAlignment = Alignment.Start,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = app.name,
        maxLines = 1,
        fontSize = MaterialTheme.typography.h6.fontSize,
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
            color = Color.Green,
            modifier = Modifier.padding(start = 4.dp),
            fontSize = MaterialTheme.typography.caption.fontSize
          )
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
  onFinishedLoadingContent: (String) -> Unit, onSelectReportApp: (App) -> Unit
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
        onFinishedLoadingContent = onFinishedLoadingContent, onSelectReportApp
      )
    }
  }
}
