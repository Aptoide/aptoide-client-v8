package com.aptoide.android.aptoidegames.play_and_earn.presentation.service

import cm.aptoide.pt.test.gherkin.scenario
import com.aptoide.android.aptoidegames.R
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// AND-964: the tracking service outlives a VPN refusal; its notification must not claim that
// play time is being counted meanwhile.
internal class PaETrackingNotificationTextTest {

  @Test
  fun `While the game in front waits for the VPN to go off, the notification says so`() =
    scenario {
      m Given "the game in front refused for a VPN"
      m When "the tracking notification's text is chosen"
      val text = trackingNotificationText(waitingForTheUser = true)

      m Then "it is the VPN message"
      assertEquals(
        PaETrackingNotificationText(
          title = R.string.play_and_earn_refused_title,
          body = R.string.play_and_earn_refused_vpn_body,
        ),
        text
      )
    }

  @Test
  fun `Otherwise the notification says play time is tracked`() = scenario {
    m Given "no game in front waiting for the user"
    m When "the tracking notification's text is chosen"
    val text = trackingNotificationText(waitingForTheUser = false)

    m Then "it is the usual recording text"
    assertEquals(
      PaETrackingNotificationText(
        title = R.string.play_and_earn_notification_recording_title,
        body = R.string.play_and_earn_notification_recording_body,
      ),
      text
    )
  }
}
