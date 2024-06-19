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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.navArgument
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.presentation.AppUiState
import cm.aptoide.pt.feature_apps.presentation.AppUiStateProvider
import cm.aptoide.pt.feature_apps.presentation.appViewModel
import com.aptoide.android.aptoidegames.AppIconImage
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.appview.AD_LIST_ID_ARG_NAME
import com.aptoide.android.aptoidegames.appview.PACKAGE_NAME
import com.aptoide.android.aptoidegames.design_system.IndeterminateCircularLoading
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar

const val appPermissionsRoute = "appInfoPermissions/{$PACKAGE_NAME}?" +
  "$AD_LIST_ID_ARG_NAME={$AD_LIST_ID_ARG_NAME}"

fun appPermissionsScreen() = ScreenData.withAnalytics(
  route = appPermissionsRoute,
  screenAnalyticsName = "AppView",
  arguments = listOf(
    navArgument(AD_LIST_ID_ARG_NAME) { nullable = true },
  ),
) { arguments, _, navigateBack ->
  val packageName = arguments?.getString("packageName")!!
  val adListId = arguments.getString(AD_LIST_ID_ARG_NAME)

  AppInfoPermissionsView(
    navigateBack = navigateBack,
    packageName = packageName,
    adListId = adListId,
  )
}

fun buildAppPermissionsRoute(app: App): String =
  "appInfoPermissions/${app.packageName}?adListId=${app.campaigns?.adListId}"

@Composable
fun AppInfoPermissionsView(
  navigateBack: () -> Unit,
  packageName: String,
  adListId: String?,
) {
  val appViewModel = appViewModel(packageName = packageName, adListId = adListId)
  val uiState by appViewModel.uiState.collectAsState()

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
      title = "Permissions"
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
