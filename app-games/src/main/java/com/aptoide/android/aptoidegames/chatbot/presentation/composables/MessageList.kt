package com.aptoide.android.aptoidegames.chatbot.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.chatbot.domain.ChatbotMessage
import com.aptoide.android.aptoidegames.chatbot.domain.GameContext

@Composable
fun MessageList(messages: List<ChatbotMessage>, apps: List<GameContext>, navigateTo: (String) -> Unit, listState: LazyListState, modifier: Modifier = Modifier) {
    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
            .padding(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(messages.size) { index ->
            if (index == messages.size - 1)
                MessageBubble(message = messages[index], apps = apps, navigateTo = navigateTo)
            else
                MessageBubble(message = messages[index])
        }
    }
}
