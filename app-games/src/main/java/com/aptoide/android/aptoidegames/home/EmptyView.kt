package com.aptoide.android.aptoidegames.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.getRandomString
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun EmptyView(
  text: String,
  subtext: String? = null,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .semantics(mergeDescendants = true) { },
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Image(
      modifier = Modifier.padding(top = 80.dp, bottom = 88.dp, start = 16.dp, end = 16.dp),
      imageVector = AppTheme.icons.GenericError,
      contentDescription = null,
    )
    Text(
      modifier = Modifier.padding(horizontal = 40.dp),
      text = text,
      style = AGTypography.Title,
      color = Palette.White,
      maxLines = 4,
      overflow = TextOverflow.Ellipsis,
      textAlign = TextAlign.Center,
    )
    subtext?.let {
      Text(
        modifier = Modifier.padding(horizontal = 40.dp),
        text = subtext,
        style = AGTypography.DescriptionGames,
        color = Palette.White,
        textAlign = TextAlign.Center,
      )
    }
  }
}

@PreviewDark
@Composable
fun EmptyViewPreview() {
  AptoideTheme {
    EmptyView(
      text = stringResource(R.string.search_empty_body, getRandomString(1..3)),
    )
  }
}
