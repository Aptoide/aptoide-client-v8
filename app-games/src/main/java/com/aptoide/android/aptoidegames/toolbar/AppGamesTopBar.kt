package com.aptoide.android.aptoidegames.toolbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun AppGamesTopBar(
  navigateBack: () -> Unit,
  title: String?,
) {
  title?.let {
    TopBar(navigateBack, it)
  }
}

@Composable
private fun TopBar(
  navigateBack: () -> Unit,
  title: String,
) {
  Row(
    Modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .defaultMinSize(minHeight = 56.dp)
      .background(color = Color.Transparent)
      .padding(horizontal = 16.dp),
    horizontalArrangement = Arrangement.Start,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Image(
      imageVector = AppTheme.icons.LeftArrow,
      contentDescription = stringResource(id = R.string.button_back_title),
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .clickable(onClick = navigateBack)
        .size(32.dp)
    )
    Text(
      text = title,
      color = Palette.White,
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      textAlign = TextAlign.Center,
      overflow = TextOverflow.Ellipsis,
      maxLines = 1,
      style = AppTheme.typography.title
    )
    Spacer(modifier = Modifier.width(32.dp))
  }
}

@PreviewDark
@Composable
fun AppGamesTopBarPreview() {
  AptoideTheme {
    AppGamesTopBar(
      navigateBack = {},
      title = "Settings"
    )
  }
}
