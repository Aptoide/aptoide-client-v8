package cm.aptoide.pt.feature_apps.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.domain.Bundle
import cm.aptoide.pt.feature_apps.domain.Type
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import java.util.*

@Composable
fun AppsScreen(viewModel: BundlesViewModel) {
  val bundles: List<Bundle> by viewModel.bundlesList.collectAsState(initial = emptyList())
  val isLoading: Boolean by viewModel.isLoading
  BundlesScreen(isLoading, bundles)
}

@Composable
private fun BundlesScreen(
  isLoading: Boolean,
  bundles: List<Bundle>,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .wrapContentSize(Alignment.TopCenter)
  ) {
    if (isLoading)
      CircularProgressIndicator()
    else
      LazyColumn(modifier = Modifier
        .fillMaxSize()
//        .verticalScroll(rememberScrollState())   Error: Nesting scrollable in the same direction layouts like LazyColumn and Column(Modifier.verticalScroll())
        .wrapContentSize(Alignment.TopCenter)) {
        items(bundles) {
          when (it.type) {
            Type.APP_GRID -> AppsListBundleView(it)
            Type.FEATURE_GRAPHIC -> AppsFeatureGraphicListBundleView(it)
            Type.UNKNOWN_BUNDLE -> {}
          }
        }
      }
  }
}

@Composable
fun AppsListBundleView(bundle: Bundle) {
  Text(bundle.title)
  AppsListView(bundle.appsList)
}

@Composable
fun AppsFeatureGraphicListBundleView(bundle: Bundle) {
  Text(bundle.title)
  AppsGraphicListView(bundle.appsList)
}

@Composable
fun AppsListView(appsList: List<App>) {
  LazyRow(modifier = Modifier
    .fillMaxWidth()
    .wrapContentHeight(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
    items(appsList) {
      Column(modifier = Modifier
        .width(80.dp)
        .height(128.dp)
        .wrapContentSize(Alignment.Center)) {
        AppGridView(it)
      }
    }
  }
}

@Composable
fun AppsGraphicListView(appsList: List<App>) {
  LazyRow(modifier = Modifier
    .fillMaxWidth()
    .wrapContentHeight(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
    items(appsList) {
      Column(modifier = Modifier
        .width(280.dp)
        .height(184.dp)
        .wrapContentSize(Alignment.Center)) {
        AppGraphicView(it)
      }
    }
  }
}

@Composable
private fun AppGraphicView(app: App) {
  Image(
    painter = rememberImagePainter(app.featureGraphic,
      builder = {
        transformations(RoundedCornersTransformation(16f))
      }),
    contentDescription = "App Icon",
    modifier = Modifier
      .width(280.dp)
      .height(136.dp)
  )
  Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween) {
    Image(
      painter = rememberImagePainter(app.icon),
      contentDescription = "App Graphic",
      modifier = Modifier.size(40.dp)
    )
    Text(app.name, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier
      .height(42.dp))

    Button(onClick = { /*TODO*/ }, shape = CircleShape) {
      Text("INSTALL", maxLines = 1)
    }
  }
}

@Composable
private fun AppGridView(app: App) {
  Image(
    painter = rememberImagePainter(app.icon,
      builder = {
        transformations(RoundedCornersTransformation(16f))
      }),
    contentDescription = "App Icon",
    modifier = Modifier.size(80.dp),

    )
  Text(app.name, maxLines = 2, modifier = Modifier
    .height(42.dp))
}

@Preview
@Composable
internal fun AppsScreenPreview() {
  BundlesScreen(
    false,
    listOf(
      createFakeBundle(),
      createFakeBundle(),
      createFakeBundle(),
      createFakeBundle(),
      createFakeBundle()
    )
  )
}

fun createFakeBundle(): Bundle {
  val appsList: MutableList<App> = ArrayList()
  for (i in 0..9) {
    appsList.add(App(
      "app name $i app name 2",
      "https://pool.img.aptoide.com/catappult/8c9974886cca4ae0169d260f441640ab_icon.jpg",
      "https://pool.img.aptoide.com/catappult/934323636c0247af73ecfcafd46aefc3_feature_graphic.jpg"
    ))
  }
  val pick: Int = Random().nextInt(Type.values().size)
  return Bundle(title = "Widget title", appsList, Type.values()[pick])
}
