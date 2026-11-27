package com.aptoide.android.aptoidegames.play_and_earn.data

import cm.aptoide.pt.test.gherkin.coScenario
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PaERewardType
import kotlinx.coroutines.flow.first
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

// AND-952: one pending redeem outlives the screen, so a redeem whose answer was lost is replayed
// with its first id, not sent again as a new one.
internal class PaEPendingRedeemsTest {

  private val dataStore = InMemoryPreferencesDataStore()
  private var now = 1_000_000L
  private var idCount = 0

  private fun store() = PaEPendingRedeems(
    dataStore = dataStore,
    nowMillis = { now },
    newRequestId = { "id-${++idCount}" },
  )

  @Test
  fun `The same redeem soon after gets the pending id`() = coScenario {
    m Given "a pending redeem"
    val store = store()
    val first = store.idFor(EMAIL, PaERewardType.ROBUX)

    m When "the same redeem asks for an id nine minutes later"
    now += TimeUnit.MINUTES.toMillis(9)
    val second = store.idFor(EMAIL, PaERewardType.ROBUX)

    m Then "it is the same id"
    assertEquals("id-1", first)
    assertEquals("id-1", second)
  }

  @Test
  fun `The same redeem after ten minutes gets a new id`() = coScenario {
    m Given "a pending redeem"
    val store = store()
    store.idFor(EMAIL, PaERewardType.ROBUX)

    m When "the same redeem asks for an id ten minutes later"
    now += TimeUnit.MINUTES.toMillis(10)
    val id = store.idFor(EMAIL, PaERewardType.ROBUX)

    m Then "it is a new redeem"
    assertEquals("id-2", id)
  }

  @Test
  fun `Another email or reward gets a new id`() = coScenario {
    m Given "a pending redeem"
    val store = store()
    store.idFor(EMAIL, PaERewardType.ROBUX)

    m When "another email, then another reward, ask for an id"
    val otherEmail = store.idFor("other@example.com", PaERewardType.ROBUX)
    val otherReward = store.idFor("other@example.com", PaERewardType.DIAMONDS)

    m Then "each is a new redeem"
    assertEquals("id-2", otherEmail)
    assertEquals("id-3", otherReward)
  }

  @Test
  fun `Case and surrounding spaces do not make another redeem`() = coScenario {
    m Given "a pending redeem for a lowercase email"
    val store = store()
    store.idFor("a@x.com", PaERewardType.ROBUX)

    m When "the same email is typed with capitals and spaces"
    val id = store.idFor(" A@x.com ", PaERewardType.ROBUX)

    m Then "it is the same redeem"
    assertEquals("id-1", id)
  }

  @Test
  fun `The email is not stored`() = coScenario {
    m Given "a pending redeem"
    store().idFor(EMAIL, PaERewardType.ROBUX)

    m When "the stored preferences are read"
    val stored = dataStore.data.first().asMap().values.map { it.toString() }

    m Then "no value holds the email"
    assertFalse(stored.any { it.contains("example.com") }, stored.toString())
  }

  @Test
  fun `Clearing the pending id makes the next redeem new`() = coScenario {
    m Given "a pending redeem"
    val store = store()
    val id = store.idFor(EMAIL, PaERewardType.ROBUX)

    m When "it is cleared and the same redeem asks again"
    store.clear(id)
    val next = store.idFor(EMAIL, PaERewardType.ROBUX)

    m Then "the id is new"
    assertEquals("id-2", next)
  }

  @Test
  fun `Clearing a stale id leaves the pending one`() = coScenario {
    m Given "a pending redeem"
    val store = store()
    store.idFor(EMAIL, PaERewardType.ROBUX)

    m When "an older id is cleared"
    store.clear("id-0")
    val id = store.idFor(EMAIL, PaERewardType.ROBUX)

    m Then "the pending id is still there"
    assertEquals("id-1", id)
  }

  @Test
  fun `Clearing all forgets the pending redeem`() = coScenario {
    m Given "a pending redeem"
    val store = store()
    store.idFor(EMAIL, PaERewardType.ROBUX)

    m When "the account changes"
    store.clearAll()
    val id = store.idFor(EMAIL, PaERewardType.ROBUX)

    m Then "the same redeem is new"
    assertEquals("id-2", id)
  }

  @Test
  fun `The pending id survives a re-created store`() = coScenario {
    m Given "a pending redeem"
    store().idFor(EMAIL, PaERewardType.ROBUX)

    m When "the process dies and a new store reads the same preferences"
    val id = store().idFor(EMAIL, PaERewardType.ROBUX)

    m Then "the pending id is replayed"
    assertEquals("id-1", id)
  }

  private companion object {
    const val EMAIL = "user@example.com"
  }
}
