package com.aptoide.android.aptoidegames.installer.presentation

import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

// In Play-distributed builds the feed stops being an install entry point: buttons that could
// divert into a Play inline install navigate to AppView instead, which is where the mandatory
// "Google Play" attribution is shown. Everything else - progress, cancel, open - must keep
// working in place, so the diversion is gated on the state, not applied to the whole card.
internal class FeedInstallDiversionTest {

  private val packageInfo = InstallPackageInfo(0)

  private val navigateToAppView: () -> Unit = {}

  private val preTapStates: List<DownloadUiState> = listOf(
    DownloadUiState.Install(installWith = {}),
    DownloadUiState.Outdated(open = {}, updateWith = {}, uninstall = {}),
    DownloadUiState.Migrate(open = {}, uninstall = {}, migrateWith = {}),
    DownloadUiState.MigrateAlias(migrateAliasWith = {}),
    DownloadUiState.Error(retryWith = {}),
  )

  private val inProgressStates: List<DownloadUiState?> = listOf(
    null,
    DownloadUiState.Waiting(installPackageInfo = packageInfo, action = null),
    DownloadUiState.Downloading(installPackageInfo = packageInfo, cancel = {}),
    DownloadUiState.ReadyToInstall(cancel = {}),
    DownloadUiState.Installing(installPackageInfo = packageInfo),
    DownloadUiState.Uninstalling(installPackageInfo = packageInfo),
    DownloadUiState.Installed(open = {}, uninstall = {}),
  )

  @Test
  fun `Play distributions divert the states that could start an inline install`() = scenario {
    m Given "a Play-distributed build and a card that can navigate to AppView"
    val states = preTapStates

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
    val states = inProgressStates

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
    val states = preTapStates + inProgressStates

    m When "the diversion is resolved for every state"
    val results = states.map {
      it.appViewDiversion(onNavigateToAppView = navigateToAppView, divertsToAppView = false)
    }

    m Then "one-tap install from the feed is left untouched"
    results.forEach(::assertNull)
  }

  @Test
  fun `A card without an AppView destination is never diverted`() = scenario {
    m Given "a Play-distributed build and a card with no navigation lambda"
    val states = preTapStates

    m When "the diversion is resolved for every pre-tap state"
    val results = states.map {
      it.appViewDiversion(onNavigateToAppView = null, divertsToAppView = true)
    }

    m Then "the button falls back to installing in place rather than doing nothing"
    results.forEach(::assertNull)
  }

  @Test
  fun `The diversion gate matches the attribution gate`() = scenario {
    m Given "every download state"
    val states = preTapStates + inProgressStates

    m When "the diversion and the attribution gates are compared"
    val mismatches = states.filter {
      val diverts =
        it.appViewDiversion(onNavigateToAppView = navigateToAppView, divertsToAppView = true) != null
      diverts != it.canTriggerInlineInstall()
    }

    m Then "they agree, so a new state cannot divert without being labeled, or vice versa"
    assertNotNull(mismatches)
    assert(mismatches.isEmpty()) {
      "states disagreeing: ${mismatches.map { it?.let { s -> s::class.simpleName } }}"
    }
  }
}
