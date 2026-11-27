package com.aptoide.android.aptoidegames.play_and_earn.presentation.app_view

import app.cash.turbine.test
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusal
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalCode
import cm.aptoide.pt.test.gherkin.coScenario
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.play_and_earn.domain.sessions.PaESessionRefusals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

// AND-950: the game's Rewards tab says why its session was refused.
@OptIn(ExperimentalCoroutinesApi::class)
internal class PaESessionRefusalViewModelTest {

  private val refusals = PaESessionRefusals()

  @BeforeEach
  fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

  @AfterEach
  fun tearDown() = Dispatchers.resetMain()

  @Test
  fun `A region refusal says the region is not supported`() = coScenario {
    m Given "the game's session refused for the user's region"
    refusals.record(GAME, PaERefusal(PaERefusalCode.REGION_NOT_SUPPORTED))

    m When "the Rewards tab reads its message"
    val message = PaESessionRefusalViewModel(GAME, refusals).uiState.value

    m Then "it is the region message"
    assertEquals(
      PaERefusalMessage(
        R.string.play_and_earn_refused_title,
        R.string.play_and_earn_refused_region_body
      ),
      message
    )
  }

  @Test
  fun `A device refusal says the device is not supported`() = coScenario {
    m Given "the game's session refused for the user's device"
    refusals.record(GAME, PaERefusal(PaERefusalCode.DEVICE_NOT_SUPPORTED))

    m When "the Rewards tab reads its message"
    val message = PaESessionRefusalViewModel(GAME, refusals).uiState.value

    m Then "it is the device message"
    assertEquals(
      PaERefusalMessage(
        R.string.play_and_earn_refused_title,
        R.string.play_and_earn_refused_device_body
      ),
      message
    )
  }

  @Test
  fun `An update refusal asks for an update`() = coScenario {
    m Given "the game's session refused because the app is too old"
    refusals.record(GAME, PaERefusal(PaERefusalCode.UPDATE_REQUIRED))

    m When "the Rewards tab reads its message"
    val message = PaESessionRefusalViewModel(GAME, refusals).uiState.value

    m Then "it is the update message"
    assertEquals(
      PaERefusalMessage(
        R.string.play_and_earn_refused_title,
        R.string.play_and_earn_refused_update_body
      ),
      message
    )
  }

  @Test
  fun `A VPN refusal asks to turn the VPN off, on every game`() = coScenario {
    m Given "another game's session refused because the phone reports a VPN"
    refusals.record(OTHER_GAME, PaERefusal(PaERefusalCode.VPN_NOT_ALLOWED))

    m When "this game's Rewards tab reads its message"
    val message = PaESessionRefusalViewModel(GAME, refusals).uiState.value

    m Then "it is the VPN message"
    assertEquals(
      PaERefusalMessage(
        R.string.play_and_earn_refused_title,
        R.string.play_and_earn_refused_vpn_body
      ),
      message
    )
  }

  @Test
  fun `A restricted account says the account is restricted, on every game`() = coScenario {
    m Given "another game's session refused for a restricted account"
    refusals.record(OTHER_GAME, PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED))

    m When "this game's Rewards tab reads its message"
    val message = PaESessionRefusalViewModel(GAME, refusals).uiState.value

    m Then "it says the account is restricted, as the restricted screen does (AND-951)"
    assertEquals(
      PaERefusalMessage(
        R.string.play_and_earn_exchange_error_restricted_title,
        R.string.play_and_earn_exchange_error_restricted_body
      ),
      message
    )
  }

  @Test
  fun `A device refusal shows on every game`() = coScenario {
    m Given "another game's session refused for the device"
    refusals.record(OTHER_GAME, PaERefusal(PaERefusalCode.DEVICE_NOT_SUPPORTED))

    m When "this game's Rewards tab reads its message"
    val message = PaESessionRefusalViewModel(GAME, refusals).uiState.value

    m Then "it is the device message"
    assertEquals(R.string.play_and_earn_refused_device_body, message?.body)
  }

  @ParameterizedTest(name = "{0}")
  @EnumSource(
    PaERefusalCode::class,
    mode = EnumSource.Mode.EXCLUDE,
    names = [
      "ACCOUNT_RESTRICTED", "REGION_NOT_SUPPORTED", "DEVICE_NOT_SUPPORTED", "UPDATE_REQUIRED",
      "VPN_NOT_ALLOWED",
    ]
  )
  fun `A refusal that does not block play shows nothing`(code: PaERefusalCode) = coScenario {
    m Given "the game's session answered with $code"
    refusals.record(GAME, PaERefusal(code))

    m When "the Rewards tab reads its message"
    val message = PaESessionRefusalViewModel(GAME, refusals).uiState.value

    m Then "there is none: the missions show as before"
    assertNull(message)
  }

  @Test
  fun `Without a refusal there is no message`() = coScenario {
    m Given "another game refused, this one not"
    refusals.record(OTHER_GAME, PaERefusal(PaERefusalCode.REGION_NOT_SUPPORTED))

    m When "this game's Rewards tab reads its message"
    val message = PaESessionRefusalViewModel(GAME, refusals).uiState.value

    m Then "there is none: the missions show as before"
    assertNull(message)
  }

  @Test
  fun `The message follows the refusal as it comes and goes`() = coScenario {
    m Given "the game's Rewards tab open with no refusal"
    val viewModel = PaESessionRefusalViewModel(GAME, refusals)

    m When "the session is refused, then accepted"
    viewModel.uiState.test {
      assertNull(awaitItem())
      refusals.record(GAME, PaERefusal(PaERefusalCode.DEVICE_NOT_SUPPORTED))
      val refused = awaitItem()
      refusals.onSessionStarted(GAME)
      val accepted = awaitItem()

      m Then "the device message shows, then goes"
      assertEquals(R.string.play_and_earn_refused_device_body, refused?.body)
      assertNull(accepted)
    }
  }

  private companion object {
    const val GAME = "com.game"
    const val OTHER_GAME = "com.other.game"
  }
}
