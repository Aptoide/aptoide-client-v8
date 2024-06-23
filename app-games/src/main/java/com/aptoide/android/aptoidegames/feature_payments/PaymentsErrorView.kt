package com.aptoide.android.aptoidegames.feature_payments

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewLandscapeDark
import com.aptoide.android.aptoidegames.SupportActivity
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun PaymentsErrorView(onFinish: (Boolean) -> Unit) {
  val context = LocalContext.current
  val onContactUsClick = { SupportActivity.openForSupport(context) }
  val onOutsideClick = { onFinish(false) }

  val configuration = LocalConfiguration.current
  AppGamesPaymentBottomSheet(
    onOutsideClick = onOutsideClick,
  ) {
    when (configuration.orientation) {
      Configuration.ORIENTATION_LANDSCAPE -> {
        LandscapePaymentErrorView(
          onRetryClick = onOutsideClick,
          onContactUsClick = onContactUsClick
        )
      }

      else -> {
        PortraitPaymentErrorView(
          onRetryClick = onOutsideClick,
          onContactUsClick = onContactUsClick
        )
      }
    }
  }
}

@Composable
fun LoadingView() {
  Column(
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .fillMaxWidth()
      .defaultMinSize(minHeight = 360.dp)
  ) {
    CircularProgressIndicator(
      modifier = Modifier
        .padding(bottom = 16.dp)
        .size(64.dp, 64.dp),
      backgroundColor = Color.Transparent,
      color = Palette.Primary,
      strokeWidth = 8.dp
    )
  }
}

@PreviewLandscapeDark
@Composable
private fun ShowPaymentsListNoConnectionPreviewLandscape(
) {
  AptoideTheme {
    PaymentsErrorView(onFinish = {})
  }
}

@PreviewLandscapeDark
@Composable
private fun LandscapePaymentViewPreview() {
  AptoideTheme {
    PaymentsErrorView(onFinish = {})
  }
}
