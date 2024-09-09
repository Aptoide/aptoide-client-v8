package com.aptoide.android.aptoidegames.appview.permissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_apps.presentation.AppUiState
import cm.aptoide.pt.feature_apps.presentation.AppUiStateProvider
import cm.aptoide.pt.feature_apps.presentation.rememberApp
import com.aptoide.android.aptoidegames.AppIconImage
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.design_system.IndeterminateCircularLoading
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar

private const val SOURCE = "source"

const val appPermissionsRoute = "appInfoPermissions/{$SOURCE}"

fun appPermissionsScreen() = ScreenData.withAnalytics(
  route = appPermissionsRoute,
  screenAnalyticsName = "AppView"
) { arguments, _, navigateBack ->
  val source = arguments?.getString(SOURCE)!!

  AppInfoPermissionsView(
    navigateBack = navigateBack,
    source = source
  )
}

fun buildAppPermissionsRoute(source: String): String = "appInfoPermissions/$source"

@Composable
fun AppInfoPermissionsView(
  navigateBack: () -> Unit,
  source: String,
) {
  val (uiState, _) = rememberApp(source = source)

  (uiState as? AppUiState.Idle)?.app?.run {
    AppInfoPermissionsViewContent(
      navigateBack = navigateBack,
      uiState = uiState
    )
  }
}

@Composable
fun AppInfoPermissionsViewContent(
  navigateBack: () -> Unit,
  uiState: AppUiState,
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    AppGamesTopBar(
      navigateBack = navigateBack,
      title = stringResource(R.string.appview_info_permissions_title)
    )

    when (uiState) {
      is AppUiState.Idle -> uiState.app.run {
        AppPresentationRow(icon, name, developerName)
        permissions?.let { PermissionsList(it) }
      }

      else -> Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        IndeterminateCircularLoading(color = Palette.Primary)
      }
    }
  }
}

@Composable
fun AppPresentationRow(
  appIcon: String,
  appName: String,
  appDeveloper: String?,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(all = 16.dp)
  ) {
    AppIconImage(
      modifier = Modifier
        .padding(end = 16.dp)
        .size(88.dp),
      data = appIcon,
      contentDescription = "App icon",
    )
    Column(
      horizontalAlignment = Alignment.Start,
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = appName,
        maxLines = 2,
        style = AGTypography.TitleGames,
        color = Palette.White,
        overflow = TextOverflow.Ellipsis,
      )
      appDeveloper?.let {
        Text(
          text = it,
          maxLines = 1,
          style = AGTypography.SmallGames,
          color = Palette.White,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }
  }
}

@Composable
fun PermissionsList(permissionsList: List<String>) {
  LazyColumn(
    modifier = Modifier.fillMaxWidth(),
    contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    items(permissionsList) {
      PermissionItem(it)
    }
  }
}

@Composable
fun PermissionItem(permission: String) {
  Text(
    text = permission.trim(),
    style = AGTypography.InputsM,
    color = Palette.GreyLight,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis
  )
}

@PreviewDark
@Composable
fun AppPermissionsViewPreview(
  @PreviewParameter(AppUiStateProvider::class) uiState: AppUiState,
) {
  AppInfoPermissionsViewContent(
    navigateBack = {},
    uiState = uiState
  )
}
