package cm.aptoide.pt.feature_apps.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.domain.Bundle
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation

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
      .verticalScroll(rememberScrollState())
      .wrapContentSize(Alignment.Center)
  ) {
    if (isLoading)
      CircularProgressIndicator()
    else
      bundles.forEach {
        Text(it.title)
        AppsList(it.appsList)
      }
  }
}

@Composable
fun AppsList(appsList: List<App>) {
  Row(modifier = Modifier
    .horizontalScroll(rememberScrollState())
    .wrapContentSize()) {
    appsList.forEach {
      Column(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.Center)) {
        Image(
          painter = rememberImagePainter(it.icon,
            builder = {
              transformations(RoundedCornersTransformation(16f))
            }),
          contentDescription = "App Icon",
          modifier = Modifier.size(80.dp),

          )
        Text(it.name)
      }

    }
  }
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
    appsList.add(App("app $i",
      "https://pool.img.aptoide.com/catappult/8c9974886cca4ae0169d260f441640ab_icon.jpg"))
  }
  return Bundle(title = "Widget title", appsList)
}
