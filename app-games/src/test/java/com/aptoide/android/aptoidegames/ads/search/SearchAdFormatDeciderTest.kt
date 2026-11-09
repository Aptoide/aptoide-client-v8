package com.aptoide.android.aptoidegames.ads.search

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class SearchAdFormatDeciderTest {

  private val on = SearchAdExperimentConfig(
    enabled = true, mrecAllocationPercent = 50, mrecAutoRefresh = false,
  )

  @Test
  fun `The same installation always lands in the same bucket`() = scenario {
    m Given "an installation id"
    val id = "6f9a1c2e-stable-installation"

    m When "the bucket is computed repeatedly"
    val buckets = (1..5).map { SearchAdFormatDecider.bucket(id) }

    m Then "it never changes and stays within 0..99"
    assertEquals(1, buckets.toSet().size)
    assertTrue(buckets.first() in 0 until SearchAdFormatDecider.BUCKETS)
  }

  @Test
  fun `Allocation splits installations by bucket`() = scenario {
    m Given "a thousand installations and a 50 percent allocation"
    val ids = (1..1000).map { "install-$it" }

    m When "each is assigned"
    val mrec = ids.count { SearchAdFormatDecider.decide(on, "mrec-unit", it) == SearchAdFormat.MREC }

    m Then "roughly half get the MREC arm and every assignment is one of the two formats"
    assertTrue(mrec in 400..600, "mrec share was $mrec")
  }

  @Test
  fun `Kill switch, blank unit and zero allocation all select the native control`() = scenario {
    m Given "an installation that would fall in the MREC arm"
    val id = (1..200).map { "install-$it" }
      .first { SearchAdFormatDecider.bucket(it) < 50 }

    m When "the experiment is off, the unit is blank, or the allocation is zero"
    val off = SearchAdFormatDecider.decide(on.copy(enabled = false), "mrec-unit", id)
    val blankUnit = SearchAdFormatDecider.decide(on, "", id)
    val zero = SearchAdFormatDecider.decide(on.copy(mrecAllocationPercent = 0), "mrec-unit", id)
    val noId = SearchAdFormatDecider.decide(on, "mrec-unit", null)
    val blankId = SearchAdFormatDecider.decide(on, "mrec-unit", "   ")

    m Then "every one of them is the native control"
    listOf(off, blankUnit, zero, noId, blankId).forEach { assertEquals(SearchAdFormat.NATIVE, it) }
  }

  @Test
  fun `Full allocation sends every installation to MREC when the unit exists`() = scenario {
    m Given "a 100 percent allocation"
    val all = on.copy(mrecAllocationPercent = 100)

    m When "several installations are assigned"
    val formats = (1..50).map { SearchAdFormatDecider.decide(all, "mrec-unit", "install-$it") }

    m Then "all of them get the MREC treatment"
    assertTrue(formats.all { it == SearchAdFormat.MREC })
  }

  @Test
  fun `The shipped default selects MREC and a missing unit still selects native`() = scenario {
    m Given "the default Search configuration"
    val config = SearchAdExperimentConfig.DEFAULT

    m When "the unit is configured or absent"
    val configured = SearchAdFormatDecider.decide(config, "mrec-unit", null)
    val missing = SearchAdFormatDecider.decide(config, "", null)
    val rollback = SearchAdFormatDecider.decide(SearchAdExperimentConfig.DISABLED, "mrec-unit", null)

    m Then "MREC is the default and both explicit rollback and missing unit use native"
    assertEquals(SearchAdFormat.MREC, configured)
    assertEquals(SearchAdFormat.NATIVE, missing)
    assertEquals(SearchAdFormat.NATIVE, rollback)
  }

  @Test
  fun `Full MREC allocation does not depend on an installation id`() = scenario {
    m Given "the default all-MREC allocation and a configured MREC unit"
    val all = on.copy(mrecAllocationPercent = 100)

    m When "the installation id is unavailable or blank"
    val formats = listOf(null, "", "   ").map {
      SearchAdFormatDecider.decide(all, "mrec-unit", it)
    }

    m Then "every eligible opportunity still selects MREC"
    assertTrue(formats.all { it == SearchAdFormat.MREC })
  }

  @Test
  fun `Allocation outside 0 to 100 is clamped at both ends`() = scenario {
    m Given "allocations of minus five and 250 percent"
    val under = on.copy(mrecAllocationPercent = -5)
    val over = on.copy(mrecAllocationPercent = 250)

    m When "a thousand installations are assigned under each"
    val ids = (1..1000).map { "install-$it" }
    val underMrec = ids.count { SearchAdFormatDecider.decide(under, "mrec-unit", it) == SearchAdFormat.MREC }
    val overMrec = ids.count { SearchAdFormatDecider.decide(over, "mrec-unit", it) == SearchAdFormat.MREC }

    m Then "a negative allocation gives nobody the MREC and an excessive one gives it to everyone"
    assertEquals(0, underMrec)
    assertEquals(1000, overMrec)
  }

  @Test
  fun `Buckets are pinned so a release never reshuffles the arms`() = scenario {
    m Given "a few installation ids"
    val ids = listOf("install-1", "install-2", "6f9a1c2e-stable-installation", "guest-uid-42")

    m When "their buckets are computed"
    val buckets = ids.map { SearchAdFormatDecider.bucket(it) }

    m Then "they match the values recorded when the experiment was built"
    assertEquals(GOLDEN_BUCKETS, buckets)
  }

  @Test
  fun `Raising the allocation only adds installations to the MREC arm`() = scenario {
    m Given "a thousand installations"
    val ids = (1..1000).map { "install-$it" }

    m When "the MREC arm is computed at 10 and at 20 percent"
    val atTen = ids.filter {
      SearchAdFormatDecider.decide(on.copy(mrecAllocationPercent = 10), "mrec-unit", it) == SearchAdFormat.MREC
    }.toSet()
    val atTwenty = ids.filter {
      SearchAdFormatDecider.decide(on.copy(mrecAllocationPercent = 20), "mrec-unit", it) == SearchAdFormat.MREC
    }.toSet()

    m Then "every installation in the MREC arm at 10 percent is still there at 20"
    assertTrue(atTwenty.containsAll(atTen))
    assertTrue(atTwenty.size > atTen.size)
  }

  @Test
  fun `Each decision exposes exactly one requesting format`() = scenario {
    m Given "a native and an MREC decision"
    val native = SearchAdDecision(SearchAdFormat.NATIVE, "mrec-unit", false)
    val mrec = SearchAdDecision(SearchAdFormat.MREC, "mrec-unit", false)

    m When "the request flags are read"
    val flags = listOf(native.requestsNative to native.requestsMrec, mrec.requestsNative to mrec.requestsMrec)

    m Then "never both, and an MREC decision never allows a native request"
    assertEquals(listOf(true to false, false to true), flags)
  }

  private companion object {
    /**
     * Literal values recorded when the experiment was built (salt "search_ad_format:", Java string
     * hash, Int wrap, modulo 100). A change here reshuffles every installation's arm mid-experiment.
     */
    val GOLDEN_BUCKETS = listOf(13, 14, 46, 78)
  }
}
