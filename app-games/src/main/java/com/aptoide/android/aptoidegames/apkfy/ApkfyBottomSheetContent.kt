package com.aptoide.android.aptoidegames.apkfy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.BottomSheetContent
import com.aptoide.android.aptoidegames.BottomSheetHeader
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.OverrideAnalyticsAPKFY
import com.aptoide.android.aptoidegames.apkfy.presentation.ApkfyUiState
import com.aptoide.android.aptoidegames.apkfy.presentation.rememberApkfyAnalytics
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppItem
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

class ApkfyBottomSheetContent(private val apkfyState: ApkfyUiState) : BottomSheetContent {
  @Composable override fun Draw(
    dismiss: () -> Unit,
    navigate: (String) -> Unit,
  ) {
    val apkfyAnalytics = rememberApkfyAnalytics()
    val app = apkfyState.app

    LaunchedEffect(Unit) {
      if (apkfyState is ApkfyUiState.Default) {
        apkfyAnalytics.sendApkfyTimeout()
      } else {
        apkfyAnalytics.sendApkfyShown()
      }
    }

    OverrideAnalyticsAPKFY(navigate) { navigateTo ->
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
      ) {
        BottomSheetHeader()
        Text(
          modifier = Modifier
            .padding(top = 11.dp, bottom = 6.dp)
            .fillMaxWidth(),
          text = stringResource(id = R.string.apkfy_install_title),
          color = Palette.White,
          style = AGTypography.Title
        )
        AppItem(
          app = app,
          onClick = {
            navigateTo(buildAppViewRoute(app))
            dismiss()
          }
        ) {
          InstallViewShort(app)
        }
        Spacer(modifier = Modifier.height(48.dp))
      }
    }
  }
}

@PreviewDark
@Composable
fun ApkfyBottomSheetPreview() {
  AptoideTheme {
    Column {
      Spacer(Modifier.weight(1f))
      ApkfyBottomSheetContent(apkfyState = ApkfyUiState.Default(randomApp)).Draw({}, { })
    }
  }
}
