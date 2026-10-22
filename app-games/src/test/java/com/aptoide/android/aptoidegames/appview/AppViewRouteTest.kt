package com.aptoide.android.aptoidegames.appview

import cm.aptoide.pt.feature_apps.domain.AppSource
import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

// AND-914: the appview route's query must start with a parameter, never with "?&": Navigation
// then leaves the first parameter unparsed (is_gamified read as false, the store appended, 404 for
// a game outside the brand store).
internal class AppViewRouteTest {

  @Test
  fun `A gamified package route carries is_gamified as its first query parameter`() = scenario {
    m Given "a Play & Earn game known by package name"
    val source = AppSource.of(appId = null, packageName = "com.example.game")

    m When "its gamified appview route is built"
    val route = buildAppViewRoute(appSource = source, isGamified = true)

    m Then "the query starts right after the ? with the parameter"
    assertEquals("app/package_name=com.example.game?is_gamified=true", route)
  }

  @Test
  fun `A route with UTMs keeps them in order with no empty query token`() = scenario {
    m Given "an app known by id and a campaign"
    val source = AppSource.of(appId = 42, packageName = "com.example.game")

    m When "its appview route is built with UTMs and gamified"
    val route = buildAppViewRoute(
      appSource = source,
      utmCampaign = "c",
      utmMedium = "m",
      utmContent = "x",
      utmSource = "s",
      isGamified = true,
    )

    m Then "every parameter follows the previous one with a single separator"
    assertEquals(
      "app/app_id=42?utm_campaign=c&utm_medium=m&utm_content=x&utm_source=s&is_gamified=true",
      route,
    )
    assertFalse(route.contains("?&") || route.contains("&&"))
  }

  @Test
  fun `A plain route has no query at all`() = scenario {
    m Given "an app known by id and nothing else"
    val source = AppSource.of(appId = 42, packageName = null)

    m When "its appview route is built"
    val route = buildAppViewRoute(appSource = source)

    m Then "it is the path alone, with is_gamified=false as the only parameter"
    assertEquals("app/app_id=42?is_gamified=false", route)
  }
}
