package cm.aptoide.pt.app_games.home

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
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.app_games.R
import cm.aptoide.pt.app_games.theme.AppTheme
import cm.aptoide.pt.app_games.theme.AptoideTheme
import cm.aptoide.pt.extensions.PreviewAll

@Composable
fun EmptyView(text: String) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .semantics(mergeDescendants = true) { },
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Image(
      modifier = Modifier.padding(all = 48.dp),
      imageVector = AppTheme.icons.Gamepad,
      contentDescription = null
    )
    Text(
      modifier = Modifier.padding(start = 40.dp, end = 40.dp),
      text = text,
      style = AppTheme.typography.gameTitleTextCondensedXL,
      textAlign = TextAlign.Center,
    )
  }
}

@PreviewAll
@Composable
fun EmptyViewPreview() {
  AptoideTheme {
    EmptyView(
      text = stringResource(R.string.empty_category_body)
    )
  }
}
