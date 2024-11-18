package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieMessage

@Composable
fun MessageList(messages: List<GameGenieMessage>, apps: List<String>, navigateTo: (String) -> Unit, listState: LazyListState, modifier: Modifier = Modifier) {
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
