package com.aptoide.android.aptoidegames

import kotlinx.coroutines.flow.Flow

interface IdsRepository {

  suspend fun getId(key: String): String

  suspend fun saveId(key: String, id: String)

  fun observeId(key: String): Flow<String>
}
