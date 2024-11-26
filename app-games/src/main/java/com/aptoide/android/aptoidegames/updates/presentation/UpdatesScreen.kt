package com.aptoide.android.aptoidegames.updates.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.navDeepLink
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_updates.di.rememberAutoUpdate
import cm.aptoide.pt.feature_updates.presentation.UpdatesUiState
import cm.aptoide.pt.feature_updates.presentation.rememberCurrentUpdates
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withItemPosition
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.design_system.AptoideGamesSwitch
import com.aptoide.android.aptoidegames.drawables.icons.getBolt
import com.aptoide.android.aptoidegames.drawables.icons.getNoUpdates
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppItemUpdates
import com.aptoide.android.aptoidegames.home.LoadingView
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

const val updatesRoute = "updates"

fun updatesScreen() = ScreenData.withAnalytics(
  route = updatesRoute,
  screenAnalyticsName = "Updates",
  deepLinks = listOf(navDeepLink { uriPattern = BuildConfig.DEEP_LINK_SCHEMA + updatesRoute })
) { _, navigate, _ ->
  val updatesUiState = rememberCurrentUpdates()

  UpdatesScreen(
    updatesUiState = updatesUiState,
    navigate = navigate
  )
}

fun buildUpdatesDeepLinkUri() =
  BuildConfig.DEEP_LINK_SCHEMA + updatesRoute

@Composable
fun UpdatesScreen(
  updatesUiState: UpdatesUiState,
  navigate: (String) -> Unit,
) {
  when (updatesUiState) {
    is UpdatesUiState.Empty -> NoUpdatesScreen()
    is UpdatesUiState.Loading -> LoadingView()
    is UpdatesUiState.Idle -> AppsList(updatesUiState.updatesList, navigate)
  }
}

@Composable
fun NoUpdatesScreen() {
  val (autoUpdateGames, toggleAutoUpdate) = rememberAutoUpdate()
  val showAutoUpdateToggle = remember { mutableStateOf(false) }
  LaunchedEffect(key1 = autoUpdateGames) {
    if (!autoUpdateGames) showAutoUpdateToggle.value = true
  }

  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    if (showAutoUpdateToggle.value) {
      NoUpdatesTopSection(40)
      NoUpdatesViewWithAutoUpdateOff(autoUpdateGames, toggleAutoUpdate)
    } else {
      NoUpdatesTopSection(88)
    }
  }
}

@Composable
private fun NoUpdatesTopSection(imageBottomPadding: Int) {
  Image(
    modifier = Modifier
      .padding(bottom = imageBottomPadding.dp, start = 16.dp, end = 16.dp)
      .fillMaxWidth(),
    imageVector = getNoUpdates(Palette.Primary, Palette.White, Palette.GreyLight),
    contentDescription = null,
  )
  Text(
    modifier = Modifier.padding(horizontal = 40.dp),
    text = stringResource(R.string.update_up_to_date_title),
    style = AGTypography.Title,
    color = Palette.White,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis,
    textAlign = TextAlign.Center,
  )
  Text(
    modifier = Modifier.padding(horizontal = 40.dp),
    text = stringResource(R.string.update_up_to_date_body),
    style = AGTypography.Title,
    color = Palette.White,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis,
    textAlign = TextAlign.Center,
  )
}

@Composable
private fun NoUpdatesViewWithAutoUpdateOff(
  autoUpdateGames: Boolean,
  toggleAutoUpdate: (Boolean) -> Unit
) {
  Divider(
    modifier = Modifier.padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 24.dp),
    color = Palette.GreyDark
  )
  Text(
    modifier = Modifier.padding(horizontal = 24.dp),
    text = stringResource(R.string.update_auto_update_title),
    style = AGTypography.InputsM,
    color = Palette.White,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis
  )
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .toggleable(
        value = autoUpdateGames,
        role = Role.Switch,
        onValueChange = toggleAutoUpdate
      )
      .fillMaxWidth()
      .padding(start = 24.dp, end = 24.dp, top = 8.dp)
      .minimumInteractiveComponentSize()
  ) {
    AptoideGamesSwitch(
      checked = autoUpdateGames,
      onCheckedChanged = toggleAutoUpdate
    )
    Text(
      text = stringResource(R.string.update_auto_update_slider),
      modifier = Modifier.padding(start = 8.dp),
      style = AGTypography.BodyBold,
      color = Palette.GreyLight
    )
  }
}

@Composable
private fun AppsList(
  appList: List<App>,
  navigate: (String) -> Unit,
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    UpdateBox(updates = appList)
    LazyColumn(
      modifier = Modifier
        .semantics { collectionInfo = CollectionInfo(appList.size, 1) }
        .padding(start = 16.dp, end = 16.dp)
        .wrapContentSize(Alignment.TopCenter),
      contentPadding = PaddingValues(bottom = 72.dp)
    ) {
      itemsIndexed(appList) { index, app ->
        AppItemUpdates(
          app = app,
          onClick = {
            navigate(
              buildAppViewRoute(app).withItemPosition(index)
            )
          },
        ) {
          InstallViewShort(app)
        }
      }
    }
  }
}

@Composable
fun UpdateBox(updates: List<App>) {
  val text =
    if (updates.size == 1 && updates.first().packageName == BuildConfig.APPLICATION_ID) {
      stringResource(R.string.update_aptoide_games_update_notification)
    } else {
      pluralStringResource(
        R.plurals.update_games_can_be_updated_body,
        updates.size,
        updates.size
      )
    }
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
      .background(Palette.GreyDark)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Image(
        imageVector = getBolt(Palette.Primary),
        contentDescription = null
      )
      Text(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 8.dp),
        text = text,
        color = Palette.White,
        style = AGTypography.BodyBold,
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
fun PreviewUpdateBox() {
  UpdateBox(List((0..5).random()) { randomApp })
}

@Preview(showBackground = false)
@Composable
fun PreviewAppsList() {
  AppsList(
    appList = List((0..5).random()) { randomApp },
    navigate = { /* Preview navigation action */ }
  )
}
