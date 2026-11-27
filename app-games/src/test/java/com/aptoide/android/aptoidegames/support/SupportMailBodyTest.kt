package com.aptoide.android.aptoidegames.support

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// AND-951: a restricted account's appeal carries what support needs to find the account.
internal class SupportMailBodyTest {

  @Test
  fun `The feedback e-mail is as before`() = scenario {
    m Given "a message written in the feedback form"
    m When "the e-mail body is built without an account"
    val body = supportMailBody("Great app", "Pixel 9", accountInfo = null)

    m Then "it is the message, the device and the signature, as before"
    assertEquals("Great app\n\n\nMy Hardware specs are\nPixel 9\n\nPowered by Aptoide", body)
  }

  @Test
  fun `A restricted account's appeal carries its wallet address`() = scenario {
    m Given "a message written from the restricted screen by a signed-in account"
    m When "the e-mail body is built with the wallet address"
    val body = supportMailBody("Please review", "Pixel 9", accountInfo = "0xabc")

    m Then "the address follows the device details"
    assertEquals(
      "Please review\n\n\nMy Hardware specs are\nPixel 9\n\nMy account is\n0xabc" +
        "\n\nPowered by Aptoide",
      body
    )
  }
}
