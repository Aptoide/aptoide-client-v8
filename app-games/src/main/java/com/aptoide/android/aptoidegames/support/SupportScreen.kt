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
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.AppGamesButton
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.ButtonStyle
import com.aptoide.android.aptoidegames.theme.shapes
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar
import com.aptoide.android.aptoidegames.toolbar.SimpleAppGamesToolbar
import cm.aptoide.pt.extensions.PreviewAll
import cm.aptoide.pt.extensions.getRandomString
import cm.aptoide.pt.extensions.sendMail

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SupportView(
  title: String,
  placeholderText: String,
  deviceInfo: String,
  email: String,
  subject: String,
  context: Context,
  navigateBack: () -> Unit,
) {
  var text by remember { mutableStateOf("") }
  val characterThreshold = 50L
  val descriptionCopy = stringResource(R.string.settings_payment_support_description_title)
  val restrictionCopy = " " + stringResource(R.string.settings_contact_form_character_limit_title)
  val bodyCore = "\n\n\nMy Hardware specs are\n" + deviceInfo + "\n\nPowered by Aptoide"
  val isKeyboardOpen by keyboardAsState()
  Scaffold(
    topBar = { if (isKeyboardOpen == Keyboard.Closed) SimpleAppGamesToolbar() else null }
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.TopCenter)
        .background(AppTheme.colors.background)
    ) {
      if (isKeyboardOpen == Keyboard.Closed) {
        AppGamesTopBar(navigateBack = { navigateBack() }, title = title)
        Spacer(modifier = Modifier.weight(24f))
      }
      Text(
        modifier = Modifier
          .padding(start = 16.dp, end = 16.dp)
          .fillMaxWidth(),
        text = buildAnnotatedString {
          withStyle(style = AppTheme.typography.bodyCopyBold.toSpanStyle()) {
            append(descriptionCopy)
          }
          withStyle(style = AppTheme.typography.bodyCopy.toSpanStyle()) {
            append(restrictionCopy)
          }
        }
      )
      Spacer(modifier = Modifier.weight(24f))
      TextField(
        value = text,
        onValueChange = { text = it },
        textStyle = AppTheme.typography.bodyCopyXS.copy(color = AppTheme.colors.textFieldTextColor),
        singleLine = false,
        modifier = Modifier
          .padding(start = 16.dp, end = 16.dp)
          .fillMaxWidth()
          .weight(392f)
          .clip(shapes.large)
          .border(
            width = 1.dp,
            color = if (text.length >= characterThreshold) AppTheme.colors.textFieldBorderColor else Color.Transparent,
            shape = shapes.large
          )
          .background(AppTheme.colors.textFieldBackgroundColor),
        colors = TextFieldDefaults.textFieldColors(
          focusedIndicatorColor = AppTheme.colors.textFieldBackgroundColor,
          unfocusedIndicatorColor = AppTheme.colors.textFieldBackgroundColor,
          disabledIndicatorColor = AppTheme.colors.textFieldBackgroundColor
        ),
        placeholder = {
          Text(
            text = placeholderText,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Visible,
            style = AppTheme.typography.bodyCopyXS,
            color = AppTheme.colors.textFieldPlaceholderTextColor
          )
        },
      )
      if (isKeyboardOpen == Keyboard.Closed) {
        Spacer(modifier = Modifier.weight(112f))
      } else {
        Spacer(modifier = Modifier.weight(32f))
      }
      AppGamesButton(
        title = stringResource(R.string.send_button),
        enabled = text.length >= characterThreshold,
        onClick = {
          context.sendMail(
            subject = subject,
            destinationEmail = email,
            body = text + bodyCore
          )
          navigateBack()
        },
        modifier = Modifier
          .padding(start = 16.dp, end = 16.dp)
          .height(48.dp)
          .imePadding()
          .fillMaxWidth(),
        style = ButtonStyle.Default(fillWidth = true)
      )
      Spacer(modifier = Modifier.weight(32f))
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

@PreviewAll
@Composable
fun SupportScreenPreview() {
  AptoideTheme {
    SupportView(
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
