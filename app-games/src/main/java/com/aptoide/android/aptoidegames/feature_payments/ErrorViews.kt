package com.aptoide.android.aptoidegames.feature_payments

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.PreviewLandscapeDark
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.design_system.PrimaryButton
import com.aptoide.android.aptoidegames.drawables.icons.getPaymentsGenericError
import com.aptoide.android.aptoidegames.drawables.icons.getPaymentsNoNetworkError
import com.aptoide.android.aptoidegames.drawables.icons.getPaymentsWarningIcon
import com.aptoide.android.aptoidegames.error_views.RetryButton
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun PortraitPaymentErrorView(
  message: String? = null,
  description: String? = null,
  onRetryClick: (() -> Unit)?,
  onContactUsClick: () -> Unit,
) {
  Column(
    modifier = Modifier
      .defaultMinSize(minHeight = 376.dp)
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Spacer(modifier = Modifier.weight(1f))
    ErrorViewContent(
      modifier = Modifier.padding(horizontal = 40.dp, vertical = 20.dp),
      icon = getPaymentsGenericError(Palette.Black, Palette.Primary),
      message = message ?: stringResource(R.string.error_message_no_internet_title),
      description = description ?: stringResource(R.string.try_again),
    )
    Spacer(modifier = Modifier.weight(1f))
    onRetryClick?.let {
      RetryButton(
        modifier = Modifier.padding(horizontal = 16.dp),
        onClick = it
      )
    }
    if (message == null) { //Generic error
      Text(
        modifier = Modifier
          .minimumInteractiveComponentSize()
          .padding(horizontal = 16.dp)
          .fillMaxWidth()
          .clickable(onClick = onContactUsClick),
        text = stringResource(R.string.contact_support_title),
        textAlign = TextAlign.Center,
        color = Palette.Black,
        style = AGTypography.InputsL,
        textDecoration = TextDecoration.Underline,
      )
    } else {
      Spacer(modifier = Modifier.padding(bottom = 24.dp))
    }
  }
}

@Composable
fun LandscapePaymentErrorView(
  message: String? = null,
  description: String? = null,
  onRetryClick: (() -> Unit)?,
  onContactUsClick: () -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxHeight()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Spacer(modifier = Modifier.weight(1f))
    ErrorViewContent(
      modifier = Modifier.padding(horizontal = 40.dp, vertical = 20.dp),
      icon = getPaymentsGenericError(Palette.Black, Palette.Primary),
      message = message ?: stringResource(R.string.error_message_no_internet_title),
      description = description ?: stringResource(R.string.try_again),
    )
    Spacer(modifier = Modifier.weight(1f))
    onRetryClick?.let {
      RetryButton(
        modifier = Modifier.padding(horizontal = 24.dp),
        onClick = it
      )
    }
    if (message == null) { //Generic error
      Text(
        modifier = Modifier
          .minimumInteractiveComponentSize()
          .padding(horizontal = 24.dp)
          .fillMaxWidth()
          .clickable(onClick = onContactUsClick),
        text = stringResource(R.string.contact_support_title),
        textAlign = TextAlign.Center,
        color = Palette.Black,
        style = AGTypography.InputsL,
        textDecoration = TextDecoration.Underline,
      )
    } else {
      Spacer(modifier = Modifier.padding(bottom = 32.dp))
    }
  }
}

@Composable
fun LandscapePaymentsNoConnectionView(onRetryClick: (() -> Unit)?) {
  Column(
    modifier = Modifier
      .fillMaxHeight()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Spacer(modifier = Modifier.weight(1f))
    ErrorViewContent(
      modifier = Modifier.padding(horizontal = 40.dp, vertical = 20.dp),
      icon = getPaymentsNoNetworkError(Palette.Black, Palette.Primary),
      message = stringResource(R.string.error_message_no_internet_title),
      description = stringResource(R.string.error_message_no_internet_body),
    )
    Spacer(modifier = Modifier.weight(1f))
    onRetryClick?.let {
      RetryButton(
        modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 32.dp),
        onClick = it
      )
    }
  }
}

@Composable
fun PortraitPaymentsNoConnectionView(onRetryClick: (() -> Unit)?) {
  Column(
    modifier = Modifier
      .defaultMinSize(minHeight = 376.dp)
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Spacer(modifier = Modifier.weight(1f))
    ErrorViewContent(
      modifier = Modifier.padding(vertical = 20.dp, horizontal = 40.dp),
      icon = getPaymentsNoNetworkError(Palette.Black, Palette.Primary),
      message = stringResource(R.string.error_message_no_internet_title),
      description = stringResource(R.string.error_message_no_internet_body),
    )
    Spacer(modifier = Modifier.weight(1f))
    onRetryClick?.let {
      RetryButton(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
        onClick = it
      )
    }
  }
}

