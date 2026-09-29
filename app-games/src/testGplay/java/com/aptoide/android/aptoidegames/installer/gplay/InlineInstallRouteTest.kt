package com.aptoide.android.aptoidegames.installer.gplay

import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.test.gherkin.scenario
import com.aptoide.android.aptoidegames.apkfy.FREE_FIRE_PACKAGE
import com.aptoide.android.aptoidegames.apkfy.ROBLOX_PACKAGE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// The resolver's decisions before any catalog token is fetched, pinned as a pure ladder so the
// ordering is a tested contract rather than the incidental order of a few early returns. Only
// PLAY_CATALOG is allowed to reach the token repository; everything else must be decided from
// the App alone, which is what keeps BDS apps from ever triggering a token request.
internal class InlineInstallRouteTest {

  private val bds = listOf("STORE_BDS")

  @Test
  fun `A regular catalog app goes on to the Play catalog`() = scenario {
    m Given "an app with no special flags and no aborted attempt"
    val app = randomApp.copy(bdsFlags = null)

    m When "the route is decided"
    val route = inlineInstallRoute(app, aborted = emptySet())

    m Then "it is the only route that may fetch a token"
    assertEquals(InlineInstallRoute.PLAY_CATALOG, route)
  }

  @Test
  fun `A BDS app stays on Aptoide before any token is considered`() = scenario {
    m Given "an app flagged STORE_BDS"
    val app = randomApp.copy(bdsFlags = bds)

    m When "the route is decided"
    val route = inlineInstallRoute(app, aborted = emptySet())

    m Then "it installs through Aptoide only"
    assertEquals(InlineInstallRoute.APTOIDE_ONLY, route)
  }

  @Test
  fun `Roblox takes the details overlay even when flagged STORE_BDS`() = scenario {
    m Given "Roblox carrying the STORE_BDS flag"
    val app = randomApp.copy(packageName = ROBLOX_PACKAGE, bdsFlags = bds)

    m When "the route is decided"
    val route = inlineInstallRoute(app, aborted = emptySet())

    m Then "the overlay rule wins, matching the labelling side"
    assertEquals(InlineInstallRoute.DETAILS_OVERLAY, route)
  }

  @Test
  fun `Free Fire takes the details overlay even when flagged STORE_BDS`() = scenario {
    m Given "Free Fire carrying the STORE_BDS flag"
    val app = randomApp.copy(packageName = FREE_FIRE_PACKAGE, bdsFlags = bds)

    m When "the route is decided"
    val route = inlineInstallRoute(app, aborted = emptySet())

    m Then "the overlay rule wins"
    assertEquals(InlineInstallRoute.DETAILS_OVERLAY, route)
  }

  @Test
  fun `A BDS app stays on Aptoide even if a previous inline attempt was aborted`() = scenario {
    m Given "an app flagged STORE_BDS whose package is in the aborted set"
    val app = randomApp.copy(bdsFlags = bds)

    m When "the route is decided"
    val route = inlineInstallRoute(app, aborted = setOf(app.packageName))

    m Then "the static exclusion is reported, not the session state"
    assertEquals(InlineInstallRoute.APTOIDE_ONLY, route)
  }

  @Test
  fun `A catalog app whose inline attempt was aborted skips the catalog this session`() =
    scenario {
      m Given "a regular app whose package is in the aborted set"
      val app = randomApp.copy(bdsFlags = null)

      m When "the route is decided"
      val route = inlineInstallRoute(app, aborted = setOf(app.packageName))

      m Then "it is aborted for the session"
      assertEquals(InlineInstallRoute.ABORTED, route)
    }
}
