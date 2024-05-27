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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.drawables.icons.getPaymentsErrorIcon
import com.aptoide.android.aptoidegames.home.RetryButton
import com.aptoide.android.aptoidegames.theme.AGTypography
import getNoConnectionSimple

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
      icon = getPaymentsErrorIcon(),
      message = message ?: "Oops, something went wrong.", // TODO hardcoded string
      description = description ?: "Please try again or contact us.", // TODO hardcoded string
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
        text = "Contact Us", // TODO hardcoded string
        textAlign = TextAlign.Center,
        style = AGTypography.Body,
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
      icon = getPaymentsErrorIcon(),
      message = message ?: "Oops, something went wrong.", // TODO hardcoded string
      description = description ?: "Please try again or contact us.", // TODO hardcoded string
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
        text = "Contact Us", // TODO hardcoded string
        textAlign = TextAlign.Center,
        style = AGTypography.Body
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
      icon = getNoConnectionSimple(),
      message = "Oops, something went wrong.", // TODO hardcoded string
      description = "Please check your Internet connection.", // TODO hardcoded string
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
      icon = getNoConnectionSimple(),
      iconModifier = Modifier.size(152.dp),
      message = "Oops, something went wrong.", // TODO hardcoded string
      description = "Please check your Internet connection.", // TODO hardcoded string
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
private fun ErrorViewContent(
  modifier: Modifier = Modifier,
  icon: ImageVector,
  iconModifier: Modifier = Modifier,
  message: String,
  description: String?,
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
      style = AGTypography.SubHeadingS,
      textAlign = TextAlign.Center,
    )
    description?.let {
      Text(
        modifier = Modifier.padding(top = 8.dp),
        text = description,
        style = AGTypography.Body,
        textAlign = TextAlign.Center,
      )
    }
  }
}
