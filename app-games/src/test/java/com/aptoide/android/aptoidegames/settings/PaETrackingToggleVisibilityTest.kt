package com.aptoide.android.aptoidegames.settings

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// AND-902: the Settings opt-out for usage tracking only makes sense when tracking can run.
internal class PaETrackingToggleVisibilityTest {

  @Test
  fun `Shown when PaE is visible, the kill switch is on and the permissions are granted`() =
    scenario {
      m Given "every condition true"
      m When "the visibility is computed"
      val shown = showPaETrackingToggle(
        playAndEarnVisible = true,
        usageTrackingEnabled = true,
        hasUsagePermissions = true,
      )

      m Then "the toggle is shown"
      assertTrue(shown)
    }

  @Test
  fun `Hidden without the permissions`() = scenario {
    m Given "Play and Earn visible and the kill switch on, but no usage permissions"
    m When "the visibility is computed"
    val shown = showPaETrackingToggle(
      playAndEarnVisible = true,
      usageTrackingEnabled = true,
      hasUsagePermissions = false,
    )

    m Then "the toggle is hidden: the lock on a time mission is the way to the permissions"
    assertFalse(shown)
  }

  @Test
  fun `Hidden when the kill switch is off`() = scenario {
    m Given "Play and Earn visible and the permissions granted, but the kill switch off"
    m When "the visibility is computed"
    val shown = showPaETrackingToggle(
      playAndEarnVisible = true,
      usageTrackingEnabled = false,
      hasUsagePermissions = true,
    )

    m Then "the toggle is hidden (Phase 1 behaviour)"
    assertFalse(shown)
  }

  @Test
  fun `Hidden when Play and Earn is not visible`() = scenario {
    m Given "the kill switch on and the permissions granted, but Play and Earn hidden"
    m When "the visibility is computed"
    val shown = showPaETrackingToggle(
      playAndEarnVisible = false,
      usageTrackingEnabled = true,
      hasUsagePermissions = true,
    )

    m Then "the toggle is hidden"
    assertFalse(shown)
  }
}
