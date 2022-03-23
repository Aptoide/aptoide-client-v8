package cm.aptoide.pt.feature_updates.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.installedapps.domain.model.InstalledApp
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation

@Composable
@Preview
fun UpdatesScreen(updatesViewModel: UpdatesViewModel = hiltViewModel()) {

  val uiState by updatesViewModel.uiState.collectAsState()

  installedAppsList(uiState.installedAppsList)
}

@Composable
fun installedAppsList(installedAppsList: List<InstalledApp>) {
  LazyColumn(
    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    items(installedAppsList) { installedApp ->
      InstalledAppItem(installedApp)
    }
  }
}

@Composable
fun InstalledAppItem(installedApp: InstalledApp) {
  Row(modifier = Modifier.height(64.dp), verticalAlignment = CenterVertically) {
    Image(
      painter = rememberImagePainter(installedApp.appIcon,
        builder = {
          transformations(RoundedCornersTransformation(16f))
        }), contentDescription = "App icon",
      modifier = Modifier
        .size(64.dp, 64.dp)
        .padding(end = 8.dp)
    )
    Column(
      modifier = Modifier.wrapContentHeight(CenterVertically),
    ) {
      Text(
        modifier = Modifier.padding(start = 8.dp),
        text = installedApp.appName,
        fontSize = MaterialTheme.typography.body1.fontSize
      )
      Text(
        modifier = Modifier.padding(start = 8.dp),
        text = "Version " + installedApp.versionCode,
        fontSize = MaterialTheme.typography.caption.fontSize
      )
    }
  }
}
