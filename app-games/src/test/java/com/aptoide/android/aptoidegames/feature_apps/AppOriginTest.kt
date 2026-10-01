package com.aptoide.android.aptoidegames.feature_apps

import cm.aptoide.pt.feature_apps.data.emptyApp
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_apps.domain.AppOrigin
import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// Install routing keys on where an app came from. Every app built before the device API
// existed - and every build that never binds it - has to keep reading as a v7 app, so the
// default is pinned.
internal class AppOriginTest {

  @Test
  fun `An app built without an origin is a v7 app`() = scenario {
    m Given "apps built by code that knows nothing about origins"
    val apps = listOf(emptyApp, randomApp)

    m When "their origins are read"
    val origins = apps.map { it.origin }

    m Then "they are all v7"
    assertEquals(listOf(AppOrigin.V7, AppOrigin.V7), origins)
  }
}
