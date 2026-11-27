package com.aptoide.android.aptoidegames.play_and_earn.data

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import cm.aptoide.pt.test.gherkin.coScenario
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEPendingInstall.Click
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.concurrent.TimeUnit

// AND-954: the installs whose click was sent and whose report is still due, kept on disk so the
// report survives the screen being closed and the app being killed.
@OptIn(ExperimentalCoroutinesApi::class)
internal class PaEPendingInstallsTest {

  private val dataStore = InMemoryPreferencesDataStore()
  private var now = 1_000_000_000L
  private var nextId = 0

  private fun store() = PaEPendingInstalls(
    dataStore = dataStore,
    nowMillis = { now },
    newClickId = { "click-${++nextId}" },
  )

  @Test
  fun `A started install is kept with a new click id`() = coScenario {
    m Given "no pending install"
    val store = store()

    m When "the install of a game starts"
    val install = store.startOrReuse("com.game", OWNER)

    m Then "it is kept, with a new id and its time, and a click not sent yet"
    assertEquals(PaEPendingInstall("com.game", "click-1", now, Click.NOT_SENT, OWNER), install)
    assertEquals(listOf(install), store().all())
  }

  @Test
  fun `The same game started again keeps its click id`() = coScenario {
    m Given "a pending install of the game whose click was recorded"
    store().startOrReuse("com.game", OWNER)
    store().setClick("click-1", Click.RECORDED)

    m When "the same game's install starts again, e.g. after a cancel or from another screen"
    now += TimeUnit.HOURS.toMillis(1)
    val install = store().startOrReuse("com.game", OWNER)

    m Then "it is the same install, with the same id: the backend counts it once"
    assertEquals(PaEPendingInstall("com.game", "click-1", 1_000_000_000L, Click.RECORDED, OWNER), install)
  }

  @Test
  fun `Another game gets its own click id`() = coScenario {
    m Given "a pending install of a game"
    store().startOrReuse("com.game", OWNER)

    m When "another game's install starts"
    val other = store().startOrReuse("com.other", OWNER)

    m Then "it has its own id and both are kept"
    assertEquals("click-2", other.clickId)
    assertEquals(listOf("com.game", "com.other"), store().all().map { it.packageName })
  }

  @Test
  fun `An install older than a week is dropped`() = coScenario {
    m Given "a pending install started a week ago and one started now"
    store().startOrReuse("com.old", OWNER)
    now += PaEPendingInstalls.MAX_AGE_MILLIS
    store().startOrReuse("com.new", OWNER)

    m When "the pending installs are read"
    val installs = store().all()

    m Then "only the recent one is left, and the old one is gone for good"
    assertEquals(listOf("com.new"), installs.map { it.packageName })
    assertNotEquals("click-1", store().startOrReuse("com.old", OWNER).clickId)
  }

  @Test
  fun `A refused click holds for the same start, then a new install is new`() = coScenario {
    m Given "a pending install whose click was refused"
    store().startOrReuse("com.game", OWNER)
    store().setClick("click-1", Click.REFUSED)

    m When "the same game starts at once, then again after the hold"
    val atOnce = store().startOrReuse("com.game", OWNER)
    now += PaEPendingInstalls.REFUSAL_HOLD_MILLIS
    val later = store().startOrReuse("com.game", OWNER)

    m Then "the first is the refused install, the second a new one with a new id"
    assertEquals(Click.REFUSED, atOnce.click)
    assertEquals("click-1", atOnce.clickId)
    assertEquals(PaEPendingInstall("com.game", "click-2", now, Click.NOT_SENT, OWNER), later)
  }

  @Test
  fun `A reported install is removed`() = coScenario {
    m Given "two pending installs"
    store().startOrReuse("com.game", OWNER)
    store().startOrReuse("com.other", OWNER)

    m When "the first one is removed"
    store().remove("click-1")

    m Then "only the second is left"
    assertEquals(listOf("click-2"), store().all().map { it.clickId })
  }

