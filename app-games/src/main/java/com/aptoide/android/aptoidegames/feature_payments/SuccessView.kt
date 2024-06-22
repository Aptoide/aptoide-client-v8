package com.aptoide.android.aptoidegames.feature_payments

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.PreviewLandscapeDark
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.drawables.backgrounds.getPaymentsProgressBackground
import com.aptoide.android.aptoidegames.drawables.icons.getCheckBox
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun SuccessView() {
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
      horizontalAlignment = Alignment.CenterHorizontally
    ) {

      Image(
        imageVector = getCheckBox(Palette.Black),
        contentDescription = null,
        modifier = Modifier
          .padding(bottom = 16.dp)
          .size(88.dp, 88.dp)
      )
      Text(
        text = stringResource(R.string.done_title),
        style = AGTypography.Title,
        color = Palette.Black
      )
    }
  }
}

@PreviewDark
@Composable
fun SuccessViewPreview() {
  AppGamesPaymentBottomSheet {
    SuccessView()
  }
}

@PreviewLandscapeDark
@Composable
fun SuccessLandscapeViewPreview() {
  AppGamesPaymentBottomSheet {
    SuccessView()
  }
}
