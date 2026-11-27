package com.aptoide.android.aptoidegames.play_and_earn.data

import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.flow.first
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

// AND-953: a refused sign-in bonus is not offered or asked for again for a day.
internal class PaESignInBonusRefusalTest {

  private val dataStore = InMemoryPreferencesDataStore()
  private var now = 1_000_000L

  private fun store() = PaESignInBonusRefusal(dataStore = dataStore, nowMillis = { now })

  @Test
  fun `Nothing is refused at first`() = coScenario {
    m Given "a fresh store"
    val store = store()

    m When "it is asked whether the bonus is refused"
    val refused = store.isRefused()

    m Then "it is not"
    assertFalse(refused)
  }

  @Test
  fun `A refusal holds for 23 hours 59 minutes`() = coScenario {
    m Given "a bonus refused now"
    val store = store()
    store.record()

    m When "23 hours 59 minutes pass"
    now += TimeUnit.HOURS.toMillis(24) - TimeUnit.MINUTES.toMillis(1)

    m Then "the bonus is still refused"
    assertTrue(store.isRefused())
  }

  @Test
  fun `A refusal ends after 24 hours`() = coScenario {
    m Given "a bonus refused now"
    val store = store()
    store.record()

    m When "24 hours pass"
    now += TimeUnit.HOURS.toMillis(24)

    m Then "the bonus may be offered again"
    assertFalse(store.isRefused())
  }

  @Test
  fun `A refusal dated in the future does not hold`() = coScenario {
    m Given "a bonus refused now"
    val store = store()
    store.record()

    m When "the clock is set back a minute"
    now -= TimeUnit.MINUTES.toMillis(1)

    m Then "the refusal does not hold, so a wrong clock cannot hide the bonus for longer"
    assertFalse(store.isRefused())
  }

  @Test
  fun `Clearing forgets the refusal`() = coScenario {
    m Given "a bonus refused now"
    val store = store()
    store.record()

    m When "the account changes"
    store.clear()

    m Then "the bonus is not refused"
    assertFalse(store.isRefused())
  }

  @Test
  fun `The refusal survives a re-created store`() = coScenario {
    m Given "a bonus refused now"
    store().record()

    m When "the process dies and a new store reads the same preferences"
    val refused = store().isRefused()

    m Then "the bonus is still refused"
    assertTrue(refused)
  }

  @Test
  fun `Only the time of the refusal is stored`() = coScenario {
    m Given "a bonus refused now"
    store().record()

    m When "the stored preferences are read"
    val stored = dataStore.data.first().asMap()

    m Then "one value is stored: when it was refused"
    assertEquals(listOf<Any>(now), stored.values.toList())
  }

  @Test
  fun `The refusal time is observable`() = coScenario {
    m Given "a bonus refused now"
    val store = store()
    store.record()

    m When "the refusal is observed, then cleared"
    val recorded = store.refusedAt.first()
    store.clear()
    val cleared = store.refusedAt.first()

    m Then "it shows the time, then nothing"
    assertEquals(now, recorded)
    assertEquals(null, cleared)
  }
}
