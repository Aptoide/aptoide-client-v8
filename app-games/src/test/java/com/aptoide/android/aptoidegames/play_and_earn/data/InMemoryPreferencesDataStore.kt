package com.aptoide.android.aptoidegames.play_and_earn.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** A preferences store kept in memory: what a file would hold across re-created readers. */
internal class InMemoryPreferencesDataStore : DataStore<Preferences> {
  private val state = MutableStateFlow(emptyPreferences())

  override val data: Flow<Preferences> = state

  override suspend fun updateData(
    transform: suspend (t: Preferences) -> Preferences
  ): Preferences = transform(state.value).also { state.value = it }
}
