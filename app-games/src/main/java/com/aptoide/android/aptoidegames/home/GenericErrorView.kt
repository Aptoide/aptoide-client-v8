package com.aptoide.android.aptoidegames.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import cm.aptoide.pt.extensions.PreviewDark
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.drawables.banners.getChessPatternBanner
import com.aptoide.android.aptoidegames.theme.AppGamesButton
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.ButtonStyle.Default

@Composable
fun ErrorView(
  imageVector: ImageVector,
  title: String,
  subtitle: String?,
  onRetryClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(modifier = modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Column(modifier = Modifier.weight(312f)) {
        Spacer(modifier = Modifier.weight(80f))
        Image(
          modifier = Modifier.weight(144f),
          imageVector = imageVector,
          contentDescription = null,
          contentScale = ContentScale.FillHeight
        )
        Spacer(modifier = Modifier.weight(88f))
      }
      PaddedRow(sideWeight = 32f) {
        Column(
          modifier = Modifier.weight(296f),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = title,
            style = AppTheme.typography.title,
            color = AppTheme.colors.onBackground,
            textAlign = TextAlign.Center,
          )
          subtitle?.let {
            Text(
              text = it,
              style = AppTheme.typography.descriptionGames,
              color = AppTheme.colors.onBackground,
              textAlign = TextAlign.Center,
            )
          }
        }
      }
      Spacer(modifier = Modifier.weight(24f))
      PaddedRow(sideWeight = 16f) {
        RetryButton(
          onClick = onRetryClick,
          modifier = Modifier.weight(328f)
        )
      }
      Spacer(modifier = Modifier.weight(154f))
      Image(
        imageVector = getChessPatternBanner(),
        contentDescription = null,
        modifier = Modifier.fillMaxWidth(),
        contentScale = ContentScale.FillWidth
      )
    }
  }
}

@Composable
fun GenericErrorView(
  onRetryClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  ErrorView(
    imageVector = AppTheme.icons.GenericError,
    title = stringResource(R.string.error_message_generic_title),
    subtitle = stringResource(R.string.error_message_generic_body),
    onRetryClick = onRetryClick,
    modifier = modifier,
  )
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

@PreviewDark
@Composable
fun GenericErrorPreview() {
  AptoideTheme {
    GenericErrorView(
      onRetryClick = {},
    )
  }
}
