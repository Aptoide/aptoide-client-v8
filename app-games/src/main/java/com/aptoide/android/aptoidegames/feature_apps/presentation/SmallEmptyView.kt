package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun SmallEmptyView(
  modifier: Modifier = Modifier,
  padding: PaddingValues = PaddingValues(horizontal = 48.dp),
  title: String = stringResource(id = R.string.editorials_view_no_content_message),
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(padding),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Image(
      imageVector = AppTheme.icons.SingleGamepad,
      contentDescription = null,
      colorFilter = ColorFilter.tint(Palette.Grey)
    )
    Text(
      text = title,
      style = AppTheme.typography.subHeading_S,
      textAlign = TextAlign.Center,
      color = Palette.White,
    )
  }
}

@PreviewDark
@Composable
fun SmallEmptyViewPreview() {
  AptoideTheme {
    SmallEmptyView()
  }
}
