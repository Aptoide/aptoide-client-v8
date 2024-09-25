package com.aptoide.android.aptoidegames

interface IdsRepository {

  suspend fun getId(key: String): String

  suspend fun saveId(key: String, id: String)
}
