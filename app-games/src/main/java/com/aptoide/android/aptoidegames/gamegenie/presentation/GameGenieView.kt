package com.aptoide.android.aptoidegames.gamegenie.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.extensions.ScreenData
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.MessageList
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.TextInputBar
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.error_views.NoConnectionView

const val genieRoute = "chatbot"

fun chatbotScreen() = ScreenData.withAnalytics(
    route = genieRoute,
    screenAnalyticsName = "Chat",
    arguments = emptyList()
) { _, navigate, _ ->

    val viewModel = hiltViewModel<ChatbotViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    ChatbotView(
        uiState = uiState,
        navigateTo = navigate,
        onError = {}, // fix this
        onMessageSend = viewModel::reload
    )
}

@Composable
fun ChatbotView(
    uiState: GameGenieUIState,
    navigateTo: (String) -> Unit,
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
            GameGenieUIStateType.LOADING -> ChatScreen(uiState = uiState, navigateTo = navigateTo, onMessageSend = {})
            GameGenieUIStateType.NO_CONNECTION -> NoConnectionView(onRetryClick = onError)
            GameGenieUIStateType.ERROR -> GenericErrorView(onError)
            GameGenieUIStateType.IDLE -> ChatScreen(uiState = uiState, navigateTo = navigateTo, onMessageSend = onMessageSend)
        }
    }
}

@Composable
fun ChatScreen(
    uiState: GameGenieUIState,
    navigateTo: (String) -> Unit,
    onMessageSend: (String) -> Unit
) {
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
            apps = uiState.apps,
            navigateTo = navigateTo,
            listState = listState,
            modifier = Modifier
                .weight(1f)
        )

        TextInputBar(
            onMessageSent = onMessageSend,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}
