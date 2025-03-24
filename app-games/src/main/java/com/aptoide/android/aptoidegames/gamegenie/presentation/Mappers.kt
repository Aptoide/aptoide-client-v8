package com.aptoide.android.aptoidegames.gamegenie.presentation

import cm.aptoide.pt.feature_apps.data.AppMapper
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.ChatInteractionEntity
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.GameGenieHistoryEntity
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteractionHistory
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieChat
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieChatHistory
import com.aptoide.android.aptoidegames.gamegenie.io_models.ChatInteractionResponse
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun GameGenieChat.toEntity() = GameGenieHistoryEntity(
  id = this.id,
  title = this.title,
  conversation = this.conversation.map { it.toEntity() }
)

fun ChatInteraction.toEntity() = ChatInteractionEntity(
  gpt = this.gpt,
  user = this.user,
  apps = Gson().toJson(this.apps.map { it.packageName })
)

fun GameGenieHistoryEntity.toDomain() = GameGenieChatHistory(
  id = this.id,
  title = this.title,
  conversation = this.conversation.map { it.toDomain() }
)

fun ChatInteractionEntity.toDomain() = ChatInteractionHistory(
  gpt = this.gpt,
  user = this.user,
  apps = Gson().fromJson(this.apps, object : TypeToken<List<String>>() {}.type)
)

fun ChatInteractionResponse.toChatInteraction(mapper: AppMapper) = ChatInteraction(
  gpt = this.gpt,
  user = this.user,
  apps =
  if (this.apps.isNullOrEmpty())
    emptyList()
  else
    this.apps.map { app ->
      app.let(mapper::map)
    }
)

fun GameGenieResponse.toGameGenieChat(mapper: AppMapper) =
  GameGenieChat(
    id = this.id,
    title = this.title,
    conversation = this.conversation.map { it.toChatInteraction(mapper) }
  )

fun GameGenieChat.toGameGenieChatHistory() =
  GameGenieChatHistory(
    id = id,
    title = title,
    conversation = conversation.map { interaction ->
      ChatInteractionHistory(
        gpt = interaction.gpt,
        user = interaction.user,
        apps = interaction.apps.map { app -> app.packageName }
      )
    }
  )
