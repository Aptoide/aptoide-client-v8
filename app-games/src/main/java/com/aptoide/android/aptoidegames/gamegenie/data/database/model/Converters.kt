package com.aptoide.android.aptoidegames.gamegenie.data.database.model

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

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
}
