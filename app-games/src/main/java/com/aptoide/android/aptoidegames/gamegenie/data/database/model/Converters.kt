package com.aptoide.android.aptoidegames.gamegenie.data.database.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {
  private val gson: Gson = GsonBuilder()
    .registerTypeAdapter(ChatInteractionEntity::class.java, ChatInteractionDeserializer())
    .registerTypeAdapter(UserMessageEntity::class.java, UserMessageDeserializer())
    .create()

  @TypeConverter
  fun fromChatInteractionList(value: List<ChatInteractionEntity>): String {
    return gson.toJson(value)
  }

  @TypeConverter
  fun toChatInteractionList(value: String): List<ChatInteractionEntity> {
    if (value.isEmpty() || value == "[]") return emptyList()
    return try {
      val type = object : TypeToken<List<ChatInteractionEntity>>() {}.type
      gson.fromJson(value, type)
    } catch (e: Exception) {
      emptyList()
    }
  }

  private class ChatInteractionDeserializer : JsonDeserializer<ChatInteractionEntity> {
    override fun deserialize(
      json: JsonElement,
      typeOfT: Type,
      context: JsonDeserializationContext
    ): ChatInteractionEntity {
      val jsonObject = json.asJsonObject

      val gpt = jsonObject.get("gpt")?.asString ?: ""
      val videoId = jsonObject.get("videoId")?.takeIf { !it.isJsonNull }?.asString
      val apps = jsonObject.get("apps")?.asString ?: "[]"

      val userElement = jsonObject.get("user")
      val user = when {
        userElement == null || userElement.isJsonNull -> null
        userElement.isJsonPrimitive && userElement.asJsonPrimitive.isString -> {
          UserMessageEntity(text = userElement.asString)
        }
        userElement.isJsonObject -> {
          context.deserialize(userElement, UserMessageEntity::class.java)
        }
        else -> null
      }

      return ChatInteractionEntity(
        gpt = gpt,
        user = user,
        videoId = videoId,
        apps = apps
      )
    }
  }
  
  private class UserMessageDeserializer : JsonDeserializer<UserMessageEntity> {
    override fun deserialize(
      json: JsonElement,
      typeOfT: Type,
      context: JsonDeserializationContext
    ): UserMessageEntity {
      return when {
        json.isJsonPrimitive && json.asJsonPrimitive.isString -> {
          UserMessageEntity(text = json.asString)
        }
        json.isJsonObject -> {
          val obj = json.asJsonObject
          UserMessageEntity(
            text = obj.get("text")?.asString ?: "",
            image = obj.get("image")?.takeIf { !it.isJsonNull }?.asString
          )
        }
        else -> UserMessageEntity(text = "")
      }
    }
  }
}
