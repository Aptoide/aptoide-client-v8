package com.aptoide.android.aptoidegames.installer.presentation

import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_apps.domain.AppOrigin
import cm.aptoide.pt.test.gherkin.scenario
import com.aptoide.android.aptoidegames.apkfy.ROBLOX_PACKAGE
import com.aptoide.android.aptoidegames.installer.CatalogStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// An app of the new services that has no Play catalog token is not offered: it installs
// through Play only, and Play cannot install it. Everything else keeps its Install button,
// above all when the lookup merely failed - a flaky connection must never hide an install.
internal class InstallAvailabilityTest {

  // randomApp may come flagged for billing, which would make it install through Aptoide
  private val playApp =
    randomApp.copy(origin = AppOrigin.DEVICE_API, isAppCoins = false, bdsFlags = null)

  @Test
  fun `An app not in the Play catalog is not offered`() = scenario {
    m Given "an app of the new services whose lookup says it is not in the catalog"

    m When "its availability is decided on the app view"
    val availability = installAvailability(playApp, CatalogStatus.NOT_IN_CATALOG, onAppView = true)

    m Then "it is not offered"
    assertEquals(InstallAvailability.NOT_OFFERED, availability)
  }

  @Test
  fun `An app still being looked up is checking`() = scenario {
    m Given "an app of the new services whose lookup has not answered"

    m When "its availability is decided on the app view"
    val availability = installAvailability(playApp, CatalogStatus.UNKNOWN, onAppView = true)

    m Then "it is being checked, so the button does not flash from Install to unavailable"
    assertEquals(InstallAvailability.CHECKING, availability)
  }

  @Test
  fun `An app in the Play catalog is available`() = scenario {
    m Given "an app of the new services with a token"

    m When "its availability is decided on the app view"
    val availability = installAvailability(playApp, CatalogStatus.IN_CATALOG, onAppView = true)

    m Then "it is available"
    assertEquals(InstallAvailability.AVAILABLE, availability)
  }

  @Test
  fun `A failed lookup keeps the app available`() = scenario {
    m Given "an app of the new services whose lookup failed"

    m When "its availability is decided on the app view"
    val availability = installAvailability(playApp, CatalogStatus.FAILED, onAppView = true)

    m Then "it stays available, and the tap retries the lookup"
    assertEquals(InstallAvailability.AVAILABLE, availability)
  }

  @Test
  fun `A v7 app is always available`() = scenario {
    m Given "a v7 app whose lookup says it is not in the catalog"
    val v7App = randomApp.copy(origin = AppOrigin.V7)

    m When "its availability is decided on the app view"
    val availability = installAvailability(v7App, CatalogStatus.NOT_IN_CATALOG, onAppView = true)

    m Then "it is available, as it installs through Aptoide when Play cannot"
    assertEquals(InstallAvailability.AVAILABLE, availability)
  }

  @Test
  fun `An app that installs through Aptoide is always available`() = scenario {
    m Given "an app of the new services flagged for Aptoide billing, not in the Play catalog"
    val flagged = playApp.copy(isAppCoins = true)

    m When "its availability is decided on the app view"
    val availability = installAvailability(flagged, CatalogStatus.NOT_IN_CATALOG, onAppView = true)

    m Then "it is available, as it never needed a token"
    assertEquals(InstallAvailability.AVAILABLE, availability)
  }

  @Test
  fun `An overlay title is always available`() = scenario {
    m Given "Roblox read from the new services, whatever the lookup says"
    val roblox = playApp.copy(packageName = ROBLOX_PACKAGE)

    m When "its availability is decided on the app view"
    val availability = installAvailability(roblox, CatalogStatus.NOT_IN_CATALOG, onAppView = true)

    m Then "it is available, as it installs through Play's overlay"
    assertEquals(InstallAvailability.AVAILABLE, availability)
  }

  @Test
  fun `A card outside the app view never withholds the install`() = scenario {
    m Given "an app of the new services not in the catalog, shown on a card"

    m When "its availability is decided off the app view"
    val availability = installAvailability(playApp, CatalogStatus.NOT_IN_CATALOG, onAppView = false)

    m Then "it is available, as cards do not look up the catalog"
    assertEquals(InstallAvailability.AVAILABLE, availability)
  }

  @Test
  fun `Without a catalog to ask the app is available`() = scenario {
    m Given "an app of the new services on a build that binds no catalog checker"

    m When "its availability is decided on the app view"
    val availability = installAvailability(playApp, status = null, onAppView = true)

    m Then "it is available"
    assertEquals(InstallAvailability.AVAILABLE, availability)
  }
}
