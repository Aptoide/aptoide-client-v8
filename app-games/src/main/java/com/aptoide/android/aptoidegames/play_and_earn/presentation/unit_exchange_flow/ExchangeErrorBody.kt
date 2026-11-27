package com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow

import androidx.annotation.StringRes
import com.aptoide.android.aptoidegames.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DecimalStyle
import java.time.format.FormatStyle
import java.util.Locale

/**
 * A redeem error's body text (AND-952).
 * @param retryAt when the limit ends, already written for the phone: the text's `%1$s`.
 */
data class ExchangeErrorBody(
  @StringRes val text: Int,
  val retryAt: String? = null,
)

/**
 * The body of a limit ([ExchangeErrorKind.hasEnd]); null for the other errors. It says when the
 * limit ends, unless the backend did not say or that moment is already gone.
 */
fun exchangeLimitBody(
  kind: ExchangeErrorKind,
  retryAt: Instant?,
  now: Instant,
  zone: ZoneId,
  locale: Locale,
): ExchangeErrorBody? {
  val end = retryAt?.takeIf { it.isAfter(now) }
  return when (kind) {
    ExchangeErrorKind.Limit -> end
      ?.let {
        ExchangeErrorBody(
          text = R.string.play_and_earn_exchange_error_limit_body_until,
          retryAt = formatLimitEnd(it, now, zone, locale, timeOnlyToday = false),
        )
      }
      ?: ExchangeErrorBody(R.string.play_and_earn_exchange_error_limit_body)

    ExchangeErrorKind.TooManyAttempts -> end
      ?.let {
        ExchangeErrorBody(
          text = R.string.play_and_earn_exchange_error_too_many_attempts_body_until,
          retryAt = formatLimitEnd(it, now, zone, locale, timeOnlyToday = true),
        )
      }
      ?: ExchangeErrorBody(R.string.play_and_earn_exchange_error_too_many_attempts_body)

    ExchangeErrorKind.Generic,
    ExchangeErrorKind.BeingChecked,
    ExchangeErrorKind.Restricted,
    ExchangeErrorKind.Vpn -> null
  }
}

/** The title and body of a redeem error whose text never changes. */
data class ExchangeErrorText(
  @StringRes val title: Int,
  @StringRes val body: Int,
)

/**
 * The fixed text of [kind]; null for the generic error, which keeps the error view's own text,
 * and for the limits, whose body is [exchangeLimitBody].
 */
fun exchangeFixedErrorText(kind: ExchangeErrorKind): ExchangeErrorText? = when (kind) {
  ExchangeErrorKind.BeingChecked -> ExchangeErrorText(
    title = R.string.play_and_earn_exchange_error_being_checked_title,
    body = R.string.play_and_earn_exchange_error_being_checked_body,
  )

  ExchangeErrorKind.Restricted -> ExchangeErrorText(
    title = R.string.play_and_earn_exchange_error_restricted_title,
    body = R.string.play_and_earn_exchange_error_restricted_body,
  )

  // AND-964: the Rewards tab's words for the same refusal.
  ExchangeErrorKind.Vpn -> ExchangeErrorText(
    title = R.string.play_and_earn_refused_title,
    body = R.string.play_and_earn_refused_vpn_body,
  )

  ExchangeErrorKind.Generic,
  ExchangeErrorKind.Limit,
  ExchangeErrorKind.TooManyAttempts -> null
}

/**
 * [end] as the phone writes a date and time in [locale] and [zone]: only the time when
 * [timeOnlyToday] and it falls on the same local day as [now].
 */
fun formatLimitEnd(
  end: Instant,
  now: Instant,
  zone: ZoneId,
  locale: Locale,
  timeOnlyToday: Boolean,
): String {
  val localEnd = end.atZone(zone)
  val sameDay = localEnd.toLocalDate() == now.atZone(zone).toLocalDate()
  val formatter = if (timeOnlyToday && sameDay) {
    DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
  } else {
    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
  }
  // The locale's own digits (Arabic, Persian, Burmese...), as the phone writes dates elsewhere:
  // java.time keeps 0-9 unless told otherwise.
  return localEnd.format(
    formatter.withLocale(locale).withDecimalStyle(DecimalStyle.of(locale))
  )
}
