package com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions

import cm.aptoide.pt.test.gherkin.scenario
import com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions.PaEPermissionsDialogKind.Denied
import com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions.PaEPermissionsDialogKind.Locked
import com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions.PaEPermissionsDialogKind.None
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// AND-922: which dialog the Start Earning screen shows when the user comes back from the system
// permission screens. On Android 15+ any missing permission means "Permissions locked?", since the
// restricted-settings card it reveals is the only way to unlock the toggles; the app cannot tell a
// locked toggle from a declined one.
internal class PaEPermissionsDialogAfterRequestTest {

  @Test
  fun `Both permissions granted need no dialog on any Android version`() = scenario {
    m Given "both permissions granted"

    m When "the dialog is chosen on Android 15+ and on Android 14"
    val onAndroid15 = paePermissionsDialogAfterRequest(
      requiresRestrictedSettings = true,
      hasOverlay = true,
      hasUsageStats = true,
    )
    val onAndroid14 = paePermissionsDialogAfterRequest(
      requiresRestrictedSettings = false,
      hasOverlay = true,
      hasUsageStats = true,
    )

    m Then "no dialog is shown"
    assertEquals(None, onAndroid15)
    assertEquals(None, onAndroid14)
  }

  @Test
  fun `Android 15+ with overlay granted and usage access missing shows Permissions locked`() =
    scenario {
      m Given "Android 15+, the overlay permission granted, usage access still missing"

      m When "the dialog is chosen"
      val dialog = paePermissionsDialogAfterRequest(
        requiresRestrictedSettings = true,
        hasOverlay = true,
        hasUsageStats = false,
      )

      m Then "Permissions locked? is shown, with the way to the restricted settings"
      assertEquals(Locked, dialog)
    }

  @Test
  fun `Android 15+ with usage access granted and overlay missing shows Permissions locked`() =
    scenario {
      m Given "Android 15+, usage access granted, the overlay permission still missing"

      m When "the dialog is chosen"
      val dialog = paePermissionsDialogAfterRequest(
        requiresRestrictedSettings = true,
        hasOverlay = false,
        hasUsageStats = true,
      )

      m Then "Permissions locked? is shown"
      assertEquals(Locked, dialog)
    }

  @Test
  fun `Android 15+ with both permissions missing shows Permissions locked`() = scenario {
    m Given "Android 15+, both permissions missing"

    m When "the dialog is chosen"
    val dialog = paePermissionsDialogAfterRequest(
      requiresRestrictedSettings = true,
      hasOverlay = false,
      hasUsageStats = false,
    )

    m Then "Permissions locked? is shown"
    assertEquals(Locked, dialog)
  }

  @Test
  fun `Android 14 with one permission missing shows Rewards are paused`() = scenario {
    m Given "Android 14, where restricted settings do not lock these permissions"

    m When "the dialog is chosen with either permission missing"
    val usageMissing = paePermissionsDialogAfterRequest(
      requiresRestrictedSettings = false,
      hasOverlay = true,
      hasUsageStats = false,
    )
    val overlayMissing = paePermissionsDialogAfterRequest(
      requiresRestrictedSettings = false,
      hasOverlay = false,
      hasUsageStats = true,
    )

    m Then "Rewards are paused is shown, since the user declined"
    assertEquals(Denied, usageMissing)
    assertEquals(Denied, overlayMissing)
  }

  @Test
  fun `Android 14 with both permissions missing shows Rewards are paused`() = scenario {
    m Given "Android 14, both permissions missing"

    m When "the dialog is chosen"
    val dialog = paePermissionsDialogAfterRequest(
      requiresRestrictedSettings = false,
      hasOverlay = false,
      hasUsageStats = false,
    )

    m Then "Rewards are paused is shown"
    assertEquals(Denied, dialog)
  }
}
