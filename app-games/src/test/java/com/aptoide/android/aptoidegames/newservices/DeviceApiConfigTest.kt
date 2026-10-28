package com.aptoide.android.aptoidegames.newservices

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// The catalog variant decides which apps the Play build can see at all, and which titles the
// direct build's apkfy can fall back to, so changing it is a product decision. Pinned here so
// it cannot drift as a side effect of another change.
internal class DeviceApiConfigTest {

  @Test
  fun `The device API is read in the Google certified catalog`() = scenario {
    m Given "Aptoide Games, on Play or direct"

    m When "the catalog variant is read"
    val variant = DEVICE_API_VARIANT

    m Then "it is the Google certified one"
    assertEquals("google-certified", variant)
  }
}
