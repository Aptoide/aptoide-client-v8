package com.aptoide.android.aptoidegames.feature_flags

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

@ExperimentalCoroutinesApi
class ConfigBucketIdStoreTest {

  @TempDir
  lateinit var tempDir: File

  @Test
  fun `The first read generates an id the endpoint accepts`() = coScenario { scope ->
    m Given "an install that never fetched the config"
    val store = ConfigBucketIdStore(dataStore(scope))

    m When "the bucket id is read"
    val id = store.get()

    m Then "it fits the charset and length the endpoint validates"
    assertTrue(Regex("^[A-Za-z0-9._-]{1,64}$").matches(id), id)
  }

  @Test
  fun `The id never changes once generated`() = coScenario { scope ->
    m Given "an install that already has a bucket id"
    val dataStore = dataStore(scope)
    val first = ConfigBucketIdStore(dataStore).get()

    m When "it is read again, also by a new store instance as after a restart"
    val again = ConfigBucketIdStore(dataStore).get()
    val concurrent = List(8) { scope.async { ConfigBucketIdStore(dataStore).get() } }.awaitAll()

    m Then "every read returns the same id"
    assertEquals(first, again)
    assertEquals(setOf(first), concurrent.toSet())
  }

  @Test
  fun `The id lives next to the flags snapshot without touching it`() = coScenario { scope ->
    m Given "a data store that already holds a flags snapshot"
    val snapshotKey = stringPreferencesKey("featureFlags")
    val dataStore = dataStore(scope)
    dataStore.edit { it[snapshotKey] = """{"show_home_tabs":"true"}""" }

    m When "the bucket id is generated"
    val id = ConfigBucketIdStore(dataStore).get()

    m Then "the snapshot is intact and the id is stored beside it"
    val stored = dataStore.data.first()
    assertEquals("""{"show_home_tabs":"true"}""", stored[snapshotKey])
    assertEquals(id, stored[stringPreferencesKey("bucketId")])
  }

  private fun dataStore(scope: TestScope): DataStore<Preferences> =
    PreferenceDataStoreFactory.create(
      scope = CoroutineScope(scope.backgroundScope.coroutineContext),
      produceFile = { File(tempDir, "userFeatureFlags.preferences_pb") },
    )
}
