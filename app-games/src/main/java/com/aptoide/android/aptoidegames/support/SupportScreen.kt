package com.aptoide.android.aptoidegames.support

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.getRandomString
import cm.aptoide.pt.extensions.sendMail
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGenericAnalytics
import com.aptoide.android.aptoidegames.design_system.PrimaryButton
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar
import com.aptoide.android.aptoidegames.toolbar.SimpleAppGamesToolbar

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SupportView(
  type: String,
  title: String,
  placeholderText: String,
  deviceInfo: String,
  email: String,
  subject: String,
  context: Context,
  navigateBack: () -> Unit,
) {
  val genericAnalytics = rememberGenericAnalytics()
  var text by remember { mutableStateOf("") }
  val characterThreshold = 50L
  val bodyCore = "\n\n\nMy Hardware specs are\n" + deviceInfo + "\n\nPowered by Aptoide"
  val isKeyboardOpen by keyboardAsState()
  Scaffold(
    topBar = { if (isKeyboardOpen == Keyboard.Closed) SimpleAppGamesToolbar() }
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.TopCenter)
        .background(Palette.Black)
    ) {
      if (isKeyboardOpen == Keyboard.Closed) {
        AppGamesTopBar(navigateBack = { navigateBack() }, title = title)
        Spacer(modifier = Modifier.weight(16f))
      }
      Text(
        text = stringResource(R.string.settings_payment_support_description_and_limit),
        modifier = Modifier
          .padding(start = 16.dp, end = 16.dp)
          .fillMaxWidth(),
        style = AGTypography.InputsL,
        color = Palette.White,
        maxLines = 1
      )
      Spacer(modifier = Modifier.weight(16f))
      TextField(
        value = text,
        onValueChange = { text = it },
        textStyle = AGTypography.DescriptionGames.copy(color = Palette.White),
        singleLine = false,
        modifier = Modifier
          .padding(start = 16.dp, end = 16.dp)
          .fillMaxWidth()
          .weight(396f)
          .clip(RectangleShape)
          .border(
            width = 1.dp,
            color = if (text.length >= characterThreshold) Palette.White else Palette.Grey,
            shape = RectangleShape
          ),
        colors = TextFieldDefaults.textFieldColors(
          backgroundColor = Color.Transparent,
          focusedIndicatorColor = Color.Transparent,
          unfocusedIndicatorColor = Color.Transparent,
          disabledIndicatorColor = Color.Transparent
        ),
        placeholder = {
          Text(
            text = placeholderText,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Visible,
            style = AGTypography.DescriptionGames,
            color = Palette.GreyLight
          )
        },
      )
      if (isKeyboardOpen == Keyboard.Closed) {
        Spacer(modifier = Modifier.weight(32f))
      } else {
        Spacer(modifier = Modifier.weight(16f))
      }
      PrimaryButton(
        onClick = {
          genericAnalytics.sendFeedbackSent(type)
          context.sendMail(
            subject = subject,
            destinationEmail = email,
            body = text + bodyCore
          )
          navigateBack()
        },
        modifier = Modifier
          .padding(start = 16.dp, end = 16.dp)
          .imePadding()
          .fillMaxWidth(),
        enabled = text.length >= characterThreshold,
        title = stringResource(R.string.send_button),
      )
      if (isKeyboardOpen == Keyboard.Closed) {
        Spacer(modifier = Modifier.weight(40f))
      } else {
        Spacer(modifier = Modifier.weight(16f))
      }
    }
  }
}

enum class Keyboard {
  Opened,
  Closed
}

@Composable
fun keyboardAsState(): State<Keyboard> {
  val keyboardState = remember { mutableStateOf(Keyboard.Closed) }
  val view = LocalView.current
  DisposableEffect(view) {
    val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
      val rect = Rect()
      view.getWindowVisibleDisplayFrame(rect)
      val screenHeight = view.rootView.height
      val keypadHeight = screenHeight - rect.bottom
      keyboardState.value = if (keypadHeight > screenHeight * 0.15) {
        Keyboard.Opened
      } else {
        Keyboard.Closed
      }
    }
    view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

    onDispose {
      view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
    }
  }

  return keyboardState
}

@PreviewDark
@Composable
fun SupportScreenPreview() {
  AptoideTheme {
    SupportView(
      type = "",
      title = getRandomString(1..2),
      placeholderText = getRandomString(10..120),
      deviceInfo = getRandomString(1..2),
      email = getRandomString(1..3),
      context = LocalContext.current,
      subject = getRandomString(1..2),
      navigateBack = {}
    )
  }
}
