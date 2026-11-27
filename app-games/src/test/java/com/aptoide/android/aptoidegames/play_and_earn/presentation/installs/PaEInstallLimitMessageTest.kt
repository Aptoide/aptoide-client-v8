package com.aptoide.android.aptoidegames.play_and_earn.presentation.installs

import cm.aptoide.pt.test.gherkin.scenario
import com.aptoide.android.aptoidegames.R
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

// AND-954: the day's install limit says when installs count again, as the phone writes it.
internal class PaEInstallLimitMessageTest {

  private val lisbon = ZoneId.of("Europe/Lisbon")
  private val now = Instant.parse("2026-12-01T10:00:00Z")

  @Test
  fun `A limit with an end says when installs count again`() = scenario {
    m Given "an install limit ending on 2 December 2026 at 00:00 in Lisbon"
    val retryAt = Instant.parse("2026-12-02T00:00:00Z")

    m When "its message is written in British English"
    val message = installLimitMessage(retryAt, now, lisbon, Locale.UK)

    m Then "it is the text with the date and time"
    assertEquals(
      PaEInstallLimitMessage(R.string.play_and_earn_install_limit_body_until, "2 Dec 2026, 00:00"),
      message
    )
  }

  @Test
  fun `A limit without an end says so without a time`() = scenario {
    m Given "an install limit whose end the backend did not say"
    val retryAt: Instant? = null

    m When "its message is written"
    val message = installLimitMessage(retryAt, now, lisbon, Locale.UK)

    m Then "it is the text without a time"
    assertEquals(PaEInstallLimitMessage(R.string.play_and_earn_install_limit_body), message)
  }

  @Test
  fun `An end already gone is not shown`() = scenario {
    m Given "an install limit whose end has passed on the phone's clock"
    val retryAt = Instant.parse("2026-12-01T09:00:00Z")

    m When "its message is written"
    val message = installLimitMessage(retryAt, now, lisbon, Locale.UK)

    m Then "it is the text without a time"
    assertEquals(PaEInstallLimitMessage(R.string.play_and_earn_install_limit_body), message)
  }
}
