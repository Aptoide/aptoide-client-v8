package com.appcoins.payments.json

import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.math.BigDecimal

// Any

fun String.jsonToAny(): Any? = JSONTokener(this).nextValue().takeUnless { it == JSONObject.NULL }

fun Any?.toJsonString(): String = this?.let { "{}" } ?: "null"

// BigDecimal

fun String.jsonToBigDecimal(): BigDecimal? = jsonToString()?.toBigDecimal()

fun BigDecimal?.toJsonString(): String = this?.toString() ?: "null"

// Boolean

fun String.jsonToBoolean(): Boolean? = JSONTokener(this)
  .nextValue()
  .takeUnless { it == JSONObject.NULL }
  ?.let { it as Boolean }

fun Boolean?.toJsonString(): String = this?.toString() ?: "null"

// Byte

fun String.jsonToByte(): Byte? = jsonToInt()
  ?.let {
    if (it !in Byte.MIN_VALUE..Byte.MAX_VALUE) {
      throw IllegalArgumentException("Out of Byte range")
    }
    it
  }
  ?.toByte()

fun Byte?.toJsonString(): String = this?.toInt()?.toString() ?: "null"

// Double

fun String.jsonToDouble(): Double? = JSONTokener(this)
  .nextValue()
  ?.takeUnless { it == JSONObject.NULL }
  ?.let { it as Double }

fun Double?.toJsonString(): String = this?.toString() ?: "null"

// Float

fun String.jsonToFloat(): Float? = this.jsonToDouble()
  ?.let {
    if (it !in Float.MIN_VALUE..Float.MAX_VALUE) {
      throw IllegalArgumentException("Out of Float range")
    }
    it
  }
  ?.toFloat()

fun Float?.toJsonString(): String = this?.toInt()?.toString() ?: "null"

// Int

fun String.jsonToInt(): Int? = JSONTokener(this).nextValue()
  .takeUnless { it == JSONObject.NULL }
  ?.let { it as Int }

fun Int?.toJsonString(): String = this?.toString() ?: "null"

// JSONArray

fun String.jsonToJSONArray(): JSONArray? = JSONTokener(this).nextValue()
  .takeUnless { it == JSONObject.NULL }
  ?.let { it as JSONArray }

// JSONObject

fun String.jsonToJSONObject(): JSONObject? = JSONTokener(this).nextValue()
  .takeUnless { it == JSONObject.NULL }
  ?.let { it as JSONObject }

// Long

fun String.jsonToLong(): Long? = JSONTokener(this).nextValue()
  .takeUnless { it == JSONObject.NULL }
  ?.let { it as Long }

fun Long?.toJsonString(): String = this?.toString() ?: "null"

// Short

fun String.jsonToShort(): Short? = this.jsonToInt()
  ?.let {
    if (it !in Short.MIN_VALUE..Short.MAX_VALUE) {
      throw IllegalArgumentException("Out of Byte range")
    }
    it
  }
  ?.toShort()

fun Short?.toJsonString(): String = this?.toInt()?.toString() ?: "null"

// String

fun String.jsonToString(): String? = JSONTokener(this)
  .nextValue()
  .takeUnless { it == JSONObject.NULL }
  ?.let { it as String }

fun String?.toJsonString(): String = this?.let { "\"$it\"" } ?: "null"

// Lst

inline fun <reified D> JSONArray.toList(getIt: JSONArray.(Int) -> D): List<D> =
  List(length()) { getIt(it) }

inline fun <reified D> List<D>.toJSONArray(putIt: JSONArray.(D) -> JSONArray): JSONArray =
  fold(JSONArray(), putIt)

inline fun <reified D> String.jsonToList(getIt: JSONArray.(Int) -> D): List<D>? = jsonToJSONArray()
  ?.toList(getIt)

inline fun <reified D> List<D>?.toJsonString(putIt: JSONArray.(D) -> JSONArray): String =
  this?.toJSONArray(putIt)?.toString() ?: "null"

// Map

inline fun <reified D> JSONObject.toMap(getIt: JSONObject.(String) -> D): Map<String, D> = keys()
  .asSequence()
  .toList()
  .associateWith { getIt(it) }

inline fun <reified D> Map<String, D>.toJSONObject(putIt: JSONObject.(String, D) -> JSONObject): JSONObject =
  entries.fold(JSONObject()) { acc, (key, value) -> acc.putIt(key, value) }

inline fun <reified D> String.jsonToMap(getIt: JSONObject.(String) -> D): Map<String, D>? =
  jsonToJSONObject()?.toMap(getIt)

inline fun <reified D> Map<String, D>?.toJsonString(putIt: JSONObject.(String, D) -> JSONObject): String =
  this?.toJSONObject(putIt)?.toString() ?: "null"
