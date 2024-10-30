package com.aptoide.android.aptoidegames.updates

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.navDeepLink
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_updates.presentation.UpdatesUiState
import cm.aptoide.pt.feature_updates.presentation.rememberUpdates
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.drawables.icons.getNoUpdates
import com.aptoide.android.aptoidegames.home.LoadingView
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

const val updatesRoute = "updates"

fun updatesScreen() = ScreenData.withAnalytics(
  route = updatesRoute,
  screenAnalyticsName = "Updates",
  deepLinks = listOf(navDeepLink { uriPattern = BuildConfig.DEEP_LINK_SCHEMA + updatesRoute })
) { _, navigate, _ ->
  val updatesUiState = rememberUpdates()

  UpdatesScreen(
    updatesUiState = updatesUiState,
    navigate = navigate
  )
}

@Composable
fun UpdatesScreen(
  updatesUiState: UpdatesUiState,
  navigate: (String) -> Unit,
) {
  when (updatesUiState) {
    is UpdatesUiState.Empty -> NoUpdatesScreen()
    is UpdatesUiState.Loading -> LoadingView()
    is UpdatesUiState.Idle -> {} //TODO Implement
  }
}

@Composable
fun NoUpdatesScreen() {
  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Image(
      modifier = Modifier
        .padding(bottom = 88.dp, start = 16.dp, end = 16.dp)
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
}
