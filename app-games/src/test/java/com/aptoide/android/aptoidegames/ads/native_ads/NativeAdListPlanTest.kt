package com.aptoide.android.aptoidegames.ads.native_ads

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class NativeAdListPlanTest {

  @Test
  fun `The ad goes after the configured number of organic rows`() = scenario {
    m Given "a list of 10 apps and position 3"

    m When "the plan is computed with a loaded ad"
    val index = NativeAdListPlan.adIndex(itemCount = 10, position = 3, hasAd = true)

    m Then "three organic rows precede the ad"
    assertEquals(3, index)
  }

  @Test
  fun `Short lists and missing ads get no slot`() = scenario {
    m Given "a two-item list and position 3"

    m When "the plan is computed with and without an ad"
    val tooShort = NativeAdListPlan.adIndex(itemCount = 2, position = 3, hasAd = true)
    val noAd = NativeAdListPlan.adIndex(itemCount = 10, position = 3, hasAd = false)
    val exact = NativeAdListPlan.adIndex(itemCount = 3, position = 3, hasAd = true)

    m Then "only the list with enough rows and an ad gets an index"
    assertNull(tooShort)
    assertNull(noAd)
    assertEquals(3, exact)
  }

  @Test
  fun `Position zero still keeps one organic row first`() = scenario {
    m Given "position 0"

    m When "the plan is computed"
    val index = NativeAdListPlan.adIndex(itemCount = 5, position = 0, hasAd = true)
    val shouldRequest = NativeAdListPlan.shouldRequest(itemCount = 0, position = 0)

    m Then "the ad is never the first row and an empty list never requests"
    assertEquals(1, index)
    assertFalse(shouldRequest)
  }

  @Test
  fun `Request gating follows the same threshold as insertion`() = scenario {
    m Given "position 3"

    m When "lists of different sizes are checked"
    val two = NativeAdListPlan.shouldRequest(itemCount = 2, position = 3)
    val three = NativeAdListPlan.shouldRequest(itemCount = 3, position = 3)

    m Then "only lists with at least three rows request"
    assertFalse(two)
    assertTrue(three)
  }

  @Test
  fun `Each placement has its own stable list key`() = scenario {
    m Given "the three browsing placements"

    m When "their keys are read"
    val keys = listOf(
      NativeAdPlacement.CATEGORY_LIST, NativeAdPlacement.TOP_CHARTS, NativeAdPlacement.COLLECTION_LIST,
    ).map { NativeAdListPlan.adKey(it) }

    m Then "they are distinct and never collide with a package name"
    assertEquals(3, keys.toSet().size)
    assertTrue(keys.all { it.startsWith("native_ad_") })
  }

  @Test
  fun `A slot is active only while resumed, long enough and near the visible range`() = scenario {
    m Given "a placement whose default position is 3 and a remote position of 8"

    m When "activity is computed for resumed, paused, shrinking and far-scrolled lists"
    val resumedDefault = NativeAdListPlan.shouldBeActive(true, 5, null, 3, nearVisible = true)
    val pausedDefault = NativeAdListPlan.shouldBeActive(false, 5, null, 3, nearVisible = true)
    val remoteBeyondList = NativeAdListPlan.shouldBeActive(true, 5, 8, 3, nearVisible = true)
    val remoteFits = NativeAdListPlan.shouldBeActive(true, 9, 8, 3, nearVisible = true)
    val shrunk = NativeAdListPlan.shouldBeActive(true, 2, 3, 3, nearVisible = true)
    val scrolledFarPast = NativeAdListPlan.shouldBeActive(true, 30, 3, 3, nearVisible = false)

    m Then "only a resumed host, enough rows and a near slot keep it active"
    assertTrue(resumedDefault)
    assertFalse(pausedDefault)
    assertFalse(remoteBeyondList)
    assertTrue(remoteFits)
    assertFalse(shrunk)
    assertFalse(scrolledFarPast)
  }

  @Test
  fun `The slot index is the effective position, at least one`() = scenario {
    m Given "remote, default and zero positions"

    m When "the lazy index of the slot is read"
    val remote = NativeAdListPlan.slotIndex(configuredPosition = 6, defaultPosition = 3)
    val default = NativeAdListPlan.slotIndex(configuredPosition = null, defaultPosition = 3)
    val zero = NativeAdListPlan.slotIndex(configuredPosition = 0, defaultPosition = 3)

    m Then "it follows the remote value, falls back to the default and is never the first row"
    assertEquals(6, remote)
    assertEquals(3, default)
    assertEquals(1, zero)
  }

  @Test
  fun `Only ordinary browse placements outside RTB lists may carry an ad`() = scenario {
    m Given "every placement and both RTB states"

    m When "eligibility is evaluated"
    val browse = listOf(
      NativeAdPlacement.CATEGORY_LIST, NativeAdPlacement.TOP_CHARTS, NativeAdPlacement.COLLECTION_LIST,
    ).map { BrowseAdEligibility.allowed(it, isRtbList = false) }
    val rtb = NativeAdPlacement.entries.map { BrowseAdEligibility.allowed(it, isRtbList = true) }
    val nonBrowse = listOf(
      NativeAdPlacement.HOME_BUNDLE, NativeAdPlacement.SEARCH_LANDING, NativeAdPlacement.APP_DETAIL,
      NativeAdPlacement.SEARCH_MREC,
    ).map { BrowseAdEligibility.allowed(it, isRtbList = false) }

    m Then "browse lists pass, RTB lists and non-browse placements never do"
    assertTrue(browse.all { it })
    assertTrue(rtb.none { it })
    assertTrue(nonBrowse.none { it })
  }

  @Test
  fun `Organic keys are unique and never move when an ad is inserted`() = scenario {
    m Given "a list that contains the same package twice"
    val ids = listOf("a.one", "b.two", "a.one", "c.three", "a.one")

    m When "keys are computed"
    val keys = NativeAdListPlan.uniqueKeys(ids)

    m Then "they are unique, stable per index, and the first occurrence keeps the plain package name"
    assertEquals(listOf("a.one", "b.two", "a.one#2", "c.three", "a.one#3"), keys)
    assertEquals(keys.size, keys.toSet().size)
    assertTrue(NativeAdListPlan.adKey(NativeAdPlacement.TOP_CHARTS) !in keys)
  }

  @Test
  fun `Near viewport is a live condition, not visible once`() = scenario {
    m Given "a 2000px screen with a 400px preload and a 300px slot"

    m When "the slot is below the preload, inside it, on screen, scrolled just past, and far past"
    val below = NativeAdVisibility.isNearViewport(top = 2500f, height = 300f, screenHeight = 2000f, preload = 400f)
    val approaching = NativeAdVisibility.isNearViewport(top = 2300f, height = 300f, screenHeight = 2000f, preload = 400f)
    val onScreen = NativeAdVisibility.isNearViewport(top = 800f, height = 300f, screenHeight = 2000f, preload = 400f)
    val justPast = NativeAdVisibility.isNearViewport(top = -500f, height = 300f, screenHeight = 2000f, preload = 400f)
    val farPast = NativeAdVisibility.isNearViewport(top = -1500f, height = 300f, screenHeight = 2000f, preload = 400f)
    val emptyPast = NativeAdVisibility.isNearViewport(top = -900f, height = 0f, screenHeight = 2000f, preload = 400f)

    m Then "only slots within the preload band on either side are near"
    assertFalse(below)
    assertTrue(approaching)
    assertTrue(onScreen)
    assertTrue(justPast)
    assertFalse(farPast)
    assertFalse(emptyPast)
  }
}
