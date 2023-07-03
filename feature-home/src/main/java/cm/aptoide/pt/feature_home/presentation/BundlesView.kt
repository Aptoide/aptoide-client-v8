package cm.aptoide.pt.feature_home.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.File
import cm.aptoide.pt.feature_apps.domain.Rating
import cm.aptoide.pt.feature_apps.domain.Store
import cm.aptoide.pt.feature_apps.domain.Votes
import cm.aptoide.pt.feature_apps.presentation.AppGraphicView
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_apps.presentation.AppsRowView
import cm.aptoide.pt.feature_apps.presentation.tagApps
import cm.aptoide.pt.feature_editorial.presentation.EditorialViewCard
import cm.aptoide.pt.feature_editorial.presentation.EditorialViewScreen
import cm.aptoide.pt.feature_editorial.presentation.editorialViewModel
import cm.aptoide.pt.feature_editorial.presentation.editorialsCardViewModel
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.Type
import cm.aptoide.pt.theme.greyMedium
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Suppress("UNUSED_PARAMETER")
@Composable
fun BundlesScreen(
  viewModel: BundlesViewModel,
  type: ScreenType,
  topBarContent: @Composable AnimatedVisibilityScope.() -> Unit,
) {
  val viewState by viewModel.uiState.collectAsState()

  val topAppBarState = rememberSaveable { (mutableStateOf(true)) }
  val navController = rememberNavController()
  Scaffold(
    topBar = {
      AnimatedVisibility(
        visible = topAppBarState.value,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
        content = topBarContent
      )
    }
  ) {
    NavigationGraph(
      navController = navController,
      isLoading = viewState.type == BundlesViewUiStateType.LOADING,
      bundles = viewState.bundles,
      topAppBarState = topAppBarState
    )
  }
}

