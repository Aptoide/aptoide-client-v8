package cm.aptoide.pt.updates

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import cm.aptoide.pt.aptoide_ui.AptoideAsyncImage
import cm.aptoide.pt.aptoide_ui.animations.staticComposable
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.aptoide_ui.theme.AptoideTheme
import cm.aptoide.pt.aptoide_ui.toolbar.AptoideActionBar
import cm.aptoide.pt.feature_updates.domain.InstalledApp
import cm.aptoide.pt.feature_updates.presentation.UpdatesUiState
import cm.aptoide.pt.feature_updates.presentation.UpdatesViewModel

const val updatesRoute = "updates"

fun NavGraphBuilder.updatesScreen(
) = staticComposable(
  updatesRoute,
) {
  val updatesViewModel: UpdatesViewModel = hiltViewModel()
  val uiState by updatesViewModel.uiState.collectAsState()

  UpdatesScreen(uiState = uiState,
    onInstalledAppClick = { updatesViewModel.onOpenInstalledApp(it) },
    onInstalledAppLongClick = { updatesViewModel.onUninstallApp(it) }
  )
}

@Composable
fun UpdatesScreen(
  uiState: UpdatesUiState,
  onInstalledAppClick: (String) -> Unit,
  onInstalledAppLongClick: (String) -> Unit,
) {
  AptoideTheme {
    Scaffold(
      topBar = {
        AptoideActionBar()
      }
    ) {
      InstalledAppsList(
        uiState.installedAppsList,
        onInstalledAppClick = { onInstalledAppClick(it) },
        onInstalledAppLongClick = { onInstalledAppLongClick(it) }
      )
    }
  }
}

@Composable
fun InstalledAppsList(
  installedAppsList: List<InstalledApp>,
  onInstalledAppClick: (String) -> Unit,
  onInstalledAppLongClick: (String) -> Unit,
) {
  LazyColumn(
    modifier = Modifier.padding(bottom = 60.dp),
    contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    items(installedAppsList) { installedApp ->
      InstalledAppItem(
        installedApp,
        onInstalledAppClick = onInstalledAppClick,
        onInstalledAppLongClick = onInstalledAppLongClick
      )
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InstalledAppItem(
  installedApp: InstalledApp,
  onInstalledAppClick: (String) -> Unit,
  onInstalledAppLongClick: (String) -> Unit,
) {
  Row(
    modifier = Modifier
      .height(64.dp)
      .fillMaxWidth()
      .combinedClickable(
        onClick = { onInstalledAppClick(installedApp.packageName) },
        onLongClick = { onInstalledAppLongClick(installedApp.packageName) }
      ),
    verticalAlignment = CenterVertically
  ) {
    AptoideAsyncImage(
      data = installedApp.appIcon,
      contentDescription = "App icon",
      placeholder = ColorPainter(AppTheme.colors.placeholderColor),
      modifier = Modifier
        .size(64.dp)
        .clip(RoundedCornerShape(16.dp))
    )
    Column(
      modifier = Modifier
        .wrapContentHeight(CenterVertically)
        .padding(start = 16.dp)
        .fillMaxWidth(),
    ) {
      Text(
        text = installedApp.appName,
        fontSize = MaterialTheme.typography.body1.fontSize
      )
      Text(
        text = "Version " + installedApp.versionCode,
        fontSize = MaterialTheme.typography.caption.fontSize
      )
    }
  }
}
