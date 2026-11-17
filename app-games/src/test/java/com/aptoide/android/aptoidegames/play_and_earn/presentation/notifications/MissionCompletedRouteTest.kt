package com.aptoide.android.aptoidegames.play_and_earn.presentation.notifications

import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.test.gherkin.scenario
import com.aptoide.android.aptoidegames.appview.appViewRoute
import com.aptoide.android.aptoidegames.play_and_earn.domain.sessions.CompletedMissionEvent
import com.aptoide.android.aptoidegames.play_and_earn.presentation.progress.myProgressRoute
import com.google.gson.JsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// The mission-completed notification is the only Play & Earn entry point the user cannot re-open,
// so its destination has to be right the first time: the game's appview, already on the Rewards
// tab, resolved like every other Play & Earn entry point. Asserting the whole route pins the
// parameter order and the query shape too — both are load-bearing for nav-graph matching, and
// `contains` checks would not catch a stray utm parameter or a reordering.
internal class MissionCompletedRouteTest {

  private val packageName = "com.example.game"

  @Test
  fun `Route opens the played game's appview by package name, gamified`() = scenario {
    m Given "a mission completed while playing $packageName"

    m When "the notification destination is built"
    val route = missionCompletedRoute(packageName)

    m Then "it is the gamified appview route for that package, with no store (AND-719)"
    assertEquals("app/package_name=$packageName?is_gamified=true", route)
  }

  @Test
  fun `Route fits the appview destination's path shape`() = scenario {
    m Given "the appview destination pattern, $appViewRoute"
    val patternSegments = appViewRoute.substringBefore("?").split("/")

    m When "the notification destination is built"
    val pathSegments = missionCompletedRoute(packageName).substringBefore("?").split("/")

    m Then "it has the destination's segment count: one more slash and nothing matches (AND-914)"
    assertEquals(patternSegments.size, pathSegments.size)
    assertEquals(patternSegments.first(), pathSegments.first())
  }

  private fun completed(isUserMission: Boolean) = CompletedMissionEvent(
    mission = PaEMission(
      title = "Play 5 minutes", description = null, icon = null, type = PaEMissionType.PLAY_TIME,
      arguments = JsonObject(), units = 10, progress = null,
    ),
    packageName = packageName,
    isUserMission = isUserMission,
  )

  @Test
  fun `A game's mission opens the game, one of the player's own opens My progress`() = scenario {
    m Given "a game mission and a user mission, both completed while playing $packageName"
    m When "each notification destination is chosen"
    val gameRoute = completedMissionRoute(completed(isUserMission = false))
    val userRoute = completedMissionRoute(completed(isUserMission = true))

    m Then "the game's goes to its Rewards tab and the player's to My progress"
    assertEquals(missionCompletedRoute(packageName), gameRoute)
    assertEquals(myProgressRoute, userRoute)
  }
}
