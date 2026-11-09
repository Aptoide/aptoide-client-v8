package com.aptoide.android.aptoidegames.ads.native_ads

import cm.aptoide.pt.test.gherkin.coScenario
import com.aptoide.android.aptoidegames.ads.AdGeoProvider
import com.aptoide.android.aptoidegames.ads.AdRetryPolicy
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class NativeAdControllerTest {

  private val placement = NativeAdPlacement.CATEGORY_LIST
  private val policy = AdRetryPolicy(baseDelayMs = 2_000, maxDelayMs = 32_000, maxAttempts = 4)

  private class Harness(
    scope: TestScope,
    adUnitId: String = "unit-1",
    distributionEnabled: Boolean = true,
    val flags: FakeFeatureFlags = FakeFeatureFlags(),
    geo: String = "BR",
    sdkInit: suspend () -> Boolean = { true },
    hasSdkKey: () -> Boolean = { true },
    sdkInitTimeoutMs: Long = 15_000,
    policy: AdRetryPolicy,
    placement: NativeAdPlacement,
  ) {
    init {
      // Deterministic regardless of build type: Remote Config says enabled unless a test overrides it.
      flags.booleans.putIfAbsent(NativeAdConfig.enabledKey(placement), true)
    }

    val dispatcher = StandardTestDispatcher(scope.testScheduler)
    val analytics = RecordingAnalytics()
    val factory = FakeLoaderFactory()
    val controller = NativeAdController(
      placement = placement,
      adUnitId = adUnitId,
      distributionEnabled = distributionEnabled,
      featureFlags = flags,
      geoProvider = AdGeoProvider { geo },
      sdkInitializer = sdkInit,
      loaderFactory = factory,
      analytics = NativeAdAnalytics(analytics.generic(), placement),
      scope = CoroutineScope(dispatcher + SupervisorJob()),
      mainDispatcher = dispatcher,
      retryPolicy = policy,
      now = { scope.testScheduler.currentTime },
      hasSdkKey = hasSdkKey,
      sdkInitTimeoutMs = sdkInitTimeoutMs,
    )
  }

  @Test
  fun `Blank ad unit id is a permanent skip and never creates a loader`() = coScenario { scope ->
    m Given "a placement with no ad unit id"
    val h = Harness(scope, adUnitId = "", policy = policy, placement = placement)
    h.controller.setActive(true)

    m When "a load is requested twice"
    h.controller.requestLoad()
    scope.advanceUntilIdle()
    h.controller.requestLoad()
    scope.advanceUntilIdle()

    m Then "nothing is requested and the skip reason is reported once"
    assertTrue(h.factory.loaders.isEmpty())
    assertEquals("no_ad_unit_id", h.controller.lastSkipReason)
    assertEquals(listOf("browse_native_category_skipped"), h.analytics.names())
  }

  @Test
  fun `A disabled flag is re-evaluated later instead of latching`() = coScenario { scope ->
    m Given "the placement flag is off"
    val h = Harness(scope, policy = policy, placement = placement)
    h.flags.booleans["browse_native_category_enabled"] = false
    h.controller.setActive(true)
    h.controller.requestLoad()
    scope.advanceUntilIdle()
    assertEquals("flag_disabled", h.controller.lastSkipReason)
    assertTrue(h.factory.loaders.isEmpty())

    m When "the flag turns on and a request comes after the re-evaluation interval"
    h.flags.booleans["browse_native_category_enabled"] = true
    h.controller.requestLoad()
    scope.advanceUntilIdle()
    val loadsBeforeInterval = h.factory.totalLoads
    scope.advanceTimeBy(policy.reevaluateIntervalMs + 1)
    h.controller.requestLoad()
    scope.advanceUntilIdle()

    m Then "the early request is ignored and the later one loads exactly once"
    assertEquals(0, loadsBeforeInterval)
    assertEquals(1, h.factory.totalLoads)
  }

  @Test
  fun `SDK initialization failure retries with backoff and then loads`() = coScenario { scope ->
    m Given "an SDK that fails once and then initializes"
    var calls = 0
    val h = Harness(
      scope, policy = policy, placement = placement, sdkInit = { ++calls > 1 },
    )
    h.controller.setActive(true)

    m When "a load is requested and the first backoff elapses"
    h.controller.requestLoad()
    scope.runCurrent()
    val loadsBeforeBackoff = h.factory.totalLoads
    scope.advanceTimeBy(policy.baseDelayMs + 1)
    scope.advanceUntilIdle()

    m Then "one failure is reported, then a single request on the retry"
    assertEquals(0, loadsBeforeBackoff)
    assertEquals(1, h.factory.totalLoads)
    val failed = h.analytics.events.filter { it.first.endsWith("_failed") }
    assertEquals(1, failed.size)
    assertEquals(NativeAdController.SDK_INIT_ERROR, failed.first().second?.get("error_code"))
    assertEquals(1, h.analytics.events.last { it.first.endsWith("_requested") }.second?.get("attempt"))
  }

  @Test
  fun `Load failures retry a bounded number of times with exponential delays`() = coScenario { scope ->
    m Given "a loader that always fails"
    val h = Harness(scope, policy = policy, placement = placement)
    h.controller.setActive(true)
    h.controller.requestLoad()
    scope.runCurrent()
    val loader = h.factory.last!!

    m When "every attempt fails and time advances to just before and then onto each backoff"
    val expectedDelays = listOf(2_000L, 4_000L, 8_000L, 16_000L)
    val loadsJustBefore = mutableListOf<Int>()
    val loadsOnTime = mutableListOf<Int>()
    expectedDelays.forEach { delayMs ->
      loader.callbacks.onLoadFailed(-5001, "no fill")
      scope.advanceTimeBy(delayMs - 1)
      scope.runCurrent()
      loadsJustBefore += loader.loadCalls
      scope.advanceTimeBy(1)
      scope.runCurrent()
      loadsOnTime += loader.loadCalls
    }
    loader.callbacks.onLoadFailed(-5001, "no fill")
    scope.advanceTimeBy(policy.maxDelayMs * 4)
    scope.runCurrent()

    m Then "each retry fires exactly at its exponential delay and the fifth failure is final"
    assertEquals(listOf(1, 2, 3, 4), loadsJustBefore)
    assertEquals(listOf(2, 3, 4, 5), loadsOnTime)
    assertEquals(5, loader.loadCalls)
    assertEquals(1, h.factory.loaders.size)
    assertEquals(NativeAdUiState.Hidden, h.controller.uiState.value)
    val requestedAttempts = h.analytics.events
      .filter { it.first == "browse_native_category_requested" }.map { it.second?.get("attempt") }
    assertEquals(listOf(0, 1, 2, 3, 4), requestedAttempts)
  }

  @Test
  fun `Retries wait while the slot is inactive and resume on activation`() = coScenario { scope ->
    m Given "a failed load with a pending retry"
    val h = Harness(scope, policy = policy, placement = placement)
    h.controller.setActive(true)
    h.controller.requestLoad()
    scope.advanceUntilIdle()
    val loader = h.factory.last!!
    loader.callbacks.onLoadFailed(-1009, "network")

    m When "the slot goes offscreen before the backoff elapses, then comes back"
    h.controller.setActive(false)
    scope.advanceTimeBy(policy.maxDelayMs * 4)
    scope.advanceUntilIdle()
    val loadsWhileInactive = loader.loadCalls
    h.controller.setActive(true)
    scope.advanceUntilIdle()

    m Then "no request is made offscreen and one is made on activation"
    assertEquals(1, loadsWhileInactive)
    assertEquals(2, loader.loadCalls)
  }

  @Test
  fun `Deactivation during gate evaluation defers the request`() = coScenario { scope ->
    m Given "SDK initialization that is still pending when the slot is deactivated"
    val gate = CompletableDeferred<Boolean>()
    val h = Harness(scope, policy = policy, placement = placement, sdkInit = { gate.await() })
    h.controller.setActive(true)
    h.controller.requestLoad()
    // runCurrent, not advanceUntilIdle: the latter would run through the SDK-init timeout.
    scope.runCurrent()

    m When "the slot deactivates, initialization completes, then the slot reactivates"
    h.controller.setActive(false)
    gate.complete(true)
    scope.runCurrent()
    val loadsWhileInactive = h.factory.totalLoads
    h.controller.setActive(true)
    scope.runCurrent()

    m Then "nothing was requested offscreen and exactly one request follows reactivation"
    assertEquals(0, loadsWhileInactive)
    assertEquals(1, h.factory.totalLoads)
  }

  @Test
  fun `A loaded ad is exposed, replaced ads are destroyed, stale binds are refused`() =
    coScenario { scope ->
      m Given "a first loaded ad"
      val h = Harness(scope, policy = policy, placement = placement)
      h.controller.setActive(true)
      h.controller.requestLoad()
      scope.advanceUntilIdle()
      val loader = h.factory.last!!
      val first = fakeMaxAd("Liftoff")
      loader.callbacks.onLoaded(first)
      val boundFirst = h.controller.bind(first) { true }
      val boundTwice = h.controller.bind(first) { true }

      m When "the ad expires and a second ad loads"
      loader.callbacks.onExpired(first)
      val stateAfterExpiry = h.controller.uiState.value
      scope.advanceUntilIdle()
      val second = fakeMaxAd("Mintegral")
      loader.callbacks.onLoaded(second)

      m Then "the first ad was destroyed, the second is current and the first cannot be bound"
      assertTrue(boundFirst)
      assertTrue(boundTwice)
      assertEquals(NativeAdUiState.Hidden, stateAfterExpiry)
      assertEquals(listOf(first), loader.destroyedAds)
      assertEquals(2, loader.loadCalls)
      assertEquals(second, (h.controller.uiState.value as NativeAdUiState.Loaded).ad)
      assertFalse(h.controller.bind(first) { true })
      assertEquals(1, h.analytics.names().count { it.endsWith("_rendered") })
    }

  @Test
  fun `A stale expiry only disposes the old ad and leaves the current one alone`() =
    coScenario { scope ->
      m Given "an ad that was already replaced by a newer one"
      val h = Harness(scope, policy = policy, placement = placement)
      h.controller.setActive(true)
      h.controller.requestLoad()
      scope.advanceUntilIdle()
      val loader = h.factory.last!!
      val old = fakeMaxAd("A")
      loader.callbacks.onLoaded(old)
      val current = fakeMaxAd("B")
      loader.callbacks.onLoaded(current)
      val loadsBefore = loader.loadCalls

      m When "the expiry callback for the old ad arrives late"
      loader.callbacks.onExpired(old)
      scope.advanceUntilIdle()

      m Then "the old ad is disposed, no new request is made and the current ad stays"
      assertTrue(old in loader.destroyedAds)
      assertFalse(current in loader.destroyedAds)
      assertEquals(loadsBefore, loader.loadCalls)
      assertEquals(current, (h.controller.uiState.value as NativeAdUiState.Loaded).ad)
    }

  @Test
  fun `A response arriving after release is disposed and never shown`() = coScenario { scope ->
    m Given "a released controller with a request in flight"
    val h = Harness(scope, policy = policy, placement = placement)
    h.controller.setActive(true)
    h.controller.requestLoad()
    scope.advanceUntilIdle()
    val loader = h.factory.last!!
    h.controller.release()

    m When "the loader answers late"
    val late = fakeMaxAd("Late")
    loader.callbacks.onLoaded(late)
    scope.advanceUntilIdle()

    m Then "the late ad is destroyed, the loader was destroyed and the state stays hidden"
    assertTrue(loader.destroyed)
    assertEquals(listOf(late), loader.destroyedAds)
    assertEquals(NativeAdUiState.Hidden, h.controller.uiState.value)
    assertFalse(h.controller.bind(late) { true })
  }

  @Test
  fun `Geo exclusion and distribution gate skip before any request`() = coScenario { scope ->
    m Given "a consent geo in the blocklist and a disabled distribution"
    val geoBlocked = Harness(scope, policy = policy, placement = placement, geo = "DE").also {
      it.flags.lists["browse_native_category_excluded_geos"] = listOf("de")
      it.controller.setActive(true)
    }
    val distributionOff = Harness(
      scope, policy = policy, placement = placement, distributionEnabled = false,
    ).also { it.controller.setActive(true) }

    m When "both request a load"
    geoBlocked.controller.requestLoad()
    distributionOff.controller.requestLoad()
    scope.advanceUntilIdle()

    m Then "neither creates a loader and each reports its reason"
    assertNull(geoBlocked.factory.last)
    assertNull(distributionOff.factory.last)
    assertEquals("geo_excluded", geoBlocked.controller.lastSkipReason)
    assertEquals("distribution_disabled", distributionOff.controller.lastSkipReason)
    assertEquals("DE", geoBlocked.analytics.events.single().second?.get("geo"))
  }

  @Test
  fun `Content shorter than the minimum is a permanent skip`() = coScenario { scope ->
    m Given "a minimum content length above the slot's content"
    val h = Harness(scope, policy = policy, placement = NativeAdPlacement.APP_DETAIL_BOTTOM)
    h.flags.ints["appview_native_bottom_min_content_length"] = 1500
    h.controller.setActive(true)

    m When "the slot requests with short content, then again much later"
    h.controller.requestLoad(contentLength = 300)
    scope.advanceUntilIdle()
    scope.advanceTimeBy(policy.reevaluateIntervalMs * 10)
    h.controller.requestLoad(contentLength = 300)
    scope.advanceUntilIdle()

    m Then "no request is ever made"
    assertEquals(0, h.factory.totalLoads)
    assertEquals("content_too_short", h.controller.lastSkipReason)
  }

  @Test
  fun `Loaded state carries the configured position and gap`() = coScenario { scope ->
    m Given "a position and a minimum gap in Remote Config"
    val h = Harness(scope, policy = policy, placement = NativeAdPlacement.HOME_DEEP)
    h.flags.booleans["home_native_deep_enabled"] = true
    h.flags.ints["home_native_deep_position"] = 7
    h.flags.ints["home_native_deep_min_gap"] = 4
    h.controller.setActive(true)

    m When "an ad loads"
    h.controller.requestLoad()
    scope.advanceUntilIdle()
    h.factory.last!!.callbacks.onLoaded(fakeMaxAd())

    m Then "the state exposes both values"
    val loaded = h.controller.uiState.value as NativeAdUiState.Loaded
    assertEquals(7, loaded.position)
    assertEquals(4, loaded.minGap)
  }

  @Test
  fun `A list shorter than the effective remote position never requests, then loads when it grows`() =
    coScenario { scope ->
      m Given "a remote position of 5 and a list of 3 apps"
      val h = Harness(scope, policy = policy, placement = placement)
      h.flags.ints["browse_native_category_position"] = 5
      h.controller.setActive(true)
      h.controller.requestLoad(listSize = 3)
      scope.advanceUntilIdle()
      val loadsWhileShort = h.factory.totalLoads
      val reasonWhileShort = h.controller.lastSkipReason
      val positionKnown = h.controller.configuredPosition.value

      m When "the list grows to 6 apps right away"
      h.controller.requestLoad(listSize = 6)
      scope.advanceUntilIdle()

      m Then "nothing was requested while short, the position is exposed, and growth loads once"
      assertEquals(0, loadsWhileShort)
      assertEquals("list_too_short", reasonWhileShort)
      assertEquals(5, positionKnown)
      assertEquals(1, h.factory.totalLoads)
      assertEquals(1, h.analytics.names().count { it.endsWith("_skipped") })
    }

  @Test
  fun `The Search MREC placement passes the same gates before any view is created`() =
    coScenario { scope ->
      val mrec = NativeAdPlacement.SEARCH_MREC
      m Given "an MREC controller per blocked condition: gplay, blank unit, flag off, excluded geo"
      val gplay = Harness(scope, policy = policy, placement = mrec, distributionEnabled = false)
      val blank = Harness(scope, adUnitId = "", policy = policy, placement = mrec)
      val flagOff = Harness(scope, policy = policy, placement = mrec).also {
        it.flags.booleans["search_mrec_enabled"] = false
      }
      val geo = Harness(scope, policy = policy, placement = mrec, geo = "FR").also {
        it.flags.lists["search_mrec_excluded_geos"] = listOf("FR")
      }
      val all = listOf(gplay, blank, flagOff, geo)

      m When "each is activated and asked to load"
      all.forEach { it.controller.setActive(true); it.controller.requestLoad() }
      scope.advanceUntilIdle()

      m Then "none of them created a loader or view, so no MREC and no fallback was requested"
      all.forEach { assertTrue(it.factory.loaders.isEmpty()) }
      assertEquals(
        listOf("distribution_disabled", "no_ad_unit_id", "flag_disabled", "geo_excluded"),
        all.map { it.controller.lastSkipReason },
      )
    }

  @Test
  fun `A failing SDK initialization never creates the MREC view`() = coScenario { scope ->
    m Given "an MREC controller whose SDK never initializes"
    val h = Harness(
      scope, policy = policy, placement = NativeAdPlacement.SEARCH_MREC, sdkInit = { false },
    )
    h.controller.setActive(true)

    m When "it requests and every backoff elapses (time is advanced step by step, never to idle)"
    h.controller.requestLoad()
    repeat(6) {
      scope.advanceTimeBy(policy.maxDelayMs + 1)
      scope.runCurrent()
    }

    m Then "the initial failure plus maxAttempts retries are reported and no view was created"
    assertTrue(h.factory.loaders.isEmpty())
    assertEquals(
      policy.maxAttempts + 1,
      h.analytics.events.count { it.first == "search_mrec_failed" },
    )
  }

  @Test
  fun `MREC retries stop while the host is paused and resume afterwards`() = coScenario { scope ->
    m Given "an MREC slot with a failed first request"
    val h = Harness(scope, policy = policy, placement = NativeAdPlacement.SEARCH_MREC)
    h.controller.setActive(true)
    h.controller.requestLoad()
    scope.advanceUntilIdle()
    val loader = h.factory.last!!
    loader.callbacks.onLoadFailed(-5001, "no fill")

    m When "the host pauses for longer than every backoff, then resumes"
    h.controller.setActive(false)
    scope.advanceTimeBy(policy.maxDelayMs * 10)
    scope.advanceUntilIdle()
    val loadsWhilePaused = loader.loadCalls
    h.controller.setActive(true)
    scope.advanceUntilIdle()

    m Then "no retry fired while paused and exactly one fires on resume"
    assertEquals(1, loadsWhilePaused)
    assertEquals(2, loader.loadCalls)
  }

  @Test
  fun `A freshly created loader is told whether the slot is already active`() = coScenario { scope ->
    m Given "an active slot"
    val states = mutableListOf<Boolean>()
    val h = Harness(scope, policy = policy, placement = placement)
    h.controller.setActive(true)

    m When "the controller creates its loader on the first request"
    h.controller.requestLoad()
    scope.advanceUntilIdle()
    h.factory.last!!.onActiveChangedCalls.let { states += it }

    m Then "the loader saw an active slot"
    assertEquals(listOf(true), states)
  }

  @Test
  fun `An ad that expires while the slot is inactive reloads only after reactivation`() =
    coScenario { scope ->
      m Given "a loaded ad and a slot that then goes offscreen"
      val h = Harness(scope, policy = policy, placement = placement)
      h.controller.setActive(true)
      h.controller.requestLoad()
      scope.advanceUntilIdle()
      val loader = h.factory.last!!
      val ad = fakeMaxAd()
      loader.callbacks.onLoaded(ad)
      h.controller.setActive(false)

      m When "the ad expires while offscreen, then the slot comes back"
      loader.callbacks.onExpired(ad)
      scope.advanceTimeBy(policy.maxDelayMs * 10)
      scope.advanceUntilIdle()
      val loadsWhileInactive = loader.loadCalls
      h.controller.setActive(true)
      scope.advanceUntilIdle()

      m Then "the expired ad was destroyed at once, nothing loaded offscreen, one reload on return"
      assertEquals(listOf(ad), loader.destroyedAds)
      assertEquals(1, loadsWhileInactive)
      assertEquals(2, loader.loadCalls)
    }

  @Test
  fun `A flag skip before activation is re-evaluated by activation alone`() = coScenario { scope ->
    m Given "a placement requested while inactive (as the feed does) and skipped because its flag is off"
    val h = Harness(scope, policy = policy, placement = placement)
    h.flags.booleans["browse_native_category_enabled"] = false
    h.controller.requestLoad()
    scope.advanceUntilIdle()
    assertEquals("flag_disabled", h.controller.lastSkipReason)
    h.flags.booleans["browse_native_category_enabled"] = true

    m When "the slot is activated at once, then deactivated and re-activated after the interval"
    h.controller.setActive(true)
    scope.advanceUntilIdle()
    val loadsRightAway = h.factory.totalLoads
    h.controller.setActive(false)
    scope.advanceTimeBy(policy.reevaluateIntervalMs + 1)
    h.controller.setActive(true)
    scope.advanceUntilIdle()

    m Then "activation inside the interval loads nothing and the later activation requests once"
    assertEquals(0, loadsRightAway)
    assertEquals(1, h.factory.totalLoads)
  }

  @Test
  fun `A geo skip before activation is re-evaluated by activation alone`() = coScenario { scope ->
    m Given "a geo exclusion that is lifted after the first skip"
    val h = Harness(scope, policy = policy, placement = placement, geo = "BR")
    h.flags.lists["browse_native_category_excluded_geos"] = listOf("BR")
    h.controller.requestLoad()
    scope.advanceUntilIdle()
    assertEquals("geo_excluded", h.controller.lastSkipReason)
    h.flags.lists["browse_native_category_excluded_geos"] = emptyList()

    m When "the slot activates after the re-evaluation interval"
    scope.advanceTimeBy(policy.reevaluateIntervalMs + 1)
    h.controller.setActive(true)
    scope.advanceUntilIdle()

    m Then "one request is made without any further request from the UI"
    assertEquals(1, h.factory.totalLoads)
  }

  @Test
  fun `Activation before any request never loads`() = coScenario { scope ->
    m Given "a slot that was never asked to load"
    val h = Harness(scope, policy = policy, placement = placement)

    m When "it is activated and deactivated"
    h.controller.setActive(true)
    h.controller.setActive(false)
    scope.advanceUntilIdle()

    m Then "nothing is evaluated or requested"
    assertEquals(0, h.factory.totalLoads)
    assertNull(h.controller.lastSkipReason)
  }

  @Test
  fun `The effective min gap is exposed after evaluation`() = coScenario { scope ->
    m Given "a remote min gap of 5"
    val h = Harness(scope, policy = policy, placement = NativeAdPlacement.HOME_DEEP)
    h.flags.ints["home_native_deep_min_gap"] = 5
    h.controller.setActive(true)

    m When "the gates are evaluated"
    h.controller.requestLoad()
    scope.advanceUntilIdle()

    m Then "the value is available to the screen"
    assertEquals(5, h.controller.configuredMinGap.value)
  }

  @Test
  fun `Scrolling far past a pending browse retry and back stops requests while away`() =
    coScenario { scope ->
      m Given "a browse slot with a pending retry and a 40-row list"
      val h = Harness(scope, policy = policy, placement = placement)
      fun activeFor(firstVisible: Int, lastVisible: Int) = NativeAdListPlan.shouldBeActive(
        resumed = true,
        itemCount = 40,
        configuredPosition = h.controller.configuredPosition.value,
        defaultPosition = placement.defaultPosition,
        nearVisible = NativeAdVisibility.isIndexNearVisible(
          index = NativeAdListPlan.slotIndex(
            h.controller.configuredPosition.value, placement.defaultPosition,
          ),
          firstVisible = firstVisible,
          lastVisible = lastVisible,
          margin = 2,
        ),
      )
      h.controller.setActive(activeFor(0, 6))
      h.controller.requestLoad(listSize = 40)
      scope.runCurrent()
      val loader = h.factory.last!!
      loader.callbacks.onLoadFailed(-5001, "no fill")

      m When "the user scrolls far past the slot, time passes, then scrolls back"
      h.controller.setActive(activeFor(25, 31))
      scope.advanceTimeBy(policy.maxDelayMs * 5)
      scope.runCurrent()
      val loadsWhileAway = loader.loadCalls
      h.controller.setActive(activeFor(0, 6))
      scope.runCurrent()

      m Then "no request happened while away and one retry fires on return"
      assertEquals(1, loadsWhileAway)
      assertEquals(2, loader.loadCalls)
    }

  @Test
  fun `Unknown geo is ineligible while a blocklist is active and eligible without one`() =
    coScenario { scope ->
      m Given "a controller whose country cannot be resolved, with and without a blocklist"
      val blocked = Harness(scope, policy = policy, placement = placement, geo = AdGeoProvider.UNKNOWN_GEO)
      blocked.flags.lists[NativeAdConfig.excludedGeosKey(placement)] = listOf("US", "DE")
      val open = Harness(scope, policy = policy, placement = placement, geo = AdGeoProvider.UNKNOWN_GEO)
      open.flags.lists[NativeAdConfig.excludedGeosKey(placement)] = emptyList()

      m When "both request"
      blocked.controller.setActive(true)
      blocked.controller.requestLoad()
      open.controller.setActive(true)
      open.controller.requestLoad()
      scope.runCurrent()

      m Then "the blocklisted build never requests (narrower, never wider) and the open one does"
      assertTrue(blocked.factory.loaders.isEmpty())
      assertEquals("geo_excluded", blocked.controller.lastSkipReason)
      assertEquals(1, open.factory.totalLoads)
    }

  @Test
  fun `A missing SDK key is a permanent skip, not a retried failure`() = coScenario { scope ->
    m Given "a build without a MAX SDK key"
    val h = Harness(scope, policy = policy, placement = placement, hasSdkKey = { false })
    h.controller.setActive(true)

    m When "it requests and a long time passes"
    h.controller.requestLoad()
    scope.advanceTimeBy(policy.reevaluateIntervalMs * 3)
    scope.runCurrent()
    h.controller.requestLoad()
    scope.runCurrent()

    m Then "nothing is requested, no failure is reported and the skip is permanent"
    assertTrue(h.factory.loaders.isEmpty())
    assertEquals("no_sdk_key", h.controller.lastSkipReason)
    assertTrue(h.analytics.events.none { it.first.endsWith("_failed") })
    assertEquals(1, h.analytics.events.count { it.first.endsWith("_skipped") })
  }

  @Test
  fun `An SDK initialization that never completes times out into the bounded backoff`() =
    coScenario { scope ->
      m Given "an SDK initializer that hangs and a 5 second timeout"
      val h = Harness(
        scope, policy = policy, placement = placement,
        sdkInit = { CompletableDeferred<Boolean>().await() }, sdkInitTimeoutMs = 5_000,
      )
      h.controller.setActive(true)

      m When "it requests and the timeout plus the first backoff elapse"
      h.controller.requestLoad()
      scope.advanceTimeBy(4_999)
      scope.runCurrent()
      val failuresBeforeTimeout = h.analytics.events.count { it.first.endsWith("_failed") }
      scope.advanceTimeBy(1)
      scope.runCurrent()
      val failuresAtTimeout = h.analytics.events.count { it.first.endsWith("_failed") }

      m Then "the hang is reported as an sdk_init failure exactly at the timeout and retried"
      assertEquals(0, failuresBeforeTimeout)
      assertEquals(1, failuresAtTimeout)
      assertEquals(
        NativeAdController.SDK_INIT_ERROR,
        h.analytics.events.last { it.first.endsWith("_failed") }.second?.get("error_code"),
      )
      assertTrue(h.factory.loaders.isEmpty())
    }

  @Test
  fun `A gate that throws is contained as a transient failure and never stalls the slot`() =
    coScenario { scope ->
      m Given "an SDK initializer that throws once, then succeeds"
      var calls = 0
      val h = Harness(
        scope, policy = policy, placement = placement,
        sdkInit = { if (calls++ == 0) throw IllegalStateException("boom") else true },
      )
      h.controller.setActive(true)

      m When "it requests and the first backoff elapses"
      h.controller.requestLoad()
      scope.runCurrent()
      val loadsAfterThrow = h.factory.totalLoads
      scope.advanceTimeBy(policy.baseDelayMs)
      scope.runCurrent()

      m Then "the exception is reported, the slot recovers and loads on the retry"
      assertEquals(0, loadsAfterThrow)
      assertEquals(
        NativeAdController.EXCEPTION_ERROR,
        h.analytics.events.first { it.first.endsWith("_failed") }.second?.get("error_code"),
      )
      assertEquals(1, h.factory.totalLoads)
    }

  @Test
  fun `Release during a suspended gate never creates a loader, and later callbacks are inert`() =
    coScenario { scope ->
      m Given "an SDK initializer that completes only after the controller is released"
      val gate = CompletableDeferred<Boolean>()
      val h = Harness(scope, policy = policy, placement = placement, sdkInit = { gate.await() })
      h.controller.setActive(true)
      h.controller.requestLoad()
      scope.runCurrent()

      m When "the slot is released, then the gate opens"
      h.controller.release()
      gate.complete(true)
      scope.runCurrent()
      scope.advanceTimeBy(policy.maxDelayMs * 4)
      scope.runCurrent()

      m Then "no loader exists, nothing was requested and the state stays hidden"
      assertTrue(h.factory.loaders.isEmpty())
      assertEquals(NativeAdUiState.Hidden, h.controller.uiState.value)
      assertTrue(h.analytics.events.none { it.first.endsWith("_requested") })
    }

  @Test
  fun `Release with a loaded ad destroys the ad and the loader and ignores later activity`() =
    coScenario { scope ->
      m Given "a loaded ad"
      val h = Harness(scope, policy = policy, placement = placement)
      h.controller.setActive(true)
      h.controller.requestLoad()
      scope.runCurrent()
      val loader = h.factory.last!!
      val ad = fakeMaxAd()
      loader.callbacks.onLoaded(ad)

      m When "the controller is released and then activated, requested and failed again"
      h.controller.release()
      h.controller.release()
      h.controller.setActive(true)
      h.controller.requestLoad()
      loader.callbacks.onLoadFailed(-5001, "late")
      scope.advanceTimeBy(policy.maxDelayMs * 4)
      scope.runCurrent()

      m Then "the ad and loader are destroyed once, the state is hidden and nothing reloads"
      assertEquals(listOf(ad), loader.destroyedAds)
      assertTrue(loader.destroyed)
      assertEquals(NativeAdUiState.Hidden, h.controller.uiState.value)
      assertEquals(1, loader.loadCalls)
      assertEquals(1, h.factory.loaders.size)
    }

  @Test
  fun `A failure while inactive is deferred and keeps its backoff on reactivation`() =
    coScenario { scope ->
      m Given "a request in flight whose slot goes inactive before the failure arrives"
      val h = Harness(scope, policy = policy, placement = placement)
      h.controller.setActive(true)
      h.controller.requestLoad()
      scope.runCurrent()
      val loader = h.factory.last!!
      h.controller.setActive(false)
      loader.callbacks.onLoadFailed(-5001, "no fill")

      m When "the slot toggles active several times right away, then the backoff elapses"
      repeat(3) {
        h.controller.setActive(true)
        scope.runCurrent()
        h.controller.setActive(false)
        scope.runCurrent()
      }
      h.controller.setActive(true)
      scope.runCurrent()
      val loadsBeforeBackoff = loader.loadCalls
      scope.advanceTimeBy(policy.baseDelayMs)
      scope.runCurrent()

      m Then "toggling never skips the backoff: exactly one retry, at the scheduled time"
      assertEquals(1, loadsBeforeBackoff)
      assertEquals(2, loader.loadCalls)
      assertEquals(listOf(true, false, true, false, true, false, true, false, true), loader.onActiveChangedCalls)
    }

  @Test
  fun `A request while inactive waits for the first activation and then loads once`() =
    coScenario { scope ->
      m Given "a controller whose gates all pass but whose slot is not active"
      val h = Harness(scope, policy = policy, placement = placement)

      m When "it is requested, time passes, then it is activated and deactivated and re-activated"
      h.controller.requestLoad()
      scope.advanceTimeBy(policy.reevaluateIntervalMs)
      scope.runCurrent()
      val loadsWhileInactive = h.factory.totalLoads
      h.controller.setActive(true)
      scope.runCurrent()
      h.controller.setActive(false)
      h.controller.setActive(true)
      scope.runCurrent()

      m Then "nothing loads while inactive and exactly one request follows activation"
      assertEquals(0, loadsWhileInactive)
      assertEquals(1, h.factory.totalLoads)
    }

  @Test
  fun `Exhausted retries are re-evaluated only after the interval`() = coScenario { scope ->
    m Given "a slot whose retries are exhausted"
    val h = Harness(scope, policy = policy, placement = placement)
    h.controller.setActive(true)
    h.controller.requestLoad()
    scope.runCurrent()
    val loader = h.factory.last!!
    repeat(policy.maxAttempts + 1) {
      loader.callbacks.onLoadFailed(-5001, "no fill")
      scope.advanceTimeBy(policy.maxDelayMs)
      scope.runCurrent()
    }
    val exhaustedLoads = loader.loadCalls

    m When "it is requested before and after the re-evaluation interval"
    h.controller.requestLoad()
    scope.runCurrent()
    val loadsBeforeInterval = loader.loadCalls
    scope.advanceTimeBy(policy.reevaluateIntervalMs)
    h.controller.requestLoad()
    scope.runCurrent()

    m Then "only the request after the interval starts a fresh attempt at attempt 0"
    assertEquals(policy.maxAttempts + 1, exhaustedLoads)
    assertEquals(exhaustedLoads, loadsBeforeInterval)
    assertEquals(exhaustedLoads + 1, loader.loadCalls)
    assertEquals(0, h.analytics.events.last { it.first.endsWith("_requested") }.second?.get("attempt"))
  }

  @Test
  fun `A location check without a valid spot skips without latching and is re-tried on request`() =
    coScenario { scope ->
      m Given "a deep home slot whose feed has no spaced spot yet"
      val h = Harness(scope, policy = policy, placement = NativeAdPlacement.HOME_DEEP)
      h.flags.booleans[NativeAdConfig.enabledKey(NativeAdPlacement.HOME_DEEP)] = true
      var hasSpot = false
      h.controller.setActive(true)

      m When "it requests, the feed later gains a spot and it requests again"
      h.controller.requestLoad(locationCheck = { _, _ -> hasSpot })
      scope.runCurrent()
      val loadsWithoutSpot = h.factory.totalLoads
      val skipReason = h.controller.lastSkipReason
      hasSpot = true
      h.controller.requestLoad(locationCheck = { _, _ -> hasSpot })
      scope.runCurrent()

      m Then "no request is made without a spot and one follows as soon as a spot exists"
      assertEquals(0, loadsWithoutSpot)
      assertEquals("no_valid_location", skipReason)
      assertEquals(1, h.factory.totalLoads)
    }

  @Test
  fun `A failure while an ad is showing keeps the ad and schedules no retry`() = coScenario { scope ->
    m Given "a loaded MREC whose auto-refresh then fails"
    val h = Harness(scope, policy = policy, placement = NativeAdPlacement.SEARCH_MREC)
    h.controller.setActive(true)
    h.controller.requestLoad()
    scope.runCurrent()
    val loader = h.factory.last!!
    val ad = fakeMaxAd()
    loader.callbacks.onLoaded(ad)

    m When "the refresh failure arrives and every backoff elapses"
    loader.callbacks.onLoadFailed(204, "refresh no fill")
    scope.advanceTimeBy(policy.maxDelayMs * 4)
    scope.runCurrent()

    m Then "the ad stays on screen, nothing is destroyed and no extra load is issued"
    assertEquals(ad, (h.controller.uiState.value as NativeAdUiState.Loaded).ad)
    assertTrue(loader.destroyedAds.isEmpty())
    assertEquals(1, loader.loadCalls)
    assertEquals(1, h.analytics.events.count { it.first == "search_mrec_failed" })
  }

  @Test
  fun `Distribution-disabled builds emit no analytics at all`() = coScenario { scope ->
    m Given "a gplay-style build"
    val h = Harness(scope, policy = policy, placement = placement, distributionEnabled = false)
    h.controller.setActive(true)

    m When "it requests"
    h.controller.requestLoad()
    scope.runCurrent()

    m Then "it skips permanently and silently"
    assertTrue(h.factory.loaders.isEmpty())
    assertEquals(NativeAdController.SKIP_DISTRIBUTION_DISABLED, h.controller.lastSkipReason)
    assertTrue(h.analytics.events.isEmpty())
  }

  @Test
  fun `Impression, click and loaded events carry ecpm, network, format and variant`() =
    coScenario { scope ->
      m Given "a Search MREC controller with a loaded ad"
      val h = Harness(scope, policy = policy, placement = NativeAdPlacement.SEARCH_MREC)
      h.controller.setActive(true)
      h.controller.requestLoad()
      scope.runCurrent()
      val loader = h.factory.last!!
      val ad = fakeMaxAd(network = "Unity", revenue = 0.0025)
      loader.callbacks.onLoaded(ad)

      m When "the ad is clicked and paid"
      loader.callbacks.onClicked(ad)
      loader.callbacks.onRevenuePaid(ad)

      m Then "the events name the network and the ecpm is revenue times a thousand"
      val loaded = h.analytics.events.first { it.first == "search_mrec_loaded" }.second!!
      val clicked = h.analytics.events.first { it.first == "search_mrec_clicked" }.second!!
      val impression = h.analytics.events.first { it.first == "search_mrec_impression" }.second!!
      assertEquals("Unity", loaded["network"])
      assertEquals("Unity", clicked["network"])
      assertEquals(2.5, impression["ecpm"] as Double, 1e-9)
      assertEquals(NativeAdAnalytics.FORMAT_NATIVE, loaded["format"])
    }

  @Test
  fun `A nested timeout inside a gate is a transient failure, not a cancellation`() =
    coScenario { scope ->
      m Given "a gate that times out internally once, then passes"
      var calls = 0
      val h = Harness(
        scope, policy = policy, placement = placement,
        sdkInit = {
          if (calls++ == 0) kotlinx.coroutines.withTimeout(1) { CompletableDeferred<Boolean>().await() }
          else true
        },
      )
      h.controller.setActive(true)

      m When "it requests, the nested timeout fires and the first backoff elapses"
      h.controller.requestLoad()
      scope.advanceTimeBy(2)
      scope.runCurrent()
      val failures = h.analytics.events.count { it.first.endsWith("_failed") }
      scope.advanceTimeBy(policy.baseDelayMs)
      scope.runCurrent()

      m Then "the slot is not stuck: one failure is reported and the retry loads"
      assertEquals(1, failures)
      assertEquals(1, h.factory.totalLoads)
    }
}
