package com.aptoide.android.aptoidegames.gamegenie

import cm.aptoide.pt.test.gherkin.scenario
import com.aptoide.android.aptoidegames.ads.native_ads.RecordingAnalytics
import com.aptoide.android.aptoidegames.gamegenie.analytics.GameGenieAnalytics
import com.aptoide.android.aptoidegames.gamegenie.analytics.GameGenieEntryPoint
import com.aptoide.android.aptoidegames.gamegenie.presentation.askGameGenieQuery
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

// APT-247: GameGenie left the bottom bar; it is reached from search and Settings instead.
internal class GameGenieEntryPointsTest {

  @Test
  fun `A typed search is carried into the chat, trimmed`() = scenario {
    m Given "a search value with surrounding spaces"
    m When "the query for the chat is computed"
    val query = askGameGenieQuery("  clash of clans ")

    m Then "the trimmed text is sent as the first message"
    assertEquals("clash of clans", query)
  }

  @Test
  fun `An empty search opens the chat without a first message`() = scenario {
    m Given "nothing typed in the search bar"
    m When "the query for the chat is computed"
    val query = askGameGenieQuery("   ")

    m Then "no query is carried"
    assertNull(query)
  }

  @Test
  fun `A search too short to be valid is not carried`() = scenario {
    m Given "a single typed character, which search itself rejects"
    m When "the query for the chat is computed"
    val query = askGameGenieQuery("a")

    m Then "no query is carried"
    assertNull(query)
  }

  @Test
  fun `Each entry point reports its own source`() = scenario {
    m Given "the GameGenie analytics"
    val recorder = RecordingAnalytics()
    val analytics = GameGenieAnalytics(recorder.generic())

    m When "each entry point is tapped"
    GameGenieEntryPoint.entries.forEach(analytics::sendGameGenieEntryClick)

    m Then "one event per entry point is sent, with its source"
    assertEquals(
      listOf(
        "gamegenie_entry_click" to mapOf("source" to "search_suggestions"),
        "gamegenie_entry_click" to mapOf("source" to "search_empty"),
        "gamegenie_entry_click" to mapOf("source" to "settings"),
      ),
      recorder.events.map { it.first to it.second },
    )
  }
}
