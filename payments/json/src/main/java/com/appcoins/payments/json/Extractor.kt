package com.appcoins.payments.json

import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal

// Nullable wrappers

inline fun <reified D : Any> JSONObject.getOrNull(
  key: String,
  getIt: JSONObject.(String) -> D,
): D? = key.takeUnless(::isNull)?.let { getIt(it) }

inline fun <reified D : Any> JSONArray.getOrNull(
  index: Int,
  getIt: JSONArray.(Int) -> D,
): D? = index.takeUnless(::isNull)?.let { getIt(it) }

inline fun <reified D : Any> JSONObject.putNullable(key: String, data: D?): JSONObject =
  put(key, data)

inline fun <reified D : Any> JSONArray.putNullable(data: D?): JSONArray = put(data)

// Any

fun JSONObject.getAnyOrNull(key: String): Any? = getOrNull(key) { get(key) }

fun JSONArray.getAnyOrNull(index: Int): Any? = getOrNull(index) { get(index) }

// BigDecimal

fun JSONObject.getBigDecimal(key: String): BigDecimal = getString(key).toBigDecimal()

fun JSONArray.getBigDecimal(index: Int): BigDecimal = getString(index).toBigDecimal()

fun JSONObject.getBigDecimalOrNull(key: String): BigDecimal? =
  getOrNull(key) { getBigDecimal(key) }

fun JSONArray.getBigDecimalOrNull(index: Int): BigDecimal? =
  getOrNull(index) { getBigDecimal(index) }

// TODO: find a way to save it unquoted
fun JSONObject.putNullable(key: String, data: BigDecimal?): JSONObject =
  putNullable(key, data?.toString())

// TODO: find a way to save it unquoted
fun JSONArray.putNullable(data: BigDecimal?): JSONArray =
  putNullable(data?.toString())

// Boolean

fun JSONObject.getBooleanOrNull(key: String): Boolean? = getOrNull(key) { getBoolean(key) }

fun JSONArray.getBooleanOrNull(index: Int): Boolean? = getOrNull(index) { getBoolean(index) }

// Byte

fun JSONObject.getByte(key: String): Byte = getInt(key)
  .takeIf { it in Byte.MIN_VALUE..Byte.MAX_VALUE }
  ?.toByte()
  ?: throw IllegalArgumentException("Out of Byte range")

fun JSONArray.getByte(index: Int): Byte = getInt(index)
  .takeIf { it in Byte.MIN_VALUE..Byte.MAX_VALUE }
  ?.toByte()
  ?: throw IllegalArgumentException("Out of Byte range")

fun JSONObject.getByteOrNull(key: String): Byte? = getOrNull(key) { getByte(key) }

fun JSONArray.getByteOrNull(index: Int): Byte? = getOrNull(index) { getByte(index) }

// Double

fun JSONObject.getDoubleOrNull(key: String): Double? = getOrNull(key) { getDouble(key) }

fun JSONArray.getDoubleOrNull(index: Int): Double? = getOrNull(index) { getDouble(index) }

// Float

fun JSONObject.getFloat(key: String): Float = getDouble(key)
  .takeIf { it in Float.MIN_VALUE..Float.MAX_VALUE }
  ?.toFloat()
  ?: throw IllegalArgumentException("Out of Byte range")

fun JSONArray.getFloat(index: Int): Float = getDouble(index)
  .takeIf { it in Float.MIN_VALUE..Float.MAX_VALUE }
  ?.toFloat()
  ?: throw IllegalArgumentException("Out of Byte range")

fun JSONObject.getFloatOrNull(key: String): Float? = getOrNull(key) { getFloat(key) }

fun JSONArray.getFloatOrNull(index: Int): Float? = getOrNull(index) { getFloat(index) }

// Int

fun JSONObject.getIntOrNull(key: String): Int? = getOrNull(key) { getInt(key) }

fun JSONArray.getIntOrNull(index: Int): Int? = getOrNull(index) { getInt(index) }

// JSONArray

fun JSONObject.getJSONArrayOrNull(key: String): JSONArray? =
  getOrNull(key) { getJSONArray(key) }

