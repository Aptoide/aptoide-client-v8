package cm.aptoide.pt.toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.theme.AppTheme
import cm.aptoide.pt.theme.grey

@Composable
fun NavigationTopBar(
  title: String,
  onBackPressed: () -> Unit,
) {
  TopBar(title = title) {
    IconButton(
      modifier = Modifier
        .clip(CircleShape)
        .size(32.dp),
      onClick = onBackPressed
    ) {
      Icon(
        imageVector = Filled.ArrowBack,
        tint = AppTheme.colors.onBackground,
        contentDescription = "Back",
        modifier = Modifier.size(21.dp)
      )
    }
  }
}

@Composable
fun AppViewTopBar(onBackPressed: () -> Unit) {
  TopBar(title = "") {
    IconButton(
      modifier = Modifier
        .background(
          color = Color.White,
          shape = CircleShape
        )
        .size(32.dp)
        .clip(CircleShape),
      onClick = onBackPressed
    ) {
      Icon(
        imageVector = Filled.ArrowBack,
        tint = grey,
        contentDescription = "Back",
        modifier = Modifier.size(16.dp)
      )
    }
  }
}

@Composable
private fun TopBar(
  title: String,
  backIcon: @Composable () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(height = 48.dp)
      .padding(start = 16.dp, top = 16.dp, end = 16.dp)
      .background(color = Color.Transparent),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    backIcon()
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
    Spacer(Modifier.width(32.dp))
  }
}
