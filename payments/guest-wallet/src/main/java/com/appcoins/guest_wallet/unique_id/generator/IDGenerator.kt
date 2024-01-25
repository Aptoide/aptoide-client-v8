package com.appcoins.guest_wallet.unique_id.generator

import java.lang.Long.toHexString
import java.util.Calendar
import javax.inject.Inject

class IDGeneratorImpl @Inject constructor() : IDGenerator {
  private companion object {
    private const val CHAR_LIMIT = 37
    private const val CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789"
  }

  override fun generateUniqueID(): String {
    val timestamp = Calendar.getInstance().timeInMillis

    val idBuilder = StringBuilder(CHAR_LIMIT)
    idBuilder.append(toHexString(timestamp))

    // Ensure the ID is always 40 characters long
    while (idBuilder.length < CHAR_LIMIT) {
      idBuilder.append(CHARACTERS.random())
    }

    return "gh_${idBuilder.toString().substring(0, CHAR_LIMIT)}"
  }
}

interface IDGenerator {
  fun generateUniqueID(): String
}
