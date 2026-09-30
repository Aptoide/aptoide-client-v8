package com.aptoide.android.aptoidegames.newservices

import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_apps.domain.AppOrigin
import cm.aptoide.pt.test.gherkin.scenario
import com.aptoide.android.aptoidegames.apkfy.ROBLOX_PACKAGE
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// Only an app that installs through Aptoide may be updated without the user asking on this
// build; everything Play installs, Play updates.
internal class PlaySilentUpdatePolicyTest {

  @Test
  fun `An app flagged for Aptoide billing may be updated silently`() = scenario {
    m Given "an update of an app read from the new services with the billing flag"
    val app = randomApp.copy(origin = AppOrigin.DEVICE_API, isAppCoins = true, bdsFlags = null)

    m When "the policy is asked"
    val allowed = PlaySilentUpdatePolicy.allows(app)

    m Then "it may"
    assertTrue(allowed)
  }

  @Test
  fun `An app Play installs may not be updated silently`() = scenario {
    m Given "an update of an app read from the new services without the billing flag"
    val app = randomApp.copy(origin = AppOrigin.DEVICE_API, isAppCoins = false, bdsFlags = null)

    m When "the policy is asked"
    val allowed = PlaySilentUpdatePolicy.allows(app)

    m Then "it may not"
    assertFalse(allowed)
  }

  @Test
  fun `A v7 store app may be updated silently`() = scenario {
    m Given "an update of a v7 app flagged STORE_BDS"
    val app = randomApp.copy(origin = AppOrigin.V7, bdsFlags = listOf("STORE_BDS"))

    m When "the policy is asked"
    val allowed = PlaySilentUpdatePolicy.allows(app)

    m Then "it may, as it installs through Aptoide"
    assertTrue(allowed)
  }

  @Test
  fun `An overlay title may not be updated silently`() = scenario {
    m Given "an update of Roblox, whatever its flags"
    val app = randomApp.copy(packageName = ROBLOX_PACKAGE, bdsFlags = listOf("STORE_BDS"))

    m When "the policy is asked"
    val allowed = PlaySilentUpdatePolicy.allows(app)

    m Then "it may not, as it only ever installs through Play's overlay"
    assertFalse(allowed)
  }

  @Test
  fun `With the switch off any update may be installed silently`() = scenario {
    m Given "an update of an app Play installs"
    val app = randomApp.copy(origin = AppOrigin.DEVICE_API, isAppCoins = false, bdsFlags = null)

    m When "the policy of the switch being off is asked"
    val allowed = AnySilentUpdatePolicy.allows(app)

    m Then "it may, as every other build installs any update"
    assertTrue(allowed)
  }
}
