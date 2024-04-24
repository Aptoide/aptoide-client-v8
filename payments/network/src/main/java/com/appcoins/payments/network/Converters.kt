package com.appcoins.payments.network

fun ByteArray.toJsonString(): String? = takeIf(ByteArray::isNotEmpty)?.toString(Charsets.UTF_8)

fun String.toBytes(): ByteArray = toByteArray(Charsets.UTF_8)
