package com.aptoide.android.aptoidegames.apkfy

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apps.data.isInCatappult
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.BottomSheetContent
import com.aptoide.android.aptoidegames.BottomSheetHeader
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.OverrideAnalyticsAPKFY
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGenericAnalytics
import com.aptoide.android.aptoidegames.apkfy.presentation.ApkfyUiState
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.drawables.icons.getAGIcon
import com.aptoide.android.aptoidegames.drawables.icons.getArrowDown
import com.aptoide.android.aptoidegames.drawables.icons.getArrowUp
import com.aptoide.android.aptoidegames.drawables.icons.getInfo
import com.aptoide.android.aptoidegames.drawables.icons.getTrustedIcon
import com.aptoide.android.aptoidegames.drawables.icons.getVerifiedIcon
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
    val analytics = rememberGenericAnalytics()
    val app = apkfyState.app

    LaunchedEffect(Unit) {
      if (apkfyState is ApkfyUiState.Default) {
        analytics.sendApkfyTimeout()
      } else {
        analytics.sendApkfyShown()
      }
    }

    OverrideAnalyticsAPKFY(navigate) { navigateTo ->
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
      ) {
        BottomSheetHeader()
        if (apkfyState is ApkfyUiState.VariantC) {
          InfoTextC()
        }
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
        if (apkfyState is ApkfyUiState.VariantD) {
          InfoTextD()
        }
        Spacer(modifier = Modifier.height(24.dp))
        Badge(apkfyState)
      }
    }
  }
}

@Composable
fun ColumnScope.InfoTextC() {
  InfoText(true)
  Divider(modifier = Modifier.padding(top = 24.dp, bottom = 13.dp))
}

@Composable
fun ColumnScope.InfoTextD() {
  var isTextVisible by remember { mutableStateOf(false) }
  Divider(modifier = Modifier.padding(bottom = 8.dp))
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { isTextVisible = !isTextVisible },
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row {
      Image(
        modifier = Modifier.size(16.dp),
        imageVector = getInfo(Palette.GreyLight),
        contentDescription = null
      )
      Text(
        modifier = Modifier.padding(start = 8.dp),
        text = stringResource(R.string.see_more_button),
        style = AGTypography.InputsS,
        color = Palette.GreyLight
      )
    }
    Image(
      modifier = Modifier.size(16.dp),
      imageVector = if (isTextVisible) getArrowUp(Palette.GreyLight) else getArrowDown(color = Palette.GreyLight),
      contentDescription = null,
    )
  }
  InfoText(isTextVisible, Modifier.padding(top = 24.dp))
}

@Composable
fun InfoText(visible: Boolean, modifier: Modifier = Modifier) {
  AnimatedVisibility(
    visible = visible,
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = modifier) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Image(
          modifier = Modifier.size(48.dp),
          imageVector = getAGIcon(Palette.Primary, Palette.Black),
          contentDescription = null
        )
        Text(
          text = stringResource(R.string.apkfy_install_body),
          style = AGTypography.InputsM,
          color = Palette.White,
          modifier = Modifier.padding(start = 8.dp),
        )
      }
      Text(
        text = stringResource(R.string.apkfy_install_bullet_points),
        style = AGTypography.Body,
        color = Palette.White,
        modifier = Modifier
          .padding(top = 8.dp, start = 56.dp)
          .fillMaxWidth()
      )
    }
  }
}

@Composable
fun Badge(apkfyState: ApkfyUiState) {
  val state = apkfyState !is ApkfyUiState.Default && apkfyState !is ApkfyUiState.VariantA
  when {
    state && (apkfyState.app.isInCatappult() == true) -> Row(
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

    state && (apkfyState.app.malware?.lowercase() == "trusted") -> Row(
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
      ApkfyBottomSheetContent(apkfyState = ApkfyUiState.Default(randomApp)).Draw({}, { })
    }
  }
}
