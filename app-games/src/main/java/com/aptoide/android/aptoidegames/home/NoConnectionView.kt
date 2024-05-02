package com.aptoide.android.aptoidegames.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import cm.aptoide.pt.extensions.PreviewAll

@Composable
fun NoConnectionView(
  onRetryClick: () -> Unit,
  modifier: Modifier = Modifier,
) {

  NoConnection(
    modifier = modifier,
    onRetryClick = {
      onRetryClick()
    }
  )
}

@Composable
fun NoConnection(
  modifier: Modifier = Modifier,
  onRetryClick: () -> Unit,
) {
  Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Column(modifier = Modifier.weight(336f)) {
      Spacer(modifier = Modifier.weight(48f))
      Image(
        modifier = Modifier.weight(248f),
        imageVector = AppTheme.icons.NoConnection,
        contentDescription = "No connection"
      )
      Spacer(modifier = Modifier.weight(40f))
    }
    PaddedRow(sideWeight = 40f) {
      Text(
        modifier = Modifier.weight(296f),
        text = stringResource(R.string.generic_network_error_message),
        style = AppTheme.typography.gameTitleTextCondensed,
        textAlign = TextAlign.Center,
      )
    }
    Spacer(modifier = Modifier.weight(24f))
    PaddedRow(sideWeight = 16f) {
      RetryButton(
        onClick = onRetryClick,
        modifier = Modifier.weight(344f)
      )
    }
    Spacer(modifier = Modifier.weight(216f))
  }
}

@Composable
fun PaddedRow(
  sideWeight: Float,
  modifier: Modifier = Modifier,
  content: @Composable RowScope.() -> Unit,
) {
  Row(modifier = modifier) {
    Spacer(modifier = Modifier.weight(sideWeight))
    content()
    Spacer(modifier = Modifier.weight(sideWeight))
  }
}

@PreviewAll
@Composable
fun NoConnectionPreview() {
  AptoideTheme {
    NoConnection(
      onRetryClick = {},
      modifier = Modifier
      // modifier = Modifier.padding(top = 240.dp)
    )
  }
}
