package com.aptoide.android.aptoidegames.device_info

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

internal class AGPaEDeviceIdentityTest {

  @TempDir
  lateinit var dir: File

  private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

  @AfterEach
  fun tearDown() = scope.cancel()

  private fun newDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.create(
      scope = scope,
      produceFile = { File(dir, "pae_test.preferences_pb") }
    )

  private fun identity(
    androidId: String?,
    dataStore: DataStore<Preferences>,
    newInstallId: () -> String = { java.util.UUID.randomUUID().toString() },
  ) = AGPaEDeviceIdentity(
    androidIdSource = { androidId },
    installIdStore = DataStoreInstallIdStore(dataStore),
    newInstallId = newInstallId,
  )

  @Test
  fun `sha256Hex matches the NIST test vector`() {
    assertEquals(
      "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
      sha256Hex("abc")
    )
  }

  @Test
  fun `device key is the lower-case SHA-256 hex of the Android ID`() = runBlocking {
    val key = identity(androidId = "9774d56d682e549c", dataStore = newDataStore()).getDeviceKey()

    assertEquals("4d853cd5e7d688e75b3dca9abc2fa52ac8bafb750b6d4dc01b78e2e7bb335f8f", key)
  }

  @Test
  fun `device key is null when the Android ID is null or blank`() = runBlocking {
    val dataStore = newDataStore()

    assertNull(identity(androidId = null, dataStore = dataStore).getDeviceKey())
    assertNull(identity(androidId = "  ", dataStore = dataStore).getDeviceKey())
  }

  @Test
  fun `device key is null when reading the Android ID throws`() = runBlocking {
    val identity = AGPaEDeviceIdentity(
      androidIdSource = { throw SecurityException("no access") },
      installIdStore = DataStoreInstallIdStore(newDataStore()),
    )

    assertNull(identity.getDeviceKey())
  }

  @Test
  fun `an empty store gets a new install id, which is then persisted`() = runBlocking {
    val dataStore = newDataStore()

    val installId = identity(androidId = "a", dataStore = dataStore) { "fresh-uuid" }.getInstallId()

    assertEquals("fresh-uuid", installId)
    assertEquals("fresh-uuid", DataStoreInstallIdStore(dataStore).read())
  }

  @Test
  fun `the install id is stable across app starts`() = runBlocking {
    val dataStore = newDataStore()
    val firstRun = identity(androidId = "a", dataStore = dataStore).getInstallId()

    val generated = AtomicInteger()
    val secondRun = identity(androidId = "a", dataStore = dataStore) {
      generated.incrementAndGet()
      "should-not-be-used"
    }.getInstallId()

    assertEquals(firstRun, secondRun)
    assertEquals(0, generated.get())
  }

  @Test
  fun `an id already in the store is returned as is`() = runBlocking {
    val dataStore = newDataStore()
    dataStore.edit { it[stringPreferencesKey("pae_install_id")] = "stored-id" }

    assertEquals("stored-id", identity(androidId = "a", dataStore = dataStore).getInstallId())
  }

  @Test
  fun `concurrent first reads create the install id exactly once`() = runBlocking {
    val generated = AtomicInteger()
    val dataStore = newDataStore()
    // Two "callers" on separate identity instances share the store, as two threads would.
    val identities = List(2) {
      identity(androidId = "a", dataStore = dataStore) {
        "uuid-${generated.incrementAndGet()}"
      }
    }

    val ids = (1..50).map { i ->
      async(Dispatchers.Default) { identities[i % 2].getInstallId() }
    }.awaitAll()

    assertEquals(1, ids.toSet().size)
    assertEquals(1, generated.get())
  }

  @Test
  fun `the default install id is a random UUID`() = runBlocking {
    val installId = AGPaEDeviceIdentity(
      androidIdSource = { "a" },
      installIdStore = DataStoreInstallIdStore(newDataStore()),
    ).getInstallId()

    assertTrue(
      Regex("^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")
        .matches(installId)
    )
  }
}
