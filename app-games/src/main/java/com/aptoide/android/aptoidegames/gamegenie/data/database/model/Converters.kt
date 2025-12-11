package com.aptoide.android.aptoidegames.gamegenie.data.database.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
  private val gson = Gson()

  @TypeConverter
  fun fromChatInteractionList(value: List<ChatInteractionEntity>): String {
    return gson.toJson(value)
  }

  @TypeConverter
  fun toChatInteractionList(value: String): List<ChatInteractionEntity> {
    val type = object : TypeToken<List<ChatInteractionEntity>>() {}.type
    return gson.fromJson(value, type)
  }

  @TypeConverter
  fun fromUserMessageEntity(value: UserMessageEntity?): String? {
    return if (value == null) null else gson.toJson(value)
  }

  @TypeConverter
  fun toUserMessageEntity(value: String?): UserMessageEntity? {
    return if (value == null) null else gson.fromJson(value, UserMessageEntity::class.java)
  }
}
