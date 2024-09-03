package com.aptoide.android.aptoidegames.feature_payments

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.PreviewLandscapeDark
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.getToolBarLogo

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppGamesPaymentBottomSheet(
  modifier: Modifier = Modifier,
  onOutsideClick: () -> Unit = {},
  onClick: () -> Unit = {},
  background: @Composable BoxScope.() -> Unit = {},
  content: @Composable () -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Palette.Black.copy(alpha = 0.60f))
      .semantics { invisibleToUser() }
      .clickable(
        // remove ripple effect
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onOutsideClick
      )
  ) {
    when (LocalConfiguration.current.orientation) {
      Configuration.ORIENTATION_LANDSCAPE,
      -> {
        DTPaymentBottomSheetLandscape(
          modifier = modifier,
          background = background,
          content = content,
          onClick = onClick
        )
      }

      else -> {
        DTPaymentBottomSheetPortrait(
          modifier = modifier,
          background = background,
          content = content,
          onClick = onClick
        )
      }

    }
  }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun DTPaymentBottomSheetPortrait(
  modifier: Modifier = Modifier,
  onClick: () -> Unit = {},
  background: @Composable BoxScope.() -> Unit = {},
  content: @Composable () -> Unit,
) {
  Box(modifier = Modifier.fillMaxSize()) {
    Surface(
      modifier = modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .padding(top = 40.dp)
        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
        .semantics { this.invisibleToUser() }
        .clickable(// remove ripple effect
          interactionSource = remember { MutableInteractionSource() },
          indication = null,
          onClick = onClick
        )
    ) {
      Box(
        modifier = Modifier.background(color = Palette.White)
      ) {
        background()
        Column {
          PaymentLogo()
          content()
        }
      }
    }
  }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun DTPaymentBottomSheetLandscape(
  modifier: Modifier = Modifier,
  onClick: () -> Unit = {},
  background: @Composable BoxScope.() -> Unit = {},
  content: @Composable () -> Unit,
) {
  Box(modifier = modifier.fillMaxSize()) {
    Surface(
      modifier = modifier
        .padding(top = 8.dp, start = 72.dp, end = 72.dp)
        .fillMaxWidth()
        .height(320.dp)
        .align(Alignment.BottomCenter)
        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
        .semantics { invisibleToUser() }
        .clickable(// remove ripple effect
          interactionSource = remember { MutableInteractionSource() },
          indication = null,
          onClick = onClick
        )
    ) {
      Box(
        modifier = Modifier.background(color = Palette.White)
      ) {
        background()
        Column {
          PaymentLogo()
          content()
        }
      }
    }
  }
}

@Composable
private fun PaymentLogo(modifier: Modifier = Modifier) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
  ) {
    Image(
      imageVector = BuildConfig.FLAVOR.getToolBarLogo(Palette.Black),
      contentDescription = null,
    )
  }
}

@PreviewDark
@Composable
private fun PaymentLogoPreview() {
  AptoideTheme {
    AppGamesPaymentBottomSheet {

    }
  }
}

@PreviewLandscapeDark
@Composable
private fun PaymentLogoLandscapePreview() {
  AptoideTheme {
    AppGamesPaymentBottomSheet {

    }
  }
}
