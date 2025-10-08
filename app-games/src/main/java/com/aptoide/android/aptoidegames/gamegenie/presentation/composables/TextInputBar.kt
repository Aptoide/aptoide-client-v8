package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun TextInputBar(
  onMessageSent: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var messageText by remember { mutableStateOf("") }
  val keyboardController = LocalSoftwareKeyboardController.current
  val focusManager = LocalFocusManager.current

  TextField(
    value = messageText,
    onValueChange = { newValue -> messageText = newValue },
    textStyle = AGTypography.Chat.copy(color = Palette.White),
    singleLine = true,
    modifier = modifier.background(Palette.GreyDark),
    placeholder = {
      Text(
        text = stringResource(R.string.genai_input_message_field),
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Visible,
        style = AGTypography.ChatBold,
        color = Palette.GreyLight
      )
    },
    trailingIcon = {
      val isEnabled = messageText.isNotBlank()
      val iconTint = if (isEnabled) Palette.Primary else Palette.GreyLight

      IconButton(
        enabled = isEnabled,
        modifier = Modifier.background(Color.Transparent),
        onClick = {
          if (isEnabled) {
            onMessageSent(messageText)
            messageText = ""
            keyboardController?.hide()
            focusManager.clearFocus()
          }
        }
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Send,
          tint = iconTint,
          contentDescription = "Send Message"
        )
      }
    },
    keyboardOptions = KeyboardOptions.Default.copy(
      imeAction = ImeAction.Send
    ),
    keyboardActions = KeyboardActions(
      onSend = {
        if (messageText.isNotBlank()) {
          onMessageSent(messageText)
          messageText = ""
          keyboardController?.hide()
          focusManager.clearFocus()
        }
      }
    ),
    colors = TextFieldDefaults.textFieldColors(
      backgroundColor = Color.Transparent,
      focusedIndicatorColor = Color.Transparent,
      unfocusedIndicatorColor = Color.Transparent
    )
  )
}
