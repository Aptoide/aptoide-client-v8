package com.aptoide.android.aptoidegames.error_views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cm.aptoide.pt.extensions.PreviewDark
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGenericAnalytics
import com.aptoide.android.aptoidegames.drawables.icons.getNoNetworkError
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun NoConnectionView(
  onRetryClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val genericAnalytics = rememberGenericAnalytics()
  ErrorView(
    imageVector = getNoNetworkError(Palette.Primary, Palette.GreyLight, Palette.White),
    title = stringResource(R.string.error_message_no_internet_title),
    subtitle = stringResource(R.string.error_message_no_internet_body),
    onRetryClick = {
      genericAnalytics.sendNoNetworkRetry()
      onRetryClick()
    },
    modifier = modifier,
  )
}

@PreviewDark
@Composable
fun NoConnectionPreview() {
  AptoideTheme {
    NoConnectionView(
      onRetryClick = {},
    )
  }
}