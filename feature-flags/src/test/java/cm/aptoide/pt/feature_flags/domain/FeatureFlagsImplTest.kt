package cm.aptoide.pt.feature_flags.domain

import cm.aptoide.pt.feature_flags.data.FeatureFlagsLocalRepository
import cm.aptoide.pt.feature_flags.data.FeatureFlagsRepository
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

  private fun featureFlags(
    remote: () -> JSONObject,
    local: FeatureFlagsLocalRepository,
    defaults: JSONObject,
  ) = FeatureFlagsImpl(
    settingsRepository = FakeRepository(remote),
    settingsLocalRepository = local,
    defaults = Optional.of(FeatureFlagsDefaults { defaults }),
  )

  private fun json(vararg pairs: Pair<String, String>) =
    JSONObject().apply { pairs.forEach { put(it.first, it.second) } }

  private class FakeRepository(private val answer: () -> JSONObject) : FeatureFlagsRepository {
    override suspend fun getFeatureFlags(): JSONObject = answer()
  }

  private class FakeLocalRepository(
    private val snapshot: JSONObject = JSONObject(),
  ) : FeatureFlagsLocalRepository {
    var saved: JSONObject? = null

    override suspend fun getFeatureFlags(): JSONObject = snapshot
    override suspend fun saveFeatureFlags(featureFlags: JSONObject) {
      saved = JSONObject(featureFlags.toString())
    }
  }
}
