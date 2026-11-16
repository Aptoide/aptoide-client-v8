package com.aptoide.android.aptoidegames.ads.native_ads

import cm.aptoide.pt.feature_home.domain.Type
import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class HomeNativeAdLayoutTest {

  private val plain = List(10) { Type.APP_GRID }

  @Test
  fun `The deep slot keeps the minimum gap from the first slot`() = scenario {
    m Given "a first ad at index 2 and a requested deep index of 3 with a gap of 3"

    m When "the deep index is computed"
    val index = HomeNativeAdLayout.deepIndex(
      bundleTypes = plain, firstIndex = 2, requestedIndex = 3, minGap = 3,
    )

    m Then "the ad is pushed to index 5, three bundles after the first ad"
    assertEquals(5, index)
  }

  @Test
  fun `The deep slot is never adjacent to a sponsored bundle`() = scenario {
    m Given "an RTB bundle at index 6 and a Play and Earn bundle at index 8"
    val types = plain.toMutableList().apply {
      this[6] = Type.RTB_PROMO
      this[8] = Type.PLAY_AND_EARN
    }

    m When "the deep index is computed for index 6"
    val index = HomeNativeAdLayout.deepIndex(
      bundleTypes = types, firstIndex = 2, requestedIndex = 6, minGap = 3,
    )

    m Then "indices 6, 7, 8 and 9 are rejected (sponsored below or above) and there is no spot"
    assertNull(index)
  }

  @Test
  fun `A sponsored bundle shifts the deep slot past it`() = scenario {
    m Given "a bonus banner at index 6 in a 12-bundle feed"
    val types = List(12) { Type.APP_GRID }.toMutableList().apply { this[6] = Type.APPC_BANNER }

    m When "the deep index is computed for index 6"
    val index = HomeNativeAdLayout.deepIndex(
      bundleTypes = types, firstIndex = 2, requestedIndex = 6, minGap = 3,
    )

    m Then "the ad lands at index 8, with the banner neither above nor below it"
    assertEquals(8, index)
  }

  @Test
  fun `Without a first ad the gap counts from the top`() = scenario {
    m Given "no first ad and a requested index of 1 with gap 3"

    m When "the deep index is computed"
    val index = HomeNativeAdLayout.deepIndex(
      bundleTypes = plain, firstIndex = null, requestedIndex = 1, minGap = 3,
    )

    m Then "the ad waits for the third bundle"
    assertEquals(2, index)
  }

  @Test
  fun `Empty feeds and the first slot clamp as before`() = scenario {
    m Given "an empty feed and a short feed"

    m When "both indices are computed"
    val emptyDeep = HomeNativeAdLayout.deepIndex(emptyList(), 2, 6, 3)
    val emptyFirst = HomeNativeAdLayout.firstIndex(bundleTypes = emptyList(), position = 2)
    val clampedFirst = HomeNativeAdLayout.firstIndex(bundleTypes = plain.take(2), position = 5)

    m Then "empty feeds get nothing and the first slot clamps to the last bundle"
    assertNull(emptyDeep)
    assertNull(emptyFirst)
    assertEquals(1, clampedFirst)
  }

  @Test
  fun `Home slot activity follows the live visible range`() = scenario {
    m Given "a feed where the visible bundles are 4 to 7"

    m When "near-visibility is checked for ads at different bundle indices"
    val inside = HomeNativeAdLayout.isNearVisible(5, firstVisible = 4, lastVisible = 7)
    val oneAbove = HomeNativeAdLayout.isNearVisible(3, firstVisible = 4, lastVisible = 7)
    val oneBelow = HomeNativeAdLayout.isNearVisible(8, firstVisible = 4, lastVisible = 7)
    val farAbove = HomeNativeAdLayout.isNearVisible(1, firstVisible = 4, lastVisible = 7)
    val farBelow = HomeNativeAdLayout.isNearVisible(12, firstVisible = 4, lastVisible = 7)
    val noLocation = HomeNativeAdLayout.isNearVisible(null, firstVisible = 4, lastVisible = 7)

    m Then "slots within one bundle of the range are near; far ones and a missing location are not"
    assertTrue(inside)
    assertTrue(oneAbove)
    assertTrue(oneBelow)
    assertFalse(farAbove)
    assertFalse(farBelow)
    assertFalse(noLocation)
  }

  @Test
  fun `The first slot is never adjacent to a sponsored bundle either`() = scenario {
    m Given "an RTB bundle at index 2 and a takeover at index 4 in a 10-bundle feed"
    val types = plain.toMutableList().apply {
      this[2] = Type.RTB_PROMO
      this[4] = Type.PUBLISHER_TAKEOVER
    }

    m When "the first index is computed for position 2, and for a feed that is sponsored to the end"
    val shifted = HomeNativeAdLayout.firstIndex(bundleTypes = types, position = 2)
    val noSpot = HomeNativeAdLayout.firstIndex(
      bundleTypes = listOf(Type.APP_GRID, Type.RTB_PROMO, Type.APPC_BANNER), position = 1,
    )

    m Then "indices 2 (RTB), 3 (RTB above), 4 (takeover) and 5 (takeover above) are skipped; no spot gives null"
    assertEquals(6, shifted)
    assertNull(noSpot)
  }

  @Test
  fun `Minimum gap and out-of-range requests are handled`() = scenario {
    m Given "a plain feed with a first ad at index 2"

    m When "the deep index is computed with gap 0 and with a request past the end"
    val gapZero = HomeNativeAdLayout.deepIndex(plain, firstIndex = 2, requestedIndex = 0, minGap = 0)
    val pastEnd = HomeNativeAdLayout.deepIndex(plain, firstIndex = 2, requestedIndex = 40, minGap = 3)

    m Then "the deep ad still leaves one bundle between the ads and a request past the end gives null"
    assertEquals(3, gapZero)
    assertNull(pastEnd)
  }
}
