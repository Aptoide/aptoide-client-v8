package com.aptoide.android.aptoidegames.installer.presentation

import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// Which states an unavailable app loses its action in. Installing and updating both go
// through Play, so both are withheld; anything already installed keeps its Open button, and
// anything in progress keeps its progress.
internal class WithheldStatesTest {

  @Test
  fun `A pending install is withheld`() = scenario {
    m Given "an app waiting to be installed, not offered"
    val state = DownloadUiState.Install(installWith = {})

    m When "it is asked whether the action is withheld"
    val withheld = state.isWithheldBy(InstallAvailability.NOT_OFFERED)

    m Then "it is"
    assertTrue(withheld)
  }

  @Test
  fun `A pending update is withheld`() = scenario {
    m Given "an installed app with an update Play cannot deliver"
    val state = DownloadUiState.Outdated(open = {}, updateWith = {}, uninstall = {})

    m When "it is asked whether the action is withheld"
    val withheld = state.isWithheldBy(InstallAvailability.NOT_OFFERED)

    m Then "it is, so the user is not sent into an update that can only fail"
    assertTrue(withheld)
  }

  @Test
  fun `An installed app keeps its open button`() = scenario {
    m Given "an installed app that the catalog no longer holds"
    val state = DownloadUiState.Installed(open = {}, uninstall = {})

    m When "it is asked whether the action is withheld"
    val withheld = state.isWithheldBy(InstallAvailability.NOT_OFFERED)

    m Then "it is not"
    assertFalse(withheld)
  }

  @Test
  fun `An available app is never withheld`() = scenario {
    m Given "an app waiting to be installed, available"
    val state = DownloadUiState.Install(installWith = {})

    m When "it is asked whether the action is withheld"
    val withheld = state.isWithheldBy(InstallAvailability.AVAILABLE)

    m Then "it is not"
    assertFalse(withheld)
  }

  @Test
  fun `An app still being checked is withheld until the answer`() = scenario {
    m Given "an app waiting to be installed, still being looked up"
    val state = DownloadUiState.Install(installWith = {})

    m When "it is asked whether the action is withheld"
    val withheld = state.isWithheldBy(InstallAvailability.CHECKING)

    m Then "it is, for now"
    assertTrue(withheld)
  }
}