@Composable
fun PortraitPaymentsItemOwnedView(
  onGoBackToGameCLick: (() -> Unit),
  onContactUsClick: () -> Unit,
) {
  Column(
    modifier = Modifier
      .defaultMinSize(minHeight = 376.dp)
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Spacer(modifier = Modifier.weight(1f))
    ErrorViewContent(
      modifier = Modifier.padding(horizontal = 40.dp, vertical = 20.dp),
      icon = getPaymentsWarningIcon(Palette.Black, Palette.Primary),
      message = stringResource(R.string.iap_already_processing_title),
      description = stringResource(R.string.iap_already_processing_body),
    )
    Spacer(modifier = Modifier.weight(1f))
    PrimaryButton(
      onClick = onGoBackToGameCLick,
      modifier = Modifier
        .padding(horizontal = 16.dp)
        .fillMaxWidth(),
      title = stringResource(id = R.string.go_back_to_game_button),
    )
    Text(
      modifier = Modifier
        .minimumInteractiveComponentSize()
        .padding(horizontal = 16.dp)
        .fillMaxWidth()
        .clickable(onClick = onContactUsClick),
      text = stringResource(R.string.contact_support_title),
      textAlign = TextAlign.Center,
      color = Palette.Black,
      style = AGTypography.InputsL,
      textDecoration = TextDecoration.Underline,
    )
  }
}

@Composable
fun LandscapePaymentsItemOwnedView(
  onGoBackToGameCLick: (() -> Unit),
  onContactUsClick: () -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxHeight()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Spacer(modifier = Modifier.weight(1f))
    ErrorViewContent(
      modifier = Modifier.padding(horizontal = 40.dp, vertical = 20.dp),
      icon = getPaymentsWarningIcon(Palette.Black, Palette.Primary),
      message = stringResource(R.string.iap_already_processing_title),
      description = stringResource(R.string.iap_already_processing_body),
    )
    Spacer(modifier = Modifier.weight(1f))
    PrimaryButton(
      onClick = onGoBackToGameCLick,
      modifier = Modifier
        .padding(horizontal = 24.dp)
        .fillMaxWidth(),
      title = stringResource(id = R.string.go_back_to_game_button),
    )
    Text(
      modifier = Modifier
        .minimumInteractiveComponentSize()
        .padding(horizontal = 24.dp)
        .fillMaxWidth()
        .clickable(onClick = onContactUsClick),
      text = stringResource(R.string.contact_support_title),
      textAlign = TextAlign.Center,
      color = Palette.Black,
      style = AGTypography.InputsL,
      textDecoration = TextDecoration.Underline,
    )
  }
}

@Composable
private fun ErrorViewContent(
  icon: ImageVector,
  message: String,
  description: String?,
  modifier: Modifier = Modifier,
  iconModifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Image(
      modifier = iconModifier,
      imageVector = icon,
      contentDescription = null
    )
    Text(
      modifier = Modifier.padding(top = 16.dp),
      text = message,
      color = Palette.Black,
      style = AGTypography.Title,
      textAlign = TextAlign.Center,
    )
    description?.let {
      Text(
        modifier = Modifier.padding(top = 8.dp),
        text = description,
        color = Palette.Black,
        style = AGTypography.DescriptionGames,
        textAlign = TextAlign.Center,
      )
    }
  }
}

@PreviewDark
@Composable
fun PortraitPaymentErrorViewPreview() {
  AppGamesPaymentBottomSheet {
    PortraitPaymentErrorView(onRetryClick = {}, onContactUsClick = {})
  }
}

@PreviewLandscapeDark
@Composable
fun LandscapePaymentErrorViewPreview() {
  AppGamesPaymentBottomSheet {
    LandscapePaymentErrorView(onRetryClick = {}, onContactUsClick = {})
  }
}

@PreviewDark
@Composable
fun PortraitPaymentsNoConnectionViewPreview() {
  AppGamesPaymentBottomSheet {
    PortraitPaymentsNoConnectionView(onRetryClick = {})
  }
}

@PreviewLandscapeDark
@Composable
fun LandscapePaymentsNoConnectionViewPreview() {
  AppGamesPaymentBottomSheet {
    LandscapePaymentsNoConnectionView(onRetryClick = {})
  }
}

@PreviewDark
@Composable
fun PortraitPaymentItemOwnedViewPreview() {
  AppGamesPaymentBottomSheet {
    PortraitPaymentsItemOwnedView(onGoBackToGameCLick = {}, onContactUsClick = {})
  }
}

@PreviewLandscapeDark
@Composable
fun LandscapePaymentItemOwnedViewPreview() {
  AppGamesPaymentBottomSheet {
    LandscapePaymentsItemOwnedView(onGoBackToGameCLick = {}, onContactUsClick = {})
  }
}
