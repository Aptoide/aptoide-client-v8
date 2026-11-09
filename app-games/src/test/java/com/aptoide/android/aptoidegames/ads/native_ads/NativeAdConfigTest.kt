package com.aptoide.android.aptoidegames.ads.native_ads

import cm.aptoide.pt.test.gherkin.coScenario
import cm.aptoide.pt.test.gherkin.scenario
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.ads.AdGeoProvider
import com.aptoide.android.aptoidegames.ads.AdsDefaults
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class NativeAdConfigTest {

  @Test
  fun `Missing flags default Search MREC deep Home and browse lists on`() =
    coScenario { _ ->
      m Given "no Remote Config values at all"
      val flags = FakeFeatureFlags()

      m When "every placement's config is resolved"
      val configs = NativeAdPlacement.entries.associateWith { NativeAdConfig.from(flags, it) }

      m Then "MREC deep Home and browse are on and other placements follow the build type"
      configs.forEach { (placement, config) ->
        val expected = placement == NativeAdPlacement.SEARCH_MREC ||
          placement == NativeAdPlacement.HOME_DEEP || placement.isBrowseList || BuildConfig.DEBUG
        assertEquals(expected, config.enabled, "enabled for $placement")
        assertEquals(placement.defaultPosition, config.position, "position for $placement")
        assertEquals(NativeAdConfig.DEFAULT_MIN_GAP, config.minGap)
        assertEquals(0, config.minContentLength)
      }
      assertTrue(configs.getValue(NativeAdPlacement.HOME_DEEP).enabled)
    }

  @Test
  fun `Release MREC deep Home and browse default on while other placements stay off`() = scenario {
    m Given "release defaults without remote flags"

    m When "the enabled defaults are resolved"
    val enabled = NativeAdPlacement.entries.filter {
      NativeAdConfig.defaultEnabled(it, isDebug = false)
    }

    m Then "Search MREC deep Home and all three browse slots are enabled"
    assertEquals(
      listOf(
        NativeAdPlacement.HOME_DEEP,
        NativeAdPlacement.CATEGORY_LIST,
        NativeAdPlacement.TOP_CHARTS,
        NativeAdPlacement.SEARCH_MREC,
        NativeAdPlacement.COLLECTION_LIST,
      ), enabled,
    )
  }

  @Test
  fun `An explicit remote flag can disable each default-on slot`() = coScenario { _ ->
    m Given "Search MREC deep Home and browse lists explicitly disabled remotely"
    val placements = listOf(
      NativeAdPlacement.SEARCH_MREC,
      NativeAdPlacement.HOME_DEEP,
      NativeAdPlacement.CATEGORY_LIST,
      NativeAdPlacement.TOP_CHARTS,
      NativeAdPlacement.COLLECTION_LIST,
    )
    val flags = FakeFeatureFlags(
      booleans = placements.associate { NativeAdConfig.enabledKey(it) to false }.toMutableMap(),
    )

    m When "the placement configs are resolved"
    val configs = placements.map { NativeAdConfig.from(flags, it) }

    m Then "each explicit kill switch wins over its enabled default"
    assertTrue(configs.none { it.enabled })
  }

  @Test
  fun `Without a geo key release builds use the consent blocklist and debug builds exclude nothing`() =
    coScenario { _ ->
      m Given "no excluded-geos key"
      val config = NativeAdConfig.from(FakeFeatureFlags(), NativeAdPlacement.HOME_BUNDLE)

      m When "the blocklist is read"
      val expected = if (BuildConfig.DEBUG) emptySet() else AdsDefaults.EXCLUDED_GEOS

      m Then "it is the consent list in release and empty in debug"
      assertEquals(expected.map { it.uppercase() }.toSet(), config.excludedGeos)
    }

  @Test
  fun `Unknown geo only narrows eligibility`() = scenario {
    m Given "a config with a blocklist and one without"
    val blocklisted = NativeAdConfig(true, 2, setOf("US", "DE"), 0, 3)
    val open = NativeAdConfig(true, 2, emptySet(), 0, 3)

    m When "eligibility is checked for a blocked, an allowed and an unknown country"
    val results = listOf(
      blocklisted.isGeoEligible("us"), blocklisted.isGeoEligible("BR"),
      blocklisted.isGeoEligible(AdGeoProvider.UNKNOWN_GEO),
      open.isGeoEligible("US"), open.isGeoEligible(AdGeoProvider.UNKNOWN_GEO),
    )

    m Then "unknown is blocked only when a blocklist exists; known countries are unchanged"
    assertEquals(listOf(false, true, false, true, true), results)
  }

  @Test
  fun `Negative remote values are coerced to zero`() = coScenario { _ ->
    m Given "negative position, gap and content length"
    val flags = FakeFeatureFlags(
      ints = mutableMapOf(
        NativeAdConfig.positionKey(NativeAdPlacement.TOP_CHARTS) to -3,
        NativeAdConfig.minGapKey(NativeAdPlacement.TOP_CHARTS) to -1,
        NativeAdConfig.minContentLengthKey(NativeAdPlacement.TOP_CHARTS) to -500,
      ),
    )

    m When "the config is resolved"
    val config = NativeAdConfig.from(flags, NativeAdPlacement.TOP_CHARTS)

    m Then "all three are zero"
    assertEquals(0, config.position)
    assertEquals(0, config.minGap)
    assertEquals(0, config.minContentLength)
  }

  @Test
  fun `Declared ad unit ids contain no blanks or duplicates and the MREC id is never a native id`() =
    scenario {
      m Given "the placements"

      m When "the declared ids are collected"
      val declared = NativeAdPlacement.declaredAdUnitIds()
      val mrecId = NativeAdPlacement.SEARCH_MREC.adUnitId
      val nativeIds = NativeAdPlacement.entries
        .filter { it != NativeAdPlacement.SEARCH_MREC }.map { it.adUnitId }.filter { it.isNotBlank() }

      m Then "none is blank, none repeats and a configured MREC id is distinct from every native id"
      assertTrue(declared.none { it.isBlank() })
      assertEquals(declared.size, declared.toSet().size)
      if (mrecId.isNotBlank()) assertFalse(mrecId in nativeIds)
    }
}
