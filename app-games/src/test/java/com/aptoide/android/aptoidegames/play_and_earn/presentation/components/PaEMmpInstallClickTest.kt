package com.aptoide.android.aptoidegames.play_and_earn.presentation.components

import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PaEMmpInstallClickTest {

  private val sent = mutableListOf<String>()
  private var started = 0
  private val click = PaEMmpInstallClick("com.game") { sent += it }

  @Test
  fun `A fresh install sends the MMP click and still reports the start`() = scenario {
    m Given "the install view offers Install"
    click.uiState = DownloadUiState.Install(installWith = {})

    m When "the install starts"
    click.onInstallStarted { started++ }()

    m Then "the click is sent once for the game and the start is reported"
    assertEquals(listOf("com.game"), sent)
    assertEquals(1, started)
  }

  @Test
  fun `An update does not send the MMP click`() = scenario {
    m Given "the install view offers Update"
    click.uiState = DownloadUiState.Outdated(open = {}, updateWith = {}, uninstall = {})

    m When "the update starts"
    click.onInstallStarted { started++ }()

    m Then "no click is sent, the start is still reported"
    assertEquals(emptyList<String>(), sent)
    assertEquals(1, started)
  }

  @Test
  fun `A migration does not send the MMP click`() = scenario {
    m Given "the install view offers Migrate"
    click.uiState = DownloadUiState.Migrate(open = {}, migrateWith = {}, uninstall = {})

    m When "the migration starts"
    click.onInstallStarted { started++ }()

    m Then "no click is sent, the start is still reported"
    assertEquals(emptyList<String>(), sent)
    assertEquals(1, started)
  }

  @Test
  fun `An unknown install state does not send the MMP click`() = scenario {
    m Given "no install view state yet"
    click.uiState = null

    m When "the install starts"
    click.onInstallStarted { started++ }()

    m Then "no click is sent, the start is still reported"
    assertEquals(emptyList<String>(), sent)
    assertEquals(1, started)
  }
}
