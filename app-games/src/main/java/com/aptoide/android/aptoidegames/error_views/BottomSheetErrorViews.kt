package com.aptoide.android.aptoidegames.error_views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import com.aptoide.android.aptoidegames.AptoideGamesBottomSheet
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.drawables.icons.getGenericError
import com.aptoide.android.aptoidegames.drawables.icons.getNoNetworkError
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun BottomSheetErrorView(
  imageVector: ImageVector,
  title: String,
  modifier: Modifier = Modifier,
  subtitle: String? = null,
  button: @Composable () -> Unit,
) {
  Column(
    modifier = modifier
      .defaultMinSize(minHeight = 376.dp)
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Spacer(modifier = Modifier.weight(1f))
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Image(
        imageVector = imageVector,
        contentDescription = null
      )
      Text(
        modifier = Modifier.padding(top = 16.dp),
        text = title,
        color = Palette.White,
        style = AGTypography.Title,
        textAlign = TextAlign.Center,
      )
      subtitle?.let {
        Text(
          modifier = Modifier.padding(top = 8.dp),
          text = subtitle,
          color = Palette.White,
          style = AGTypography.DescriptionGames,
          textAlign = TextAlign.Center,
        )
      }
    }
    Spacer(modifier = Modifier.weight(1f))
    button()
  }
}

@Composable
fun BottomSheetGenericErrorView(
  onRetryClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BottomSheetErrorView(
    imageVector = getGenericError(Palette.Primary, Palette.GreyLight, Palette.White),
    title = stringResource(R.string.error_message_generic_title),
    subtitle = stringResource(R.string.error_message_generic_body),
    modifier = modifier,
  ) {
    RetryButton(onClick = onRetryClick)
  }
}

@Composable
fun BottomSheetNoConnectionView(
  onRetryClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BottomSheetErrorView(
    imageVector = getNoNetworkError(Palette.Primary, Palette.GreyLight, Palette.White),
    title = stringResource(R.string.error_message_no_internet_title),
    subtitle = stringResource(R.string.error_message_no_internet_body),
    modifier = modifier,
  ) {
    RetryButton(onClick = onRetryClick)
  }
}

@Composable
fun BottomSheetGenericErrorOkView(
  onOkClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  BottomSheetErrorView(
    imageVector = getGenericError(Palette.Primary, Palette.GreyLight, Palette.White),
    title = stringResource(R.string.error_message_no_internet_title),
    modifier = modifier,
  ) {
    OkButton(onClick = onOkClick)
  }
}

@PreviewDark
@Composable
fun PreviewBottomSheetGenericErrorView() {
  AptoideGamesBottomSheet {
    BottomSheetGenericErrorView(onRetryClick = { })
  }
}

@PreviewDark
@Composable
fun PreviewBottomSheetNoConnectionView() {
  AptoideGamesBottomSheet {
    BottomSheetNoConnectionView(onRetryClick = { })
  }
}

@PreviewDark
@Composable
fun PreviewBottomSheetGenericErrorOkView() {
  AptoideGamesBottomSheet {
    BottomSheetGenericErrorOkView(onOkClick = { })
  }
}
