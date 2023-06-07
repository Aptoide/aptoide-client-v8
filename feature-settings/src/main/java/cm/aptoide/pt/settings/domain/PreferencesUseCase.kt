package cm.aptoide.pt.settings.domain

import kotlinx.coroutines.flow.Flow

interface FlagPreferencesUseCase {

  suspend fun set(flag: Boolean?)

  fun get(): Flow<Boolean?>
}

interface StringPreferencesUseCase {

  suspend fun set(str: String)

  fun get(): Flow<String>
}

interface IntPreferencesUseCase {

  suspend fun set(value: Int)

  fun get(): Flow<Int>
}
