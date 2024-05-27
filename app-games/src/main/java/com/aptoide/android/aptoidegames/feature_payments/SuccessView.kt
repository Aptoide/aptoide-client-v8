package com.aptoide.android.aptoidegames.feature_payments

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import com.aptoide.android.aptoidegames.theme.AGTypography
import getDoneIcon

@Composable
fun SuccessView() {
  RealSuccessView()
}

@Composable
private fun RealSuccessView() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .defaultMinSize(minHeight = 360.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Image(
      imageVector = getDoneIcon(),
      contentDescription = null,
      modifier = Modifier
        .padding(bottom = 16.dp)
        .size(64.dp, 64.dp)
    )
    Text(
      text = "Done!", // TODO hardcoded text
      style = AGTypography.SubHeadingS
    )
  }
}

@PreviewDark
@Composable
fun SuccessViewPreview() {
  SuccessView()
}
