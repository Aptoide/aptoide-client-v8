package com.aptoide.android.aptoidegames.gamegenie.presentation

import cm.aptoide.pt.feature_apps.data.AppMapper
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.ChatInteractionEntity
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.GameCompanionEntity
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.GameGenieHistoryEntity
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.UserMessageEntity
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteractionHistory
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieChat
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieChatHistory
import com.aptoide.android.aptoidegames.gamegenie.domain.UserMessage
import com.aptoide.android.aptoidegames.gamegenie.io_models.ChatInteractionResponse
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun GameGenieChat.toEntity() = GameGenieHistoryEntity(
  id = this.id,
  title = this.title,
  conversation = this.conversation.map { it.toEntity() }
)

fun GameGenieChat.toCompanionEntity(packageName: String) = GameCompanionEntity(
  id = this.id,
  name = this.title,
  gamePackageName = packageName,
  conversation = this.conversation.map { it.toEntity() },
  lastMessageTimestamp = System.currentTimeMillis()
)

fun ChatInteraction.toEntity() = ChatInteractionEntity(
  gpt = this.gpt,
  user = this.user?.toEntity(),
  videoId = this.videoId,
  apps = Gson().toJson(this.apps.map { it.packageName }),
  followUps = this.followUps
)

fun UserMessage.toEntity() = UserMessageEntity(
  text = this.text,
  image = this.image
)

fun GameGenieHistoryEntity.toDomain() = GameGenieChatHistory(
  id = this.id,
  title = this.title,
  conversation = this.conversation.map { it.toDomain() }
)

fun GameCompanionEntity.toDomain() = GameGenieChatHistory(
  id = this.id,
  title = this.name,
  conversation = this.conversation.map { it.toDomain() }
)

fun ChatInteractionEntity.toDomain() = ChatInteractionHistory(
  gpt = this.gpt,
  user = this.user?.toDomain(),
  videoId = this.videoId,
  apps = Gson().fromJson(this.apps, object : TypeToken<List<String>>() {}.type),
  followUps = this.followUps
)

fun UserMessageEntity.toDomain() = UserMessage(
  text = this.text,
  image = this.image
)

fun ChatInteractionResponse.toChatInteraction(mapper: AppMapper) = ChatInteraction(
  gpt = this.gpt,
  user = this.user,
  videoId = this.video,
  apps =
    if (this.apps.isNullOrEmpty())
      emptyList()
    else
      this.apps.map { app ->
        app.let(mapper::map)
      },
  followUps = this.followUps?.take(3).orEmpty()
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
    conversation = conversation.withoutLocalActions().map { interaction ->
      ChatInteractionHistory(
        gpt = interaction.gpt,
        user = interaction.user,
        videoId = interaction.videoId,
        apps = interaction.apps.map { app -> app.packageName },
        followUps = interaction.followUps
      )
    }
  )

/**
 * HACKATHON: drops the client-only Genie purchase turns (those with an action) so the backend and
 * the saved chat only ever see the regular conversation. A user reply written on a dropped turn
 * moves onto the previous kept turn, so the next normal message still reaches the backend.
 */
internal fun List<ChatInteraction>.withoutLocalActions(): List<ChatInteraction> =
  fold(mutableListOf()) { kept, interaction ->
    if (interaction.action == null || kept.isEmpty()) {
      kept += interaction.copy(action = null)
    } else if (interaction.user != null) {
      kept[kept.lastIndex] = kept.last().copy(user = interaction.user)
    }
    kept
  }
