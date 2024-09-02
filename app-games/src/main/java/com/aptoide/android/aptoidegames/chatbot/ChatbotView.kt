package com.aptoide.android.aptoidegames.chatbot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.extensions.ScreenData
import coil.compose.rememberAsyncImagePainter
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.chatbot.io_models.GameContext
import com.aptoide.android.aptoidegames.home.GenericErrorView
import com.aptoide.android.aptoidegames.home.NoConnectionView
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

const val chatbotRoute = "chatbot"

fun chatbotScreen() = ScreenData.withAnalytics(
  route = chatbotRoute,
  screenAnalyticsName = "Chat",
  arguments = emptyList()
) { arguments, navigate, navigateBack ->

  val viewModel = hiltViewModel<ChatbotViewModel>()
  val uiState by viewModel.uiState.collectAsState()

  ChatbotView(
    uiState = uiState,
    onError = {}, // fix this
    onMessageSend = viewModel::reload
  )
}

@Composable
fun ChatbotView(
  uiState: ChatbotUIState,
  onError: () -> Unit,
  onMessageSend: (String) -> Unit,
  ) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .wrapContentSize(Alignment.TopCenter)
      .imePadding(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    when (uiState.type) {
      ChatbotUiStateType.LOADING -> ChatScreen(uiState = uiState, onMessageSend = {})
      ChatbotUiStateType.NO_CONNECTION -> NoConnectionView(onRetryClick = onError)
      ChatbotUiStateType.ERROR -> GenericErrorView(onError)
      ChatbotUiStateType.IDLE -> ChatScreen(uiState = uiState, onMessageSend = onMessageSend) // will recompose when changes are made
    }
  }
}

@Composable
fun ChatScreen(uiState: ChatbotUIState, onMessageSend: (String) -> Unit) {
  val listState = rememberLazyListState()
  val messages = uiState.messages
  LaunchedEffect(key1 = messages.size) {
    listState.animateScrollToItem(index = messages.size - 1)
  }

  Column(
    modifier = Modifier
      .padding(vertical = 4.dp)
  ) {
    MessageList(
      messages = messages,
      listState = listState,
      modifier = Modifier
        .weight(1f)
    )

    if (uiState.packageName != null)
      GameInstallationBlock(uiState.packageName, uiState.context)

    TextInputBar(
      onMessageSent = onMessageSend,
      modifier = Modifier
        .fillMaxWidth()
    )
  }
}

@Composable
fun GameInstallationBlock(packageName: String, context: List<GameContext>) {
  val gameContext = context.find { it.packageName == packageName }

  gameContext?.let {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .background(
          color = Color.Transparent,
        )
        .border(2.dp, Palette.Primary, shape = RoundedCornerShape(12.dp))
        .padding(16.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Image(
          painter = rememberAsyncImagePainter(model = it.icon),
          contentDescription = "${it.name} Icon",
          modifier = Modifier
            .size(64.dp),
          contentScale = ContentScale.Fit
        )
        Text(
          text = it.name,
          style = AGTypography.BodyBold,
          fontSize = 18.sp,
          modifier = Modifier
            .weight(1f)
            .padding(8.dp)
        )
      }
    }
  }
}

@Composable
fun MessageList(messages: List<ChatbotMessage>, listState: LazyListState, modifier: Modifier = Modifier) {
  LazyColumn(
    state = listState,
    verticalArrangement = Arrangement.Bottom,
    modifier = modifier
      .padding(8.dp),
    contentPadding = PaddingValues(vertical = 8.dp)
  ) {
    items(messages.size) { index ->
      MessageBubble(message = messages[index])
    }
  }
}

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
        tint = Palette.GreyLight,
        contentDescription = "Send Message",
        )
    }
  }
}

@Composable
fun MessageBubble(message: ChatbotMessage) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .wrapContentWidth(if (message.isUserMessage()) Alignment.End else Alignment.Start)
  ) {
    if (!message.isUserMessage()) {
      Text(
        text = "Assistant", //TODO take this out
        style = AGTypography.BodyBold,
        modifier = Modifier.padding(start = 8.dp, bottom = 1.dp)
      )
    } else {
      Text(
        text = "Me", //TODO take this out
        style = AGTypography.BodyBold,
        modifier = Modifier.padding(end = 8.dp, bottom = 1.dp).align(Alignment.End)
      )
    }
    Box(
      modifier = Modifier
        .padding(vertical = 4.dp, horizontal = 8.dp)
        .clip(shape = RoundedCornerShape(2.dp))
        .background(
          color = if (message.isUserMessage()) Palette.Primary else Palette.GreyLight,
        )
    ) {
      Text(
        text = message.messageBody.replace("\"", ""),
        style = AGTypography.Body,
        color = Palette.Black,
        fontSize = 16.sp,
        modifier = Modifier
          .padding(8.dp)
      )
    }
  }
}
