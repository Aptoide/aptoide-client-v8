package com.aptoide.android.aptoidegames.installer

import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.test.gherkin.scenario
import com.aptoide.android.aptoidegames.apkfy.FREE_FIRE_PACKAGE
import com.aptoide.android.aptoidegames.apkfy.ROBLOX_PACKAGE
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// BDS (Catappult) builds carry AppCoins billing that the Play build would lose, so they must
// never take the Play Catalog path: no token fetched, no "Google Play" tag, no inline install.
// The exclusion keys off the STORE_BDS flag alone - a missing or unrelated flag list is not
// excluded, so regular catalog apps keep the Play path.
internal class PlayCatalogExclusionTest {

  @Test
  fun `An app flagged STORE_BDS is excluded from the Play catalog`() = scenario {
    m Given "an app whose bdsFlags contain STORE_BDS"
    val app = randomApp.copy(bdsFlags = listOf("STORE_BDS"))

    m When "the Play catalog exclusion is evaluated"
    val excluded = app.excludedFromPlayCatalog()

    m Then "it is excluded"
    assertTrue(excluded)
  }

  @Test
  fun `STORE_BDS among other flags still excludes the app`() = scenario {
    m Given "an app whose bdsFlags mix STORE_BDS with unrelated flags"
    val app = randomApp.copy(bdsFlags = listOf("OTHER_FLAG", "STORE_BDS", null))

    m When "the Play catalog exclusion is evaluated"
    val excluded = app.excludedFromPlayCatalog()

    m Then "it is excluded"
    assertTrue(excluded)
  }

  @Test
  fun `An app with no bdsFlags is not excluded`() = scenario {
    m Given "an app whose bdsFlags are null"
    val app = randomApp.copy(bdsFlags = null)

    m When "the Play catalog exclusion is evaluated"
    val excluded = app.excludedFromPlayCatalog()

    m Then "it keeps the Play path"
    assertFalse(excluded)
  }

  @Test
  fun `An app with an empty flag list is not excluded`() = scenario {
    m Given "an app whose bdsFlags are empty"
    val app = randomApp.copy(bdsFlags = emptyList())

    m When "the Play catalog exclusion is evaluated"
    val excluded = app.excludedFromPlayCatalog()

    m Then "it keeps the Play path"
    assertFalse(excluded)
  }

  @Test
  fun `A Roblox build flagged STORE_BDS still takes Play's overlay`() = scenario {
    m Given "Roblox carrying the STORE_BDS flag"
    val app = randomApp.copy(packageName = ROBLOX_PACKAGE, bdsFlags = listOf("STORE_BDS"))

    m When "the Play catalog exclusion is evaluated"
    val excluded = app.excludedFromPlayCatalog()

    m Then "the overlay rule wins: it is not excluded, so the attribution and the overlay stay"
    assertFalse(excluded)
  }

  @Test
  fun `A Free Fire build flagged STORE_BDS still takes Play's overlay`() = scenario {
    m Given "Free Fire carrying the STORE_BDS flag"
    val app = randomApp.copy(packageName = FREE_FIRE_PACKAGE, bdsFlags = listOf("STORE_BDS"))

    m When "the Play catalog exclusion is evaluated"
    val excluded = app.excludedFromPlayCatalog()

    m Then "the overlay rule wins: it is not excluded"
    assertFalse(excluded)
  }

  @Test
  fun `The flag match is exact - a lowercase variant does not exclude`() = scenario {
    m Given "an app whose bdsFlags carry a differently cased variant"
    val app = randomApp.copy(bdsFlags = listOf("store_bds"))

    m When "the Play catalog exclusion is evaluated"
    val excluded = app.excludedFromPlayCatalog()

    m Then "it keeps the Play path, matching the backend's uppercase contract exactly"
    assertFalse(excluded)
  }

  @Test
  fun `Unrelated flags alone do not exclude the app`() = scenario {
    m Given "an app whose bdsFlags contain only unrelated flags"
    val app = randomApp.copy(bdsFlags = listOf("OTHER_FLAG", "ANOTHER"))

    m When "the Play catalog exclusion is evaluated"
    val excluded = app.excludedFromPlayCatalog()

    m Then "it keeps the Play path"
    assertFalse(excluded)
  }
}
