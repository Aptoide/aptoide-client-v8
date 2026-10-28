package com.aptoide.android.aptoidegames.apkfy.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apkfy.domain.UnavailableReason
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

/** Takes the install button's place for a title the catalog lists but cannot install here. */
@Composable
fun ApkfyUnavailableLabel(
  reason: UnavailableReason,
  modifier: Modifier = Modifier,
) {
  Text(
    modifier = modifier.fillMaxWidth(),
    text = stringResource(
      when (reason) {
        UnavailableReason.MIN_SDK -> R.string.apkfy_not_available_min_sdk
        UnavailableReason.NOT_INSTALLABLE_YET,
        UnavailableReason.NO_COMPATIBLE_SPLIT,
          -> R.string.apkfy_not_available_yet
      }
    ),
    style = AGTypography.InputsM,
    color = Palette.GreyLight,
    textAlign = TextAlign.Center,
  )
}

@PreviewDark
@Composable
private fun ApkfyUnavailableLabelPreview() {
  AptoideTheme {
    ApkfyUnavailableLabel(reason = UnavailableReason.NOT_INSTALLABLE_YET)
  }
}
