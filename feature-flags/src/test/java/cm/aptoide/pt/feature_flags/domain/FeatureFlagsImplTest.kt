package cm.aptoide.pt.feature_flags.domain

import cm.aptoide.pt.feature_flags.data.FeatureFlagsLocalRepository
import cm.aptoide.pt.feature_flags.data.FeatureFlagsRepository
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException
import java.util.Optional

@ExperimentalCoroutinesApi
class FeatureFlagsImplTest {

  @Test
  fun `Fetched values win over the defaults and the defaults fill the gaps`() = coScenario {
    m Given "defaults and a remote that answers with one overlapping key"
    val local = FakeLocalRepository()
    val flags = featureFlags(
      remote = { json("show_play_and_earn" to "true", "show_home_tabs" to "true") },
      local = local,
      defaults = json("show_play_and_earn" to "false", "appopen_cap_window_minutes" to "30"),
    )

    m When "the flags are initialized"
    flags.initialize()

    m Then "the remote value overrides the default"
    assertTrue(flags.getFlag("show_play_and_earn", false))
    assertTrue(flags.getFlag("show_home_tabs", false))
    m And "a key only the defaults have keeps its default"
    assertEquals(30, flags.getInt("appopen_cap_window_minutes", 0))
  }

  @Test
  fun `A successful fetch saves only the fetched values as the snapshot`() = coScenario {
    m Given "defaults and a remote that answers"
    val local = FakeLocalRepository()
    val flags = featureFlags(
      remote = { json("show_home_tabs" to "true") },
      local = local,
      defaults = json("appopen_cap_window_minutes" to "30"),
    )

    m When "the flags are initialized"
    flags.initialize()

    m Then "the snapshot holds the fetched values without the defaults"
    assertEquals("true", local.saved?.getString("show_home_tabs"))
    assertFalse(local.saved!!.has("appopen_cap_window_minutes"))
  }

  @Test
  fun `A failed fetch falls back to the snapshot over the defaults`() = coScenario {
    m Given "a snapshot from an earlier run and a remote that fails"
    val local = FakeLocalRepository(json("show_play_and_earn" to "true"))
    val flags = featureFlags(
      remote = { throw IOException("offline") },
      local = local,
      defaults = json("show_play_and_earn" to "false", "appopen_cap_window_minutes" to "30"),
    )

    m When "the flags are initialized"
    flags.initialize()

    m Then "the snapshot value overrides the default"
    assertTrue(flags.getFlag("show_play_and_earn", false))
    m And "the defaults still fill the gaps"
    assertEquals(30, flags.getInt("appopen_cap_window_minutes", 0))
    m And "the snapshot is left as it was"
    assertNull(local.saved)
  }

  @Test
  fun `A failed fetch on the first run leaves the defaults`() = coScenario {
    m Given "no snapshot and a remote that fails"
    val flags = featureFlags(
      remote = { throw IOException("offline") },
      local = FakeLocalRepository(),
      defaults = json("show_play_and_earn" to "false", "appopen_cap_window_minutes" to "30"),
    )

    m When "the flags are initialized"
    flags.initialize()

    m Then "the defaults apply"
    assertEquals(false, flags.getFlag("show_play_and_earn"))
    assertEquals(30, flags.getInt("appopen_cap_window_minutes", 0))
    m And "a key nobody defines falls through to the caller default"
    assertTrue(flags.getFlag("unknown_key", true))
  }

  @Test
  fun `Without a defaults source the flags are the fetched values`() = coScenario {
    m Given "no defaults source and a remote that answers"
    val flags = FeatureFlagsImpl(
      settingsRepository = FakeRepository { json("show_home_tabs" to "true") },
      settingsLocalRepository = FakeLocalRepository(),
      defaults = Optional.empty(),
    )

    m When "the flags are initialized"
    flags.initialize()

    m Then "the fetched value is served"
    assertTrue(flags.getFlag("show_home_tabs", false))
  }

  @Test
  fun `With a snapshot the flags are readable while the fetch is still running`() =
    coScenario { scope ->
      m Given "a snapshot from an earlier run and a fetch that has not answered yet"
      val answer = CompletableDeferred<JSONObject>()
      val local = FakeLocalRepository(json("show_home_tabs" to "true", "home_tabs" to "old"))
      val flags = featureFlags(
        remote = { answer.await() },
        local = local,
        defaults = json("appopen_cap_window_minutes" to "30"),
      )

      m When "the flags are initialized and read before the fetch lands"
      val initialization = scope.launch { flags.initialize() }
      val earlyRead = scope.async { flags.getFlagAsString("home_tabs") }
      scope.runCurrent()

      m Then "the read does not wait and gets the snapshot over the defaults"
      assertTrue(earlyRead.isCompleted)
      assertEquals("old", earlyRead.await())
      assertEquals(30, flags.getInt("appopen_cap_window_minutes", 0))

      m And "the fetched values take over once they arrive, and become the snapshot"
      answer.complete(json("show_home_tabs" to "true", "home_tabs" to "new"))
      initialization.join()
      assertEquals("new", flags.getFlagAsString("home_tabs"))
      assertEquals("new", local.saved?.getString("home_tabs"))
    }