@Composable
private fun BundlesView(
  isLoading: Boolean,
  bundles: List<Bundle>,
  nav: NavHostController,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .wrapContentSize(Alignment.TopCenter)
  ) {
    if (isLoading) {
      CircularProgressIndicator()
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
//        .verticalScroll(rememberScrollState())   Error: Nesting scrollable in the same direction layouts like LazyColumn and Column(Modifier.verticalScroll())
          .wrapContentSize(Alignment.TopCenter)
          .padding(start = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
      ) {
        items(bundles) {
          Box {
//            if (it.type == Type.ESKILLS) {
//              Box(modifier = Modifier
//                .fillMaxWidth()
//                .padding(start = 16.dp)
//                .background(Color(0xFFFEF2D6))
//                .height(112.dp))
//            }
            Column {
              Text(
                text = it.title,
                style = AppTheme.typography.medium_M,
                modifier = Modifier.padding(bottom = 8.dp)
              )
              when (it.type) {
                Type.APP_GRID -> AppsSimpleListView(it.tag)
                Type.FEATURE_GRAPHIC -> AppsGraphicListView(it.tag, false)
                Type.ESKILLS -> AppsSimpleListView(it.tag)
                Type.FEATURED_APPC -> AppsGraphicListView(it.tag, true)
                Type.EDITORIAL -> EditorialMetaView(requestUrl = it.view, nav = nav)
                else -> {}
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun AppsGraphicListView(
  tag: String,
  bonusBanner: Boolean,
) {
  val (uiState, _) = tagApps(tag)

  when (uiState) {
    is AppsListUiState.Idle -> LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      items(uiState.apps) {
        AppGraphicView(it, bonusBanner)
      }
    }

    AppsListUiState.Empty,
    AppsListUiState.Error,
    AppsListUiState.NoConnection,
    -> EmptyBundleView(height = 184.dp)

    AppsListUiState.Loading -> LoadingBundleView(height = 184.dp)
  }
}

@Composable
fun AppsSimpleListView(tag: String) {
  val (uiState, _) = tagApps(tag)

  when (uiState) {
    is AppsListUiState.Idle -> AppsRowView(uiState.apps)

    AppsListUiState.Empty,
    AppsListUiState.Error,
    AppsListUiState.NoConnection,
    -> EmptyBundleView(height = 184.dp)

    AppsListUiState.Loading -> LoadingBundleView(height = 184.dp)
  }
}

@Composable
fun EditorialMetaView(
  requestUrl: String?,
  nav: NavHostController,
) = requestUrl?.let {
  val editorialsCardViewModel = editorialsCardViewModel(requestUrl = it)
  val uiState by editorialsCardViewModel.uiState.collectAsState()
  val items = uiState

  if (items == null) {
    LoadingBundleView(height = 240.dp)
  } else {
    items.firstOrNull()
      ?.let { editorial ->
        EditorialViewCard(
          articleId = editorial.id,
          title = editorial.title,
          image = editorial.image,
          label = editorial.caption.uppercase(),
          summary = editorial.summary,
          date = editorial.date,
          views = editorial.views,
          navController = nav,
        )
      }
      ?: EmptyBundleView(height = 240.dp)
  }
}

@Composable
fun LoadingBundleView(height: Dp) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(height),
    contentAlignment = Alignment.Center,
  ) {
    CircularProgressIndicator()
  }
}

@Composable
fun EmptyBundleView(height: Dp) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .height(height),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Image(
      imageVector = Icons.Default.Search,
      contentDescription = null
    )
    Text(
      modifier = Modifier.padding(all = 24.dp),
      text = "Oops, there\\'s no content here yet!",
      style = AppTheme.typography.regular_XL,
      textAlign = TextAlign.Center,
      color = greyMedium,
    )
  }
}

@Preview
@Composable
internal fun AppsScreenPreview() {
  BundlesView(
    false,
    listOf(
      createFakeBundle(),
      createFakeBundle(),
      createFakeBundle(),
      createFakeBundle(),
      createFakeBundle()
    ),
    rememberNavController()
  )
}

fun createFakeBundle(): Bundle {
  val appsList: MutableList<App> = ArrayList()
  for (i in 0..9) {
    appsList.add(
      App(
        name = "app name $i app name 2",
        packageName = "packagename",
        md5 = "md5",
        appSize = 123,
        icon = "https://pool.img.aptoide.com/catappult/8c9974886cca4ae0169d260f441640ab_icon.jpg",
        malware = "trusted",
        rating = Rating(
          avgRating = 2.3,
          totalVotes = 12321,
          votes = listOf(
            Votes(1, 3),
            Votes(2, 8),
            Votes(3, 123),
            Votes(4, 100),
            Votes(5, 1994)
          )
        ),
        pRating = Rating(
          avgRating = 2.3,
          totalVotes = 12321,
          votes = listOf(
            Votes(1, 3),
            Votes(2, 8),
            Votes(3, 123),
            Votes(4, 100),
            Votes(5, 1994)
          )
        ),
        downloads = 11113,
        versionName = "alfa",
        versionCode = 123,
        featureGraphic = "https://pool.img.aptoide.com/catappult/934323636c0247af73ecfcafd46aefc3_feature_graphic.jpg",
        isAppCoins = true,
        screenshots = listOf("", ""),
        description = "app with the name 1 descpription",
        videos = listOf("", ""),
        store = Store(
          storeName = "rmota",
          icon = "rmota url",
          apps = 123,
          subscribers = 123123,
          downloads = 1312132314
        ),
        releaseDate = "18 of may",
        updateDate = "18 of may",
        website = "www.aptoide.com",
        email = "aptoide@aptoide.com",
        privacyPolicy = "none",
        permissions = listOf("Permission 1", "permission 2"),
        file = File(
          vername = "asdas",
          vercode = 123,
          md5 = "md5",
          filesize = 123,
          path = null,
          path_alt = null
        ),
        obb = null,
        developerName = null
      )
    )
  }
  val pick: Int = Random().nextInt(Type.values().size)
  return Bundle(
    title = "Widget title",
    actions = emptyList(),
    type = Type.values()[pick],
    tag = ""
  )
}

enum class ScreenType {
  APPS,
  GAMES,
  BONUS
}

@Composable
private fun NavigationGraph(
  navController: NavHostController,
  isLoading: Boolean,
  bundles: List<Bundle>,
  topAppBarState: MutableState<Boolean>,
) {
  NavHost(
    navController = navController,
    startDestination = "games"
  ) {
    composable("games") {
      topAppBarState.value = true
      BundlesView(isLoading, bundles, navController)
    }
    composable("editorial/{articleId}") {
      topAppBarState.value = false
      val viewModel = editorialViewModel(it.arguments?.getString("articleId")!!)
      EditorialViewScreen(viewModel)
    }
  }
}
