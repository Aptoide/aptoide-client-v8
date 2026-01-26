package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import java.io.File

@Composable
fun TextInputBar(
  onMessageSent: (String, String?) -> Unit,
  modifier: Modifier = Modifier,
  screenshotPath: String? = null,
  onClearScreenshot: () -> Unit = {},
) {
  var messageText by remember { mutableStateOf("") }
  val keyboardController = LocalSoftwareKeyboardController.current
  val focusManager = LocalFocusManager.current

  Column(
    modifier = modifier.background(Palette.GreyDark)
  ) {
    screenshotPath?.let { path ->
      val file = File(path)
      if (file.exists()) {
        Box(
          modifier = Modifier
            .padding(top = 16.dp, start = 16.dp)
        ) {
          AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
              .data(file)
              .crossfade(true)
              .build(),
            contentDescription = "Screenshot preview",
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
              .height(124.dp)
              .wrapContentWidth()
          )

          Box(
            modifier = Modifier
              .align(Alignment.TopEnd)
              .size(24.dp)
              .padding(4.dp)
              .background(Palette.GreyDark)
              .clickable { onClearScreenshot() },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Remove screenshot",
              tint = Palette.White,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }

    TextField(
      value = messageText,
      onValueChange = { newValue -> messageText = newValue },
    textStyle = AGTypography.ChatBold.copy(color = Palette.White),
      singleLine = true,
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .background(Palette.GreyDark),
      placeholder = {
        Text(
          text = stringResource(
            if (screenshotPath != null) {
              R.string.genai_input_message_field_with_screenshot
            } else {
              R.string.genai_input_message_field
            }
          ),
          textAlign = TextAlign.Start,
          overflow = TextOverflow.Visible,
          style = AGTypography.Chat,
          color = Palette.GreyLight
        )
      },
      trailingIcon = {
        if (messageText.isNotBlank()) {
          IconButton(
            modifier = Modifier.background(Color.Transparent),
            onClick = {
              onMessageSent(messageText, screenshotPath)
              messageText = ""
              keyboardController?.hide()
              focusManager.clearFocus()
            }
          ) {
            Icon(
              imageVector = getSendIconEnabled(),
              tint = Palette.Primary,
              contentDescription = "Send Message"
            )
          }
        }
      },
      keyboardOptions = KeyboardOptions.Default.copy(
        imeAction = ImeAction.Send
      ),
      keyboardActions = KeyboardActions(
        onSend = {
          if (messageText.isNotBlank()) {
            onMessageSent(messageText, screenshotPath)
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
}
