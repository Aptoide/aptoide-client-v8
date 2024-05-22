package com.aptoide.android.aptoidegames.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cm.aptoide.pt.extensions.PreviewDark
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.AptoideTheme

@Composable
fun NoConnectionView(
  onRetryClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  ErrorView(
    imageVector = AppTheme.icons.NoNetworkError,
    title = stringResource(R.string.error_message_no_internet_title),
    subtitle = stringResource(R.string.error_message_no_internet_body),
    onRetryClick = onRetryClick,
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
