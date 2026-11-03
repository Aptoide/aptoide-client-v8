package com.aptoide.android.aptoidegames.installer.analytics

import cm.aptoide.pt.feature_apps.domain.AppOrigin
import cm.aptoide.pt.test.gherkin.scenario
import com.google.gson.Gson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

// Installs of titles read from the device API - apkfy's cohort titles above all - have to be
// told apart from v7 installs in Firebase, so the download and install events carry the
// catalog. Payloads are stored with each install, so ones stored before the field existed are
// pinned too.
internal class CatalogSourceAnalyticsTest {

  @Test
  fun `Each catalog reports under its own name`() = scenario {
    m Given "the catalogs an app can come from"
    val origins = AppOrigin.entries

    m When "they are reported"
    val sources = origins.map { it.catalogSource }

    m Then "v7 and the device API read as such"
    assertEquals(listOf("v7", "device_api"), sources)
  }

  @Test
  fun `An install's events carry its catalog`() = scenario {
    m Given "the payload of an install read from the device API"
    val payload = payload.copy(catalogSource = AppOrigin.DEVICE_API.catalogSource)

    m When "its event parameters are built"
    val params = payload.toAppGenericParameters(packageName = "com.spotify.music")

    m Then "they name the catalog"
    assertEquals("device_api", params["catalog_source"])
    assertEquals("com.spotify.music", params["package_name"])
  }

  @Test
  fun `A payload stored before the catalog existed reports none`() = scenario {
    m Given "a payload stored by an older version, without the catalog"
    val stored = Gson().toJson(payload).replace(",\"catalogSource\":\"v7\"", "")

    m When "it is read back and its event parameters are built"
    val restored = stored.toAnalyticsPayload()
    val params = restored.toAppGenericParameters(packageName = "com.roblox.client")

    m Then "the catalog is unknown and left out, the rest intact"
    assertNull(restored?.catalogSource)
    assertFalse(params.containsKey("catalog_source"))
    assertEquals("com.roblox.client", params["package_name"])
  }

  private val payload = AnalyticsPayload(
    action = null,
    isApkfy = true,
    homeTab = null,
    isAab = false,
    aabTypes = "",
    isAppCoins = false,
    isInCatappult = null,
    hasObb = false,
    versionCode = 1,
    context = "apkfy",
    previousContext = null,
    store = "aptoide-games",
    bundleMeta = null,
    searchMeta = null,
    itemPosition = null,
    trustedBadge = null,
    userClicks = 1,
    catalogSource = "v7",
  )
}