fun JSONArray.getJSONArrayOrNull(index: Int): JSONArray? =
  getOrNull(index) { getJSONArray(index) }

// JSONObject

fun JSONObject.getJSONObjectOrNull(key: String): JSONObject? =
  getOrNull(key) { getJSONObject(key) }

fun JSONArray.getJSONObjectOrNull(index: Int): JSONObject? =
  getOrNull(index) { getJSONObject(index) }

// Long

fun JSONObject.getLongOrNull(key: String): Long? = getOrNull(key) { getLong(key) }

fun JSONArray.getLongOrNull(index: Int): Long? = getOrNull(index) { getLong(index) }

// Short

fun JSONObject.getShort(key: String): Short = getInt(key)
  .takeIf { it in Short.MIN_VALUE..Short.MAX_VALUE }
  ?.toShort()
  ?: throw IllegalArgumentException("Out of Byte range")

fun JSONArray.getShort(index: Int): Short = getInt(index)
  .takeIf { it in Short.MIN_VALUE..Short.MAX_VALUE }
  ?.toShort()
  ?: throw IllegalArgumentException("Out of Byte range")

fun JSONObject.getShortOrNull(key: String): Short? = getOrNull(key) { getShort(key) }

fun JSONArray.getShortOrNull(index: Int): Short? = getOrNull(index) { getShort(index) }

// String

fun JSONObject.getStringOrNull(key: String): String? = getOrNull(key) { getString(key) }

fun JSONArray.getStringOrNull(index: Int): String? = getOrNull(index) { getString(index) }

// Lst

inline fun <reified D> JSONObject.getList(
  key: String,
  getIt: JSONArray.(Int) -> D,
): List<D> = getJSONArray(key).toList(getIt)

inline fun <reified D> JSONArray.getList(
  index: Int,
  getIt: JSONArray.(Int) -> D,
): List<D> = getJSONArray(index).toList(getIt)

inline fun <reified D> JSONObject.getListOrNull(
  key: String,
  getIt: JSONArray.(Int) -> D,
): List<D>? = getJSONArrayOrNull(key)?.toList(getIt)

inline fun <reified D> JSONArray.getListOrNull(
  index: Int,
  getIt: JSONArray.(Int) -> D,
): List<D>? = getJSONArrayOrNull(index)?.toList(getIt)

inline fun <reified D> JSONObject.putNullable(
  key: String,
  data: List<D>?,
  putIt: JSONArray.(D) -> JSONArray,
): JSONObject = putNullable(key, data?.toJSONArray(putIt))

inline fun <reified D> JSONArray.putNullable(
  data: List<D>?,
  putIt: JSONArray.(D) -> JSONArray,
): JSONArray = putNullable(data?.toJSONArray(putIt))

// Map

inline fun <reified D> JSONObject.getMap(
  key: String,
  getIt: JSONObject.(String) -> D,
): Map<String, D> = getJSONObject(key).toMap(getIt)

inline fun <reified D> JSONArray.getMap(
  index: Int,
  getIt: JSONObject.(String) -> D,
): Map<String, D> = getJSONObject(index).toMap(getIt)

inline fun <reified D> JSONObject.getMapOrNull(
  key: String,
  getIt: JSONObject.(String) -> D,
): Map<String, D>? = getJSONObjectOrNull(key)?.toMap(getIt)

inline fun <reified D> JSONArray.getMapOrNull(
  index: Int,
  getIt: JSONObject.(String) -> D,
): Map<String, D>? = getJSONObjectOrNull(index)?.toMap(getIt)

inline fun <reified D> JSONObject.putNullable(
  key: String,
  data: Map<String, D>?,
  putIt: JSONObject.(String, D) -> JSONObject,
): JSONObject = putNullable(key, data?.toJSONObject(putIt))

inline fun <reified D> JSONArray.putNullable(
  data: Map<String, D>?,
  putIt: JSONObject.(String, D) -> JSONObject,
): JSONArray = putNullable(data?.toJSONObject(putIt))
