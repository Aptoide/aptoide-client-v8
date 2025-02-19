package com.aptoide.android.aptoidegames.gamegenie.presentation

import com.aptoide.android.aptoidegames.gamegenie.data.database.model.ChatInteractionEntity
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.GameGenieHistoryEntity
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction
import com.aptoide.android.aptoidegames.gamegenie.domain.GameContext
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieChat
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun GameGenieChat.toEntity() = GameGenieHistoryEntity(
  id = this.id,
  conversation = this.conversation.map { it.toEntity() }
)

fun ChatInteraction.toEntity() = ChatInteractionEntity(
  gpt = this.gpt,
  user = this.user,
  apps = Gson().toJson(this.apps)
)

fun GameGenieHistoryEntity.toDomain() = GameGenieChat(
  id = this.id,
  conversation = this.conversation.map { it.toDomain() }
)

fun ChatInteractionEntity.toDomain() = ChatInteraction(
  gpt = this.gpt,
  user = this.user,
  apps = Gson().fromJson(this.apps, object : TypeToken<List<GameContext>>() {}.type)
)

fun GameGenieResponse.toGameGenieChat() =
  GameGenieChat(
    id = this.id,
    conversation = this.conversation
  )
