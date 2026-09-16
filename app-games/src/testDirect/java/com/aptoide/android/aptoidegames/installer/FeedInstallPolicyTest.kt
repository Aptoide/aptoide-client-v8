package com.aptoide.android.aptoidegames.installer

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

// The other half of the switch: directly distributed builds never inline install, so taking
// one-tap install away from their feed would be a pure regression.
internal class FeedInstallPolicyTest {

  @Test
  fun `Directly distributed builds keep one tap install in the feed`() = scenario {
    m Given "the direct source set"
    m When "the feed install policy is read"
    m Then "feed cards install in place, as there is nothing to attribute"
    assertFalse(FEED_INSTALL_DIVERTS_TO_APPVIEW)
  }
}
