package com.aptoide.android.aptoidegames.feature_payments

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.PreviewLandscapeDark
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.design_system.IndeterminateCircularLoading
import com.aptoide.android.aptoidegames.drawables.backgrounds.getPaymentsProgressBackground
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun LoadingView(
  modifier: Modifier = Modifier,
  textMessage: Int? = null,
) {
  Box(
    modifier = Modifier
      .padding(16.dp)
      .fillMaxWidth(),
    contentAlignment = Alignment.Center,
    propagateMinConstraints = true
  ) {
    Image(
      imageVector = getPaymentsProgressBackground(Palette.Black, Palette.Primary),
      contentDescription = null,
    )
    Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier
        .fillMaxWidth()
        .defaultMinSize(minHeight = 360.dp)
    ) {
      IndeterminateCircularLoading(color = Palette.Black)
      textMessage?.let {
        Text(
          modifier = Modifier.padding(top = 8.dp),
          style = AGTypography.Title,
          text = stringResource(id = textMessage)
        )
      }
    }
  }
}

@PreviewDark
@Composable
fun LoadingViewPreview() {
  AppGamesPaymentBottomSheet {
    LoadingView(textMessage = R.string.purchase_making_purchase_title)
  }
}

@PreviewLandscapeDark
@Composable
fun LoadingLandscapeViewPreview() {
  AppGamesPaymentBottomSheet {
    LoadingView()
  }
}
