package com.aptoide.android.aptoidegames.newservices

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// The catalog variant decides which apps the Play build can see at all, so changing it is a
// product decision. Pinned here so it cannot drift as a side effect of another change.
internal class DeviceApiConfigTest {

  @Test
  fun `The Play build reads the Google certified catalog`() = scenario {
    m Given "the Play distribution of Aptoide Games"

    m When "the catalog variant is read"
    val variant = DEVICE_API_VARIANT

    m Then "it is the Google certified one"
    assertEquals("google-certified", variant)
  }
}
