package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalCode
import cm.aptoide.pt.test.gherkin.scenario
import com.aptoide.android.aptoidegames.R
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

// AND-953: a bonus refused right after the user's claim says why. AND-964 adds the VPN (the
// bonus stays claimable) and the device limit (the bonus ends).
internal class SignInBonusRefusedMessageTest {

  @ParameterizedTest(name = "{0}")
  @EnumSource(
    PaERefusalCode::class,
    names = [
      "ACCOUNT_RESTRICTED", "REGION_NOT_SUPPORTED", "DEVICE_NOT_SUPPORTED", "UPDATE_REQUIRED",
      "VPN_NOT_ALLOWED", "SIGN_IN_BONUS_DEVICE_LIMIT",
    ]
  )
  fun `Each refusal of the bonus has its own text`(code: PaERefusalCode) = scenario {
    m Given "a sign-in bonus refused with ${code.name}"
    m When "its text is chosen"
    val message = signInBonusRefusedMessage(code)

    m Then "it is that refusal's existing text"
    val expected = when (code) {
      PaERefusalCode.ACCOUNT_RESTRICTED -> R.string.play_and_earn_exchange_error_restricted_body
      PaERefusalCode.REGION_NOT_SUPPORTED -> R.string.play_and_earn_refused_region_body
      PaERefusalCode.DEVICE_NOT_SUPPORTED -> R.string.play_and_earn_refused_device_body
      PaERefusalCode.VPN_NOT_ALLOWED -> R.string.play_and_earn_refused_vpn_body
      PaERefusalCode.SIGN_IN_BONUS_DEVICE_LIMIT ->
        R.string.play_and_earn_refused_bonus_device_limit_body

      else -> R.string.play_and_earn_refused_update_body
    }
    assertEquals(expected, message)
  }

  @ParameterizedTest(name = "{0}")
  @EnumSource(
    PaERefusalCode::class,
    mode = EnumSource.Mode.EXCLUDE,
    names = [
      "ACCOUNT_RESTRICTED", "REGION_NOT_SUPPORTED", "DEVICE_NOT_SUPPORTED", "UPDATE_REQUIRED",
      "VPN_NOT_ALLOWED", "SIGN_IN_BONUS_DEVICE_LIMIT",
    ]
  )
  fun `A refusal that does not end the bonus has no text`(code: PaERefusalCode) = scenario {
    m Given "a ${code.name} refusal, which keeps the bonus claimable"
    m When "its text is chosen"
    val message = signInBonusRefusedMessage(code)

    m Then "there is none"
    assertNull(message)
  }
}
