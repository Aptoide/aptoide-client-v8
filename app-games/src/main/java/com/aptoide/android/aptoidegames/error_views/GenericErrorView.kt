package com.aptoide.android.aptoidegames.error_views

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import cm.aptoide.pt.extensions.PreviewDark
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.design_system.PrimaryButton
import com.aptoide.android.aptoidegames.drawables.banners.getChessPatternBanner
import com.aptoide.android.aptoidegames.drawables.icons.getGenericError
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

/**
 * The text colour of an error drawn on the app's usual screen background. A screen with a
 * background of its own (the redeem flow's gradient) passes the colour that reads on it.
 */
@Composable
fun errorContentColor(): Color =
  if (BuildConfig.FLAVOR_brand == "vanilla") Palette.Black else Palette.White

/**
 * @param onRetryClick null for an error that retrying cannot fix: no Retry button is drawn.
 * @param contentColor the title and subtitle colour; it must read on the caller's background.
 */
@Composable
fun ErrorView(
  imageVector: ImageVector,
  title: String,
  subtitle: String?,
  onRetryClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier,
  contentColor: Color = errorContentColor(),
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
      val textColor = contentColor
      PaddedRow(sideWeight = 32f) {
        Column(
          modifier = Modifier.weight(296f),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = title,
            style = AGTypography.Title,
            color = textColor,
            textAlign = TextAlign.Center,
          )
          subtitle?.let {
            Text(
              text = it,
              style = AGTypography.DescriptionGames,
              color = textColor,
              textAlign = TextAlign.Center,
            )
          }
        }
      }
      Spacer(modifier = Modifier.weight(24f))
      onRetryClick?.let {
        PaddedRow(sideWeight = 16f) {
          RetryButton(
            onClick = it,
            modifier = Modifier.weight(328f)
          )
        }
      }
      Spacer(modifier = Modifier.weight(154f))
      Image(
        imageVector = getChessPatternBanner(Palette.Primary),
        contentDescription = null,
        modifier = Modifier.fillMaxWidth(),
        contentScale = ContentScale.FillWidth
      )
    }
  }
}

/**
 * @param onRetryClick null hides the Retry button.
 * @param title and [subtitle] replace the generic text, for an error with its own message.
 * @param contentColor the text and illustration outline colour; see [ErrorView].
 */
@Composable
fun GenericErrorView(
  onRetryClick: (() -> Unit)?,
  modifier: Modifier = Modifier,
  title: String = stringResource(R.string.error_message_generic_title),
  subtitle: String? = stringResource(R.string.error_message_generic_body),
  contentColor: Color = errorContentColor(),
) {
  ErrorView(
    imageVector = getGenericError(Palette.Primary, Palette.GreyLight, contentColor),
    title = title,
    subtitle = subtitle,
    onRetryClick = onRetryClick,
    modifier = modifier,
    contentColor = contentColor,
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
  PrimaryButton(
    onClick = onClick,
    modifier = modifier.fillMaxWidth(),
    title = stringResource(id = R.string.retry_button),
  )
}

@Composable
fun OkButton(
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  PrimaryButton(
    onClick = onClick,
    modifier = modifier.fillMaxWidth(),
    title = stringResource(id = R.string.ok_button),
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
