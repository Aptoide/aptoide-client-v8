package com.aptoide.android.aptoidegames.installer

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// Pins the Play Catalog Access compliance switch. Without this the constant is only read from
// production code, so flipping it would silently remove the feed gate with every test green.
internal class FeedInstallPolicyTest {

  @Test
  fun `Play distributed builds hand feed installs over to AppView`() = scenario {
    m Given "the gplay source set"
    m When "the feed install policy is read"
    m Then "feed cards divert, because they cannot show the attribution themselves"
    assertTrue(FEED_INSTALL_DIVERTS_TO_APPVIEW)
  }
}
