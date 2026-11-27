package com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow

import cm.aptoide.pt.test.gherkin.scenario
import com.aptoide.android.aptoidegames.R
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZoneId
import java.time.format.DecimalStyle
import java.util.Locale

// AND-952: a limit's message says when it ends, as the phone writes a date and time.
internal class ExchangeErrorBodyTest {

  private val lisbon = ZoneId.of("Europe/Lisbon")
  private val plusTwo = ZoneId.of("Europe/Athens") // UTC+2 in December
  private val now = Instant.parse("2026-12-01T10:00:00Z")
  private val end = Instant.parse("2026-12-01T14:30:00Z")

  @Test
  fun `The redeem limit's end is a date and time in the phone's language`() = scenario {
    m Given "a redeem limit ending on 1 December 2026 at 14:30 in Lisbon"

    m When "it is written in British English and in Portuguese"
    val uk = formatLimitEnd(end, now, lisbon, Locale.UK, timeOnlyToday = false)
    val pt = formatLimitEnd(end, now, lisbon, Locale("pt", "PT"), timeOnlyToday = false)

    m Then "each locale writes its own date and time"
    assertEquals("1 Dec 2026, 14:30", uk)
    assertEquals("01/12/2026, 14:30", pt)
  }

  @Test
  fun `The end is written in the language's own digits`() = scenario {
    m Given "a language that does not write with 0 to 9"
    val locale = Locale.forLanguageTag("fa-IR")
    val zero = DecimalStyle.of(locale).zeroDigit
    assertNotEquals('0', zero)

    m When "the end is written for it"
    val text = formatLimitEnd(
      end = Instant.parse("2026-12-01T14:30:00Z"),
      now = Instant.parse("2026-11-30T10:00:00Z"),
      zone = ZoneId.of("UTC"),
      locale = locale,
      timeOnlyToday = false,
    )

    m Then "it has that language's digits and none of 0 to 9"
    assertTrue(text.any { it in zero..(zero + 9) }, text)
    assertFalse(text.any { it in '0'..'9' }, text)
  }

  @Test
  fun `A late UTC end is the next day where the phone is`() = scenario {
    m Given "a limit ending at 23:30 UTC on 1 December, read in a UTC+2 zone"
    val lateEnd = Instant.parse("2026-12-01T23:30:00Z")

    m When "it is written"
    val text = formatLimitEnd(lateEnd, now, plusTwo, Locale.UK, timeOnlyToday = false)

    m Then "it is 2 December at 01:30"
    assertEquals("2 Dec 2026, 01:30", text)
  }

  @Test
  fun `Too many attempts ending today shows only the time`() = scenario {
    m Given "too many attempts ending later today"

    m When "it is written"
    val text = formatLimitEnd(end, now, lisbon, Locale.UK, timeOnlyToday = true)

    m Then "the time is enough"
    assertEquals("14:30", text)
  }

  @Test
  fun `Too many attempts ending on another local day shows the date too`() = scenario {
    m Given "at 22:00 in a UTC+2 zone, too many attempts ending at 01:30 local time"
    val lateNow = Instant.parse("2026-12-01T20:00:00Z")
    val lateEnd = Instant.parse("2026-12-01T23:30:00Z")

    m When "it is written there, and in UTC where both are on the same day"
    val local = formatLimitEnd(lateEnd, lateNow, plusTwo, Locale.UK, timeOnlyToday = true)
    val utc = formatLimitEnd(lateEnd, lateNow, ZoneId.of("UTC"), Locale.UK, timeOnlyToday = true)

    m Then "the date is shown only where the end is tomorrow"
    assertEquals("2 Dec 2026, 01:30", local)
    assertEquals("23:30", utc)
  }

  @Test
  fun `A limit with a future end says when it ends`() = scenario {
    m Given "a redeem limit and too many attempts, both ending later"

    m When "their bodies are chosen"
    val limit = body(ExchangeErrorKind.Limit, end)
    val attempts = body(ExchangeErrorKind.TooManyAttempts, end)

    m Then "each uses its text with the end"
    val limitText = R.string.play_and_earn_exchange_error_limit_body_until
    val attemptsText = R.string.play_and_earn_exchange_error_too_many_attempts_body_until
    assertEquals(ExchangeErrorBody(limitText, "1 Dec 2026, 14:30"), limit)
    assertEquals(ExchangeErrorBody(attemptsText, "14:30"), attempts)
  }

  @Test
  fun `A limit with no end or an end already gone keeps the plain text`() = scenario {
    m Given "limits without an end, and limits whose end has passed"
    val past = Instant.parse("2026-12-01T09:59:00Z")

    m When "their bodies are chosen"
    val bodies = listOf(
      body(ExchangeErrorKind.Limit, null),
      body(ExchangeErrorKind.Limit, past),
      body(ExchangeErrorKind.Limit, now),
      body(ExchangeErrorKind.TooManyAttempts, null),
      body(ExchangeErrorKind.TooManyAttempts, past),
    )

    m Then "no time is shown"
    assertEquals(
      listOf(
        ExchangeErrorBody(R.string.play_and_earn_exchange_error_limit_body),
        ExchangeErrorBody(R.string.play_and_earn_exchange_error_limit_body),
        ExchangeErrorBody(R.string.play_and_earn_exchange_error_limit_body),
        ExchangeErrorBody(R.string.play_and_earn_exchange_error_too_many_attempts_body),
        ExchangeErrorBody(R.string.play_and_earn_exchange_error_too_many_attempts_body),
      ),
      bodies
    )
  }

  @Test
  fun `Errors without an end have no limit body`() = scenario {
    m Given "the errors that are not limits"
    val kinds = ExchangeErrorKind.entries.filterNot { it.hasEnd }

    m When "their limit bodies are asked for"
    val bodies = kinds.map { body(it, end) }

    m Then "there are none"
    bodies.forEach { assertNull(it) }
  }

  private fun body(kind: ExchangeErrorKind, retryAt: Instant?) =
    exchangeLimitBody(kind, retryAt, now, lisbon, Locale.UK)
}
