package com.aptoide.android.aptoidegames.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.R.string
import com.aptoide.android.aptoidegames.theme.AppGamesButton
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.ButtonStyle.Default
import com.aptoide.android.aptoidegames.theme.blueGradient
import com.aptoide.android.aptoidegames.theme.lightGradient
import cm.aptoide.pt.extensions.PreviewAll

@Composable
fun GenericErrorView(
  onRetryClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(modifier = modifier) {
    Column {
      Spacer(
        modifier = Modifier
          .fillMaxWidth()
          .weight(177f)
          .background(
            brush = blueGradient,
            alpha = 0.3f
          )
      )
      Spacer(
        modifier = Modifier
          .fillMaxWidth()
          .weight(336f)
          .background(
            brush = blueGradient,
            alpha = 0.08f
          )
      )
      Spacer(
        modifier = Modifier
          .fillMaxWidth()
          .weight(200f)
          .background(
            brush = lightGradient,
            alpha = 0.08f
          )
      )
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Column(modifier = Modifier.weight(288f)) {
        Spacer(modifier = Modifier.weight(120f))
        Image(
          modifier = Modifier.weight(104f),
          imageVector = AppTheme.icons.ErrorBug,
          contentDescription = "Error or Bug",
        )
        Spacer(modifier = Modifier.weight(64f))
      }
      PaddedRow(sideWeight = 40f) {
        Text(
          modifier = Modifier.weight(296f),
          text = stringResource(string.generic_error_message),
          style = AppTheme.typography.gameTitleTextCondensed,
          textAlign = TextAlign.Center,
        )
      }
      Spacer(modifier = Modifier.weight(16f))
      PaddedRow(sideWeight = 16f) {
        RetryButton(
          onClick = onRetryClick,
          modifier = Modifier.weight(344f)
        )
      }
      Spacer(modifier = Modifier.weight(296f))
    }
  }
}

@Composable
fun RetryButton(
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  AppGamesButton(
    title = stringResource(id = R.string.button_retry_title),
    onClick = onClick,
    modifier = modifier,
    style = Default(fillWidth = true)
  )
}

@PreviewAll
@Composable
fun GenericErrorPreview() {
  AptoideTheme {
    GenericErrorView(
      onRetryClick = {},
      modifier = Modifier
      // modifier = Modifier.padding(top = 240.dp)
    )
  }
}
