package cm.aptoide.pt.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.appview.buildAppViewRoute
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.editorial.EditorialViewCard
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_apps.presentation.AppGraphicView
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_apps.presentation.AppsRowView
import cm.aptoide.pt.feature_apps.presentation.tagApps
import cm.aptoide.pt.feature_editorial.presentation.editorialsCardViewModel
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.Type
import cm.aptoide.pt.feature_home.presentation.BundlesViewUiState
import cm.aptoide.pt.feature_home.presentation.BundlesViewUiStateType.LOADING
import cm.aptoide.pt.theme.greyMedium
import java.util.Random

@Composable
fun BundlesView(
  viewState: BundlesViewUiState,
  navigate: (String) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .wrapContentSize(Alignment.TopCenter)
  ) {
    if (viewState.type == LOADING) {
      CircularProgressIndicator()
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
//        .verticalScroll(rememberScrollState())   Error: Nesting scrollable in the same direction layouts like LazyColumn and Column(Modifier.verticalScroll())
          .wrapContentSize(Alignment.TopCenter)
          .padding(start = 16.dp),
      ) {
        items(viewState.bundles) {
          when (it.type) {
            Type.APP_GRID -> AppsSimpleListView(
              title = it.title,
              tag = it.tag,
              onAppClick = { packageName ->
                navigate(
                  buildAppViewRoute(packageName)
                )
              }
            )

            Type.FEATURE_GRAPHIC -> AppsGraphicListView(
              title = it.title,
              tag = it.tag,
              bonusBanner = false,
              onAppClick = { packageName ->
                navigate(
                  buildAppViewRoute(packageName)
                )
              }
            )

            Type.ESKILLS -> AppsSimpleListView(
              title = it.title,
              tag = it.tag,
              onAppClick = { packageName ->
                navigate(
                  buildAppViewRoute(packageName)
                )
              }
            )

            Type.FEATURED_APPC -> AppsGraphicListView(
              title = it.title,
              tag = it.tag,
              bonusBanner = true,
              onAppClick = { packageName ->
                navigate(
                  buildAppViewRoute(packageName)
                )
              }
            )

            Type.EDITORIAL -> EditorialMetaView(
              title = it.title,
              tag = it.tag,
              navigate = navigate
            )

            else -> {}
          }
        }
      }
    }
  }
}

@Composable
fun AppsGraphicListView(
  title: String,
  tag: String,
  bonusBanner: Boolean,
  onAppClick: (String) -> Unit,
) {
  val (uiState, _) = tagApps(tag)
  if (uiState !is AppsListUiState.Empty) {
    Box(modifier = Modifier.padding(bottom = 24.dp)) {
//            if (it.type == Type.ESKILLS) {
//              Box(modifier = Modifier
//                .fillMaxWidth()
//                .padding(start = 16.dp)
//                .background(Color(0xFFFEF2D6))
//                .height(112.dp))
//            }
      Column {
        Text(
          text = title,
          style = AppTheme.typography.medium_M,
          modifier = Modifier.padding(bottom = 8.dp)
        )
        when (uiState) {
          is AppsListUiState.Idle -> LazyRow(
            modifier = Modifier
              .fillMaxWidth()
              .wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            items(uiState.apps) {
              AppGraphicView(
                app = it,
                bonusBanner = bonusBanner,
                onAppClick = onAppClick
              )
            }
          }

          AppsListUiState.Empty,
          AppsListUiState.Error,
          AppsListUiState.NoConnection,
          -> EmptyBundleView(height = 184.dp)

          AppsListUiState.Loading -> LoadingBundleView(height = 184.dp)
        }
      }
    }
  }
}

@Composable
fun AppsSimpleListView(
  title: String,
  tag: String,
  onAppClick: (String) -> Unit,
) {
  val (uiState, _) = tagApps(tag)
  if (uiState !is AppsListUiState.Empty) {
    Box(modifier = Modifier.padding(bottom = 24.dp)) {
//            if (it.type == Type.ESKILLS) {
//              Box(modifier = Modifier
//                .fillMaxWidth()
//                .padding(start = 16.dp)
//                .background(Color(0xFFFEF2D6))
//                .height(112.dp))
//            }
      Column {
        Text(
          text = title,
          style = AppTheme.typography.medium_M,
          modifier = Modifier.padding(bottom = 8.dp)
        )
        when (uiState) {
          is AppsListUiState.Idle -> AppsRowView(
            appsList = uiState.apps,
            onAppClick = onAppClick
          )

          AppsListUiState.Empty,
          AppsListUiState.Error,
          AppsListUiState.NoConnection,
          -> EmptyBundleView(height = 184.dp)

          AppsListUiState.Loading -> LoadingBundleView(height = 184.dp)
        }
      }
    }
  }
}

@Composable
fun EditorialMetaView(
  title: String,
  tag: String,
  navigate: (String) -> Unit,
) {
  val editorialsCardViewModel = editorialsCardViewModel(tag = tag)
  val uiState by editorialsCardViewModel.uiState.collectAsState()
  val items = uiState

  if (items == null) {
    LoadingBundleView(height = 240.dp)
  } else {
    items.firstOrNull()
      ?.let { editorial ->
        Box(modifier = Modifier.padding(bottom = 24.dp)) {
//            if (it.type == Type.ESKILLS) {
//              Box(modifier = Modifier
//                .fillMaxWidth()
//                .padding(start = 16.dp)
//                .background(Color(0xFFFEF2D6))
//                .height(112.dp))
//            }
          Column {
            Text(
              text = title,
              style = AppTheme.typography.medium_M,
              modifier = Modifier.padding(bottom = 8.dp)
            )
            EditorialViewCard(
              articleId = editorial.id,
              title = editorial.title,
              image = editorial.image,
              label = editorial.caption.uppercase(),
              summary = editorial.summary,
              date = editorial.date,
              views = editorial.views,
              navigate = navigate,
            )
          }
        }
      } ?: EmptyBundleView(height = 240.dp)
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
    BundlesViewUiState(
      listOf(
        createFakeBundle(),
        createFakeBundle(),
        createFakeBundle(),
        createFakeBundle(),
        createFakeBundle()
      ),
      LOADING
    ),
    navigate = { }
  )
}

fun createFakeBundle(): Bundle {
  val appsList: MutableList<App> = ArrayList()
  for (i in 0..9) {
    appsList.add(randomApp)
  }
  val pick: Int = Random().nextInt(Type.values().size)
  return Bundle(
    title = "Widget title",
    actions = emptyList(),
    type = Type.values()[pick],
    tag = ""
  )
}
