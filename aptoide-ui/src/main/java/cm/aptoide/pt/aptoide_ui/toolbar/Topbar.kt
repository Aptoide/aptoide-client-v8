package cm.aptoide.pt.aptoide_ui.toolbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.aptoide_ui.theme.AppTheme

@Composable
fun TopBar(
  title: String,
  onBackPressed: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .defaultMinSize(minHeight = 56.dp)
      .padding(start = 16.dp, top = 16.dp)
      .background(color = Color.Transparent),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Image(
      imageVector = Icons.Filled.ArrowBack,
      colorFilter = ColorFilter.tint(AppTheme.colors.onBackground),
      contentDescription = "Back",
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .clickable(onClick = onBackPressed)
        .size(24.dp)
    )
    Text(
      text = title,
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      textAlign = TextAlign.Center,
      overflow = TextOverflow.Ellipsis,
      maxLines = 1,
      style = AppTheme.typography.medium_M
    )
    Spacer(Modifier.width(64.dp))
  }
}
