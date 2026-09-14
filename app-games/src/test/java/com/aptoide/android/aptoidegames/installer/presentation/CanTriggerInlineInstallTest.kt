package com.aptoide.android.aptoidegames.installer.presentation

import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// The compliance gate: the "Google Play" attribution must show exactly for the states
// whose action can divert into a Play inline install. Every DownloadUiState subtype is
// listed on one side or the other, so a new subtype should be added here deliberately.
internal class CanTriggerInlineInstallTest {

  private val packageInfo = InstallPackageInfo(0)

  private val preTapStates: List<DownloadUiState> = listOf(
    DownloadUiState.Install(installWith = {}),
    DownloadUiState.Outdated(open = {}, updateWith = {}, uninstall = {}),
    DownloadUiState.Migrate(open = {}, uninstall = {}, migrateWith = {}),
    DownloadUiState.MigrateAlias(migrateAliasWith = {}),
    DownloadUiState.Error(retryWith = {}),
  )

  private val postTapStates: List<DownloadUiState?> = listOf(
    null,
    DownloadUiState.Waiting(installPackageInfo = packageInfo, action = null),
    DownloadUiState.Downloading(installPackageInfo = packageInfo, cancel = {}),
    DownloadUiState.ReadyToInstall(cancel = {}),
    DownloadUiState.Installing(installPackageInfo = packageInfo),
    DownloadUiState.Uninstalling(installPackageInfo = packageInfo),
    DownloadUiState.Installed(open = {}, uninstall = {}),
  )

  @Test
  fun `Pre-tap actionable states can trigger the inline install`() = scenario {
    m Given "every state whose button can start an install"
    val states = preTapStates

    m When "the inline install gate is evaluated"
    val results = states.map { it.canTriggerInlineInstall() }

    m Then "all of them can trigger the inline install"
    results.forEachIndexed { index, result ->
      assertTrue(result) { "${states[index]::class.simpleName} should trigger" }
    }
  }

  @Test
  fun `In-progress and terminal states cannot trigger the inline install`() = scenario {
    m Given "the null state and every in-progress or terminal state"
    val states = postTapStates

    m When "the inline install gate is evaluated"
    val results = states.map { it.canTriggerInlineInstall() }

    m Then "none of them can trigger the inline install"
    results.forEachIndexed { index, result ->
      assertFalse(result) { "${states[index]?.let { it::class.simpleName }} should not trigger" }
    }
  }
}
