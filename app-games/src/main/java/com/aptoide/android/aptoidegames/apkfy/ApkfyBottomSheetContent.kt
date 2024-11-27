package com.aptoide.android.aptoidegames.apkfy

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.isInCatappult
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.BottomSheetContent
import com.aptoide.android.aptoidegames.BottomSheetHeader
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.OverrideAnalyticsAPKFY
import com.aptoide.android.aptoidegames.apkfy.presentation.rememberBadgeVisibility
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.drawables.icons.getTrustedIcon
import com.aptoide.android.aptoidegames.drawables.icons.getVerifiedIcon
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppItem
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

class ApkfyBottomSheetContent(private val app: App) : BottomSheetContent {
  @Composable override fun Draw(
    dismiss: () -> Unit,
    navigate: (String) -> Unit,
  ) {
    OverrideAnalyticsAPKFY(navigate) { navigateTo ->
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
      ) {
        BottomSheetHeader()
        Text(
          modifier = Modifier
            .padding(top = 11.dp, bottom = 22.dp)
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
        Spacer(modifier = Modifier.height(24.dp))
        Badge(app)
      }
    }
  }
}

@Composable
fun Badge(app: App) {
  val state = rememberBadgeVisibility()
  when {
    state && (app.isInCatappult() == true) -> Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .background(color = Palette.Official)
        .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      Image(
        imageVector = getVerifiedIcon(Palette.White),
        contentDescription = null,
        modifier = Modifier.size(16.dp)
      )
      Text(
        text = stringResource(R.string.official_game_badge),
        style = AGTypography.SmallGames,
        color = Palette.White,
        modifier = Modifier.padding(start = 4.dp)
      )
    }

    state && (app.malware?.lowercase() == "trusted") -> Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .background(color = Palette.Trusted)
        .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      Image(
        imageVector = getTrustedIcon(Palette.White),
        contentDescription = null,
        modifier = Modifier.size(16.dp)
      )
      Text(
        text = stringResource(R.string.trusted_game_badge),
        style = AGTypography.SmallGames,
        color = Palette.White,
        modifier = Modifier.padding(start = 4.dp)
      )
    }

    else -> Spacer(modifier = Modifier.height(24.dp))
  }
}

@PreviewDark
@Composable
fun ApkfyBottomSheetPreview() {
  AptoideTheme {
    Column {
      Spacer(Modifier.weight(1f))
      ApkfyBottomSheetContent(app = randomApp).Draw({}, { })
    }
  }
}
