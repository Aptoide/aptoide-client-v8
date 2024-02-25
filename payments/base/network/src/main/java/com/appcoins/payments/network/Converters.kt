package com.appcoins.payments.network

import com.google.gson.Gson

fun ByteArray.toJsonString(): String? = takeIf(ByteArray::isNotEmpty)?.toString(Charsets.UTF_8)

fun String.toBytes(): ByteArray = toByteArray(Charsets.UTF_8)

fun <T : Any> String?.fromJson(type: Class<T>): T =
  if (type == Unit::class.java) {
    @Suppress("UNCHECKED_CAST")
    Unit as T
  } else {
    Gson().fromJson(this, type)
  }

fun <T : Any> T.toJson(): String? = Gson().toJson(this)
