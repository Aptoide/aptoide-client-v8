package com.aptoide.android.aptoidegames.play_and_earn.presentation.notifications

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// The mission-completed notification is the only Play & Earn entry point the user cannot re-open,
// so its destination has to be right the first time: the game's appview, already on the Rewards
// tab, resolved like every other Play & Earn entry point. Asserting the whole route pins the
// parameter order and the `?&` shape too — both are load-bearing for nav-graph matching, and
// `contains` checks would not catch a stray utm parameter or a reordering.
internal class MissionCompletedRouteTest {

  private val packageName = "com.example.game"

  @Test
  fun `Route opens the played game's appview by package name, gamified`() = scenario {
    m Given "a mission completed while playing $packageName"

    m When "the notification destination is built"
    val route = missionCompletedRoute(packageName)

    m Then "it is the gamified appview route for that package, with no store (AND-719)"
    assertEquals("app/package_name=$packageName?&is_gamified=true", route)
  }

  @Test
  fun `Route keeps the source in a single path segment`() = scenario {
    m Given "a mission completed while playing $packageName"

    m When "the notification destination is built"
    val path = missionCompletedRoute(packageName).substringBefore("?")

    m Then "only the app/ separator splits the path; a second slash can't match app/{source}"
    assertEquals(1, path.count { it == '/' })
  }
}
