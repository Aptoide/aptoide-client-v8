package cm.aptoide.pt.feature_appview.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.feature_apps.data.App
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation

@Preview
@Composable
fun AppViewScreen(appViewViewModel: AppViewViewModel = hiltViewModel()) {

  val uiState by appViewViewModel.uiState.collectAsState()

  MainAppViewView(uiState) { appViewViewModel.onSelectAppViewTab(it) }


}

@Composable
fun MainAppViewView(uiState: AppViewUiState, onSelectTab: (AppViewTab) -> Unit) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()
  ) {
    if (!uiState.isLoading) {
      uiState.app?.let { AppViewContent(uiState = uiState, app = it, onSelectTab = onSelectTab) }
    }
  }
}

@Composable
fun AppViewContent(uiState: AppViewUiState, app: App, onSelectTab: (AppViewTab) -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {
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
        .padding(bottom = 8.dp)
    )
    Box(modifier = Modifier.padding(start = 16.dp, top = 19.dp, bottom = 19.dp, end = 16.dp)) {
      Column {
        AppPresentationView(app)
        AppStatsView(app)
        InstallButton(app)
        AppInfoViewPager(app, uiState.selectedTab, uiState.tabsList, onSelectTab)
      }
    }
  }
}

@Composable
fun AppInfoViewPager(
  app: App,
  selectedTab: AppViewTab,
  tabsList: List<AppViewTab>, onSelectTab: (AppViewTab) -> Unit
) {
//Viewpager not implemented yet as it does not exist on jetpack compose
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 40.dp)
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
            modifier = Modifier.padding(12.dp)
          )
        }
      }
    }
  }

  when (selectedTab) {
    AppViewTab.DETAILS -> {
      DetailsView(app)
    }
    AppViewTab.REVIEWS -> {
      TODO()
    }
    AppViewTab.NFT -> {
      TODO()
    }
    AppViewTab.RELATED -> {
      TODO()
    }
    AppViewTab.VERSIONS -> {
      TODO()
    }
    AppViewTab.INFO -> {
      InfoView(app)
    }
  }

}

@Composable
fun InfoView(app: App) {
  Column(modifier = Modifier.padding(top = 25.dp)) {
    StoreCard(app)
    AppInfoRow(infoCategory = "Package name", infoContent = app.packageName)
    app.releaseDate?.let { AppInfoRow(infoCategory = "Release", infoContent = it) }
    app.updateDate?.let { AppInfoRow(infoCategory = "Update on", infoContent = it) }
    AppInfoRow(infoCategory = "Downloads", infoContent = "" + app.downloads)
    AppInfoRow(infoCategory = "Download size", infoContent = app.appSize.toString())
    AppInfoRowWithButton(infoCategory = "Website", buttonText = app.website)
    AppInfoRowWithButton(infoCategory = "Email", buttonText = app.email)
    app.privacyPolicy?.let {
      AppInfoRowWithButton(
        infoCategory = "Privacy Policy",
        buttonText = it
      )
    }
    AppInfoRowWithButton(infoCategory = "Permissions", buttonText = app.permissions.toString())
  }
}

@Composable
fun AppInfoRowWithButton(infoCategory: String, buttonText: String) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 12.dp)
  ) {
    Text(infoCategory, modifier = Modifier.align(Alignment.TopStart))
    Text("MORE", modifier = Modifier.align(Alignment.TopEnd), color = Color(0xFFFE6446))
  }
}


@Composable
fun AppInfoRow(infoCategory: String, infoContent: String) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 12.dp)
  ) {
    Text(infoCategory, modifier = Modifier.align(Alignment.TopStart))
    Text(infoContent, modifier = Modifier.align(Alignment.TopEnd))
  }
}

@Composable
fun StoreCard(app: App) {
  Card(
    modifier = Modifier
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
        Text(text = "App available in", modifier = Modifier.padding(bottom = 12.dp))
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
            Text(text = app.store.storeName, modifier = Modifier.padding(bottom = 4.dp))
            Text(text = "" + app.store.apps + " Apps", modifier = Modifier.padding(bottom = 2.dp))
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
fun DetailsView(app: App) {
  Column(modifier = Modifier.padding(top = 16.dp)) {
    ScreenshotsList(app)
    Text(text = app.description, modifier = Modifier.padding(top = 18.dp))
  }

}

@Composable
fun ScreenshotsList(app: App) {
  LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    items(app.screenshots) { screenshot ->
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
  Button(
    onClick = { /*TODO*/ },
    shape = CircleShape,
    modifier = Modifier
      .height(56.dp)
      .fillMaxWidth()
  ) {
    Text("INSTALL", maxLines = 1)
  }
}

@Composable
fun AppStatsView(app: App) {
  Row(
    horizontalArrangement = Arrangement.SpaceEvenly,
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 20.dp)
  ) {
    Column(modifier = Modifier.padding(end = 40.dp, start = 22.dp)) {
      Text(text = "" + app.downloads)
      Text(text = "Downloads")
    }

    Column(modifier = Modifier.padding(end = 40.dp)) {
      Text(text = "" + app.versionName)
      Text(text = "Last Version")
    }

    Column(modifier = Modifier.padding(end = 26.dp)) {
      Text(text = "" + app.rating)
      Text(text = "Rating")
    }
  }
}

@Composable
fun AppPresentationView(app: App) {
  Row(
    modifier = Modifier
      .height(88.dp)
      .padding(bottom = 24.dp)
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
        fontSize = MaterialTheme.typography.subtitle2.fontSize,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
          .padding(top = 12.dp)
      )
      if (app.malware == "TRUSTED") {
        Row {
          /*  Image(
          painter = painterResource(id = R.drawable.ic_trusted_app),
          contentDescription = "Trusted icon",
          modifier = Modifier
            .size(10.dp, 13.dp)
            .wrapContentHeight(Alignment.CenterVertically)
        )*/
          Text(
            text = "Trusted",
            color = Color.Green,
            modifier = Modifier.padding(start = 8.dp),
            fontSize = MaterialTheme.typography.caption.fontSize
          )
        }
      }
    }

  }
}

