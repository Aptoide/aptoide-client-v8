package com.aptoide.android.aptoidegames.appview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_apps.presentation.rememberSimilarApps
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppGridView
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

/**
 * The apps related to [app], as a row of cards at the end of the details. Nothing is shown
 * while they load or when there are none, so the details end where they always did.
 */
@Composable
fun RelatedAppsRow(
  app: App,
  navigate: (String) -> Unit,
) {
  val (uiState, _) = rememberSimilarApps(packageName = app.packageName)
  val apps = (uiState as? AppsListUiState.Idle)?.apps?.takeIf { it.isNotEmpty() } ?: return
  Column(modifier = Modifier.padding(bottom = 32.dp)) {
    Text(
      text = stringResource(R.string.appview_related_apps_title),
      style = AGTypography.InputsL,
      color = Palette.White,
      modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
    )
    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      items(apps, key = { it.packageName }) { related ->
        AppGridView(
          app = related,
          onClick = { navigate(buildAppViewRoute(related)) },
        )
      }
    }
  }
}
