package com.aptoide.android.aptoidegames.play_and_earn.presentation.overlays

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.random.Random

// The numbers of the Claim animation in the mission-completed overlay (Hackathon 2026, T5), as
// in the designer's prototype. Distances are in dp, 1:1 with the prototype's px.

internal const val CLAIM_COINS = 12
internal const val SLIDE_MILLIS = 380
internal const val BALANCE_COUNT_MILLIS = 500
private const val COIN_STAGGER_MILLIS = 45
private const val COIN_MIN_MILLIS = 900
private const val COIN_EXTRA_MILLIS = 250

/** When the last of the [CLAIM_COINS] coins has landed, at the latest. */
internal const val COINS_FLIGHT_MILLIS =
  (CLAIM_COINS - 1) * COIN_STAGGER_MILLIS + COIN_MIN_MILLIS + COIN_EXTRA_MILLIS

private const val SPREAD_END = 0.35f
private const val FLIGHT_END = 0.95f

/** Where one coin spreads to (relative to the Claim button's centre) and when it flies. */
internal data class CoinFlightPlan(
  val spreadX: Float,
  val spreadY: Float,
  val durationMillis: Int,
  val delayMillis: Int,
)

/** A coin at one moment: offset from the Claim button's centre, scale and opacity. */
internal data class CoinFrame(
  val x: Float,
  val y: Float,
  val scale: Float,
  val alpha: Float,
)

/** Coins leave in a ring around the button, slightly downwards, one every 45 ms. */
internal fun coinFlightPlan(index: Int, count: Int, random: Random): CoinFlightPlan {
  val angle = 2 * PI * index / count + random.nextFloat() * 0.5
  val distance = 40 + random.nextFloat() * 40
  return CoinFlightPlan(
    spreadX = (cos(angle) * distance).toFloat(),
    spreadY = (sin(angle) * distance).toFloat() + 20,
    durationMillis = COIN_MIN_MILLIS + random.nextInt(COIN_EXTRA_MILLIS + 1),
    delayMillis = index * COIN_STAGGER_MILLIS,
  )
}

/**
 * The coin at [progress] (0 to 1) of its flight: it grows while spreading out, flies to the
 * balance at ([targetX], [targetY]) while shrinking, and fades out there. [easing] shapes each of
 * the three legs, as the prototype's keyframes do.
 */
internal fun coinFrame(
  progress: Float,
  plan: CoinFlightPlan,
  targetX: Float,
  targetY: Float,
  easing: (Float) -> Float,
): CoinFrame {
  val p = progress.coerceIn(0f, 1f)
  return when {
    p <= SPREAD_END -> {
      val k = easing(p / SPREAD_END)
      CoinFrame(
        x = lerp(0f, plan.spreadX, k),
        y = lerp(0f, plan.spreadY, k),
        scale = lerp(0.4f, 1f, k),
        alpha = k,
      )
    }

    p <= FLIGHT_END -> {
      val k = easing((p - SPREAD_END) / (FLIGHT_END - SPREAD_END))
      CoinFrame(
        x = lerp(plan.spreadX, targetX, k),
        y = lerp(plan.spreadY, targetY, k),
        scale = lerp(1f, 0.6f, k),
        alpha = 1f,
      )
    }

    else -> {
      val k = easing((p - FLIGHT_END) / (1f - FLIGHT_END))
      CoinFrame(x = targetX, y = targetY, scale = lerp(0.6f, 0.3f, k), alpha = 1f - k)
    }
  }
}

private fun lerp(from: Float, to: Float, k: Float) = from + (to - from) * k

/**
 * What the balance chip counts through while the coins land. [isWalletBalance] is false when the
 * wallet could not be read: the chip then counts the reward alone, shown as "+N".
 */
internal data class BalanceCount(
  val from: Long,
  val to: Long,
  val isWalletBalance: Boolean,
) {
  fun at(progress: Float): Long =
    (from + (to - from) * progress.coerceIn(0f, 1f).toDouble()).roundToLong()
}

/**
 * The server credits a mission before the overlay shows, so the wallet's balance, [balanceAfter],
 * already holds the reward: the count starts [units] below it.
 */
internal fun claimBalanceCount(balanceAfter: Long?, units: Int): BalanceCount =
  if (balanceAfter == null) {
    BalanceCount(from = 0, to = units.toLong(), isWalletBalance = false)
  } else {
    BalanceCount(
      from = (balanceAfter - units).coerceAtLeast(0),
      to = balanceAfter,
      isWalletBalance = true,
    )
  }
