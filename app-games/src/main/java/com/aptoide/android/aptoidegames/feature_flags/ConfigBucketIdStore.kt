package com.aptoide.android.aptoidegames.feature_flags

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import java.util.UUID

/**
 * The install's identity for percentage rollouts, sent as `bucket_id` on every config fetch.
 * Generated once and kept next to the flags snapshot; regenerating it would move the install to
 * other buckets, so it is never replaced.
 */
class ConfigBucketIdStore(
  private val dataStore: DataStore<Preferences>,
) {

  // The edit block is transactional, so concurrent first reads agree on a single id
  suspend fun get(): String = dataStore.edit { preferences ->
    if (preferences[BUCKET_ID].isNullOrBlank()) {
      preferences[BUCKET_ID] = UUID.randomUUID().toString()
    }
  }[BUCKET_ID]!!

  private companion object {
    val BUCKET_ID = stringPreferencesKey("bucketId")
  }
}
