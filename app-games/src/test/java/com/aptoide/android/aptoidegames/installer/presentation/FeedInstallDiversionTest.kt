package com.aptoide.android.aptoidegames.installer.presentation

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

// In Play-distributed builds the feed stops being an install entry point: buttons that could
// divert into a Play inline install navigate to AppView instead, which is where the mandatory
// "Google Play" attribution is shown. Everything else - progress, cancel, open - must keep
// working in place, so the diversion is gated on the state, not applied to the whole card.
internal class FeedInstallDiversionTest {

  private val navigateToAppView: () -> Unit = {}

  @Test
  fun `Play distributions divert the states that could start an inline install`() = scenario {
    m Given "a Play-distributed build and a card that can navigate to AppView"
    val states = DownloadUiStateFixtures.preTap

    m When "the diversion is resolved for every pre-tap state"
    val results = states.map {
      it.appViewDiversion(onNavigateToAppView = navigateToAppView, divertsToAppView = true)
    }

    m Then "each button navigates to AppView instead of installing"
    results.forEachIndexed { index, result ->
      assertSame(navigateToAppView, result) { "${states[index]::class.simpleName} should divert" }
    }
  }

  @Test
  fun `In-progress and terminal states keep their own action`() = scenario {
    m Given "a Play-distributed build and a card that can navigate to AppView"
    val states = DownloadUiStateFixtures.postTap

    m When "the diversion is resolved for every in-progress or terminal state"
    val results = states.map {
      it.appViewDiversion(onNavigateToAppView = navigateToAppView, divertsToAppView = true)
    }

    m Then "none of them is diverted, so progress, cancel and open still work in place"
    results.forEachIndexed { index, result ->
      assertNull(result) { "${states[index]?.let { s -> s::class.simpleName }} should not divert" }
    }
  }

  @Test
  fun `Direct distributions never divert`() = scenario {
    m Given "a directly distributed build, which cannot inline install at all"
    val states = DownloadUiStateFixtures.all

    m When "the diversion is resolved for every state"
    val results = states.map {
      it.appViewDiversion(onNavigateToAppView = navigateToAppView, divertsToAppView = false)
    }

    m Then "one-tap install from the feed is left untouched"
    results.forEachIndexed { index, result ->
      assertNull(result) { "${states[index]?.let { s -> s::class.simpleName }} should not divert" }
    }
  }

  @Test
  fun `A card without an AppView destination is never diverted`() = scenario {
    m Given "a Play-distributed build and a card with no navigation lambda"
    val states = DownloadUiStateFixtures.preTap

    m When "the diversion is resolved for every pre-tap state"
    val results = states.map {
      it.appViewDiversion(onNavigateToAppView = null, divertsToAppView = true)
    }

    m Then "the button falls back to installing in place rather than doing nothing"
    results.forEachIndexed { index, result ->
      assertNull(result) { "${states[index]::class.simpleName} should not divert" }
    }
  }
}
