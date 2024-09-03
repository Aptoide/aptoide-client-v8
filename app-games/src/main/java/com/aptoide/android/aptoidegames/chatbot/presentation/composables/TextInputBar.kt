package com.aptoide.android.aptoidegames.chatbot.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun TextInputBar(
    onMessageSent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var messageText by remember { mutableStateOf(TextFieldValue("")) }

    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(shape = RoundedCornerShape(8.dp))
            .background(Palette.Secondary),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        TextField(
            value = messageText,
            onValueChange = { newValue -> messageText = newValue },
            textStyle = AGTypography.InputsL.copy(color = Palette.White),
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
                .background(Palette.Secondary),
            placeholder = {
                Text(
                    text = "Type a message...",
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Visible,
                    style = AGTypography.InputsL,
                    color = Palette.GreyLight
                )
            }
        )

        IconButton(
            enabled = messageText.text.isNotBlank(),
            onClick = {
                if (messageText.text.isNotBlank()) {
                    onMessageSent(messageText.text)
                    messageText = TextFieldValue("") // Clear the input field
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                tint = Palette.White,
                contentDescription = "Send Message",
            )
        }
    }
}
