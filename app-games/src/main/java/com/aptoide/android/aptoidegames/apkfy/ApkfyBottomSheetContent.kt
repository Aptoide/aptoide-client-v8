package com.aptoide.android.aptoidegames.apkfy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.BottomSheetContent
import com.aptoide.android.aptoidegames.BottomSheetHeader
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.OverrideAnalyticsAPKFY
import com.aptoide.android.aptoidegames.appview.buildAppViewRouteByAppId
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppItem
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

class ApkfyBottomSheetContent(private val app: App) : BottomSheetContent {
  @Composable override fun Draw(
    dismiss: () -> Unit,
    navigate: (String) -> Unit,
  ) {
    OverrideAnalyticsAPKFY(navigate) { navigateTo ->
      Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 50.dp)
      ) {
        BottomSheetHeader()
        Text(
          modifier = Modifier.padding(top = 11.dp, bottom = 22.dp),
          text = stringResource(id = R.string.apkfy_install_title),
          color = Palette.White,
          style = AGTypography.Title
        )
        AppItem(
          app = app,
          onClick = {
            navigateTo(
              buildAppViewRouteByAppId(appId = app.id, useStoreName = false)
            )
            dismiss()
          }
        ) {
          InstallViewShort(app)
        }
      }
    }
  }
}