  @Test
  fun `An account change clears every pending install`() = coScenario {
    m Given "two pending installs"
    store().startOrReuse("com.game", OWNER)
    store().startOrReuse("com.other", OWNER)

    m When "the account changes"
    store().clearAll()

    m Then "nothing is pending"
    assertEquals(emptyList<PaEPendingInstall>(), store().all())
  }

  @Test
  fun `The number of pending installs is capped, the oldest going first`() = coScenario {
    m Given "as many pending installs as the cap"
    repeat(PaEPendingInstalls.MAX_INSTALLS) {
      store().startOrReuse("com.game$it", OWNER)
      now++
    }

    m When "one more install starts"
    store().startOrReuse("com.latest", OWNER)

    m Then "the oldest is dropped and the latest kept"
    val packages = store().all().map { it.packageName }
    assertEquals(PaEPendingInstalls.MAX_INSTALLS, packages.size)
    assertEquals(listOf("com.game1", "com.latest"), listOf(packages.first(), packages.last()))
  }

  @Test
  fun `Another account starting the same game gets its own click id`() = coScenario {
    m Given "a pending install of the game by one account"
    store().startOrReuse("com.game", OWNER)

    m When "another account starts the same game"
    val other = store().startOrReuse("com.game", "0xOther")

    m Then "it is a new install that replaces the first one's"
    assertEquals(PaEPendingInstall("com.game", "click-2", now, Click.NOT_SENT, "0xOther"), other)
    assertEquals(listOf(other), store().all())
  }

  @Test
  fun `An install saved without an owner is dropped`() = coScenario {
    m Given "an install saved before installs had an owner, next to a current one"
    store().startOrReuse("com.game", OWNER)
    val withOwner = dataStore.data.first()[INSTALLS]!!
    dataStore.edit {
      it[INSTALLS] = withOwner.dropLast(1) +
        """,{"package":"com.old","click_id":"old","created_at":$now,"click":"RECORDED"}]"""
    }

    m When "the pending installs are read"
    val installs = store().all()

    m Then "only the one with an owner is left"
    assertEquals(listOf("com.game"), installs.map { it.packageName })
  }

  @Test
  fun `An install from a clock set back is kept`() = coScenario {
    m Given "an install started, then the phone's clock set back a day"
    store().startOrReuse("com.game", OWNER)
    now -= TimeUnit.DAYS.toMillis(1)

    m When "the pending installs are read"
    val installs = store().all()

    m Then "the install is still pending"
    assertEquals(listOf("click-1"), installs.map { it.clickId })
  }

  @ParameterizedTest(name = "{0}")
  @ValueSource(
    strings = [
      "not json",
      "{\"package\":\"com.game\"}",
      "[1, 2]",
      "[null]",
      "[{\"package\":\"com.game\",\"click_id\":\"c\",\"created_at\":1000000000," +
        "\"click\":\"SOMETHING_NEW\",\"owner\":\"0xWallet\"}]",
    ]
  )
  fun `What cannot be read back is nothing pending`(stored: String) = coScenario {
    m Given "a stored value that is not a list of installs: $stored"
    dataStore.edit { it[INSTALLS] = stored }

    m When "the pending installs are read and a new install starts"
    val installs = store().all()
    val started = store().startOrReuse("com.game", OWNER)

    m Then "nothing was pending, nothing was thrown, and the new install is kept"
    assertEquals(emptyList<PaEPendingInstall>(), installs)
    assertEquals(listOf(started), store().all())
  }

  @Test
  fun `Whether a game is pending is read without writing`() = coScenario {
    m Given "a pending install of a game"
    store().startOrReuse("com.game", OWNER)
    val before = dataStore.data.first()

    m When "the games are looked up"
    val pending = listOf("com.game", "com.other").map { store().isPending(it) }

    m Then "only the pending game is found, and the store is untouched"
    assertTrue(pending[0])
    assertFalse(pending[1])
    assertEquals(before, dataStore.data.first())
  }

  private companion object {
    const val OWNER = "0xWallet"
    val INSTALLS = stringPreferencesKey("pending_installs")
  }
}