  @Test
  fun `Without a snapshot a read waits for the first fetch`() = coScenario { scope ->
    m Given "a first run and a fetch that has not answered yet"
    val answer = CompletableDeferred<JSONObject>()
    val flags = featureFlags(
      remote = { answer.await() },
      local = FakeLocalRepository(),
      defaults = json("show_home_tabs" to "false"),
    )

    m When "the flags are initialized and read before the fetch lands"
    val initialization = scope.launch { flags.initialize() }
    scope.runCurrent()
    val earlyRead = scope.async { flags.getFlag("show_home_tabs", false) }
    scope.runCurrent()

    m Then "the read is held instead of answering with the defaults"
    assertFalse(earlyRead.isCompleted)

    m And "it gets the fetched value once the fetch lands"
    answer.complete(json("show_home_tabs" to "true"))
    initialization.join()
    assertTrue(earlyRead.await())
  }

  @Test
  fun `A read made before the initialization starts waits for it`() = coScenario { scope ->
    m Given "flags that have not been initialized yet, with a snapshot on disk"
    val flags = featureFlags(
      remote = { json("show_play_and_earn" to "true") },
      local = FakeLocalRepository(json("show_play_and_earn" to "true")),
      defaults = json("show_play_and_earn" to "false"),
    )

    m When "a reader asks first and the initialization starts afterwards"
    val earlyRead = scope.async { flags.getFlag("show_play_and_earn", false) }
    scope.runCurrent()
    val answeredBeforeInitialization = earlyRead.isCompleted
    flags.initialize()

    m Then "the reader is held until there are values, instead of getting its fallback"
    assertFalse(answeredBeforeInitialization)
    assertTrue(earlyRead.await())
  }

  @Test
  fun `The snapshot wins over the defaults and the fetch wins over both`() = coScenario {
    m Given "one key defined by the defaults, the snapshot and the remote"
    val flags = featureFlags(
      remote = { json("layered" to "fetched") },
      local = FakeLocalRepository(json("layered" to "snapshot", "only_snapshot" to "stale")),
      defaults = json("layered" to "default"),
    )

    m When "the flags are initialized"
    flags.initialize()

    m Then "the fetched value is served"
    assertEquals("fetched", flags.getFlagAsString("layered"))
    m And "a snapshot key the server no longer carries is gone"
    assertNull(flags.getFlagAsString("only_snapshot"))
  }

  @Test
  fun `Fetched values are served even when saving the snapshot fails`() = coScenario {
    m Given "a first run where the snapshot cannot be written"
    val flags = featureFlags(
      remote = { json("show_home_tabs" to "true") },
      local = FakeLocalRepository(failOnSave = true),
      defaults = json("show_home_tabs" to "false"),
    )

    m When "the flags are initialized"
    flags.initialize()

    m Then "the fetched value still applies for this session"
    assertTrue(flags.getFlag("show_home_tabs", false))
  }

  @Test
  fun `A cancelled fetch cancels the initialization and leaves the flags readable`() =
    coScenario { scope ->
      m Given "a first run whose fetch gets cancelled"
      val flags = featureFlags(
        remote = { throw CancellationException("scope gone") },
        local = FakeLocalRepository(),
        defaults = json("appopen_cap_window_minutes" to "30"),
      )

      m When "the flags are initialized"
      val thrown = runCatching { flags.initialize() }.exceptionOrNull()

      m Then "the cancellation is not swallowed"
      assertTrue(thrown is CancellationException)
      m And "readers are released with the defaults rather than held forever"
      assertEquals(30, flags.getInt("appopen_cap_window_minutes", 0))
    }

  @Test
  fun `A defaults source that fails does not stop the fetched values`() = coScenario {
    m Given "a defaults source that throws and a remote that answers"
    val flags = FeatureFlagsImpl(
      settingsRepository = FakeRepository { json("show_home_tabs" to "true") },
      settingsLocalRepository = FakeLocalRepository(),
      defaults = Optional.of(FeatureFlagsDefaults { throw IllegalStateException("bad xml") }),
    )

    m When "the flags are initialized"
    flags.initialize()

    m Then "the fetched value is served"
    assertTrue(flags.getFlag("show_home_tabs", false))
  }

  private fun featureFlags(
    remote: suspend () -> JSONObject,
    local: FeatureFlagsLocalRepository,
    defaults: JSONObject,
  ) = FeatureFlagsImpl(
    settingsRepository = FakeRepository(remote),
    settingsLocalRepository = local,
    defaults = Optional.of(FeatureFlagsDefaults { defaults }),
  )

  private fun json(vararg pairs: Pair<String, String>) =
    JSONObject().apply { pairs.forEach { put(it.first, it.second) } }

  private class FakeRepository(
    private val answer: suspend () -> JSONObject,
  ) : FeatureFlagsRepository {
    override suspend fun getFeatureFlags(): JSONObject = answer()
  }

  private class FakeLocalRepository(
    private val snapshot: JSONObject = JSONObject(),
    private val failOnSave: Boolean = false,
  ) : FeatureFlagsLocalRepository {
    var saved: JSONObject? = null

    override suspend fun getFeatureFlags(): JSONObject = snapshot
    override suspend fun saveFeatureFlags(featureFlags: JSONObject) {
      if (failOnSave) throw IOException("disk full")
      saved = JSONObject(featureFlags.toString())
    }
  }
}
