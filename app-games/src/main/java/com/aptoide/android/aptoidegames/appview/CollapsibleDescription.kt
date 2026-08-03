package com.aptoide.android.aptoidegames.appview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

private const val COLLAPSED_MAX_LINES = 5

/**
 * App-view description that collapses to [COLLAPSED_MAX_LINES] with a Read more /
 * Show less toggle, so the sections below the description stay reachable. The toggle
 * only appears when the text actually overflows when collapsed.
 */
@Composable
fun CollapsibleDescription(description: String) {
  var expanded by rememberSaveable(description) { mutableStateOf(false) }
  var hasOverflow by remember(description) { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 16.dp, bottom = 32.dp, start = 16.dp, end = 16.dp),
  ) {
    Text(
      text = description,
      style = AGTypography.ArticleText,
      color = Palette.White,
      maxLines = if (expanded) Int.MAX_VALUE else COLLAPSED_MAX_LINES,
      overflow = TextOverflow.Ellipsis,
      onTextLayout = { result ->
        if (!expanded) hasOverflow = result.hasVisualOverflow
      },
    )
    if (expanded || hasOverflow) {
      Text(
        text = stringResource(
          if (expanded) R.string.appview_description_show_less
          else R.string.appview_description_read_more
        ),
        style = AGTypography.BodyBold,
        color = Palette.Primary,
        modifier = Modifier
          .padding(top = 8.dp)
          .clickable { expanded = !expanded },
      )
    }
  }
}
