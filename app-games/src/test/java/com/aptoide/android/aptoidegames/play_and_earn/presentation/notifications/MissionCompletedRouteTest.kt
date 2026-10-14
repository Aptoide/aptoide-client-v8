package com.aptoide.android.aptoidegames.play_and_earn.presentation.notifications

import cm.aptoide.pt.test.gherkin.scenario
import com.aptoide.android.aptoidegames.BuildConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// The mission-completed notification is the only Play & Earn entry point the user cannot re-open,
// so its destination has to be right the first time: the game's appview, on the brand's own store,
// already on the Rewards tab. Asserting the whole route pins the parameter order and the `?&`
// shape too — both are load-bearing for nav-graph matching, and `contains` checks would not catch
// a stray utm parameter or a reordering.
internal class MissionCompletedRouteTest {

  private val packageName = "com.example.game"

  @Test
  fun `Route opens the played game's appview on the brand store, gamified`() = scenario {
    m Given "a mission completed while playing $packageName"

    m When "the notification destination is built"
    val route = missionCompletedRoute(packageName)

    m Then "it is the gamified appview route for that package, scoped to the brand's store"
    assertEquals(
      "app/package_name=$packageName/store_name=${BuildConfig.MARKET_NAME}?&is_gamified=true",
      route
    )
  }
}
