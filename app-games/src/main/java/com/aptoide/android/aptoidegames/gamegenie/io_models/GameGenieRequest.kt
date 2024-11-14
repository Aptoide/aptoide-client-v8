package com.aptoide.android.aptoidegames.gamegenie.io_models

import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction
import com.aptoide.android.aptoidegames.gamegenie.domain.ConversationIntent

data class GameGenieRequest (
    val conversation: List<ChatInteraction>,
    val state: ConversationIntent?,
)
