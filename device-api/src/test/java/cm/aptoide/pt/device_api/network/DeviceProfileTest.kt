package cm.aptoide.pt.device_api.network

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

// The device API filters releases by the device profile and answers 404 for a density that is
// not one of the standard buckets, so what goes on the wire is pinned here.
internal class DeviceProfileTest {

  @Test
  fun `The abis are sent in order as one comma separated value`() = scenario {
    m Given "a device supporting three abis"
    val abis = listOf("arm64-v8a", "armeabi-v7a", "armeabi")

    m When "the profile is built"
    val profile = deviceProfileOf(sdk = 34, abis = abis, isTv = false, bucketedDensity = 480)

    m Then "they are joined in the device's order of preference"
    assertEquals("arm64-v8a,armeabi-v7a,armeabi", profile.abi)
    assertEquals(abis, profile.abis)
  }

  @Test
  fun `The remaining fields are carried as given`() = scenario {
    m Given "a tv running sdk 30 with a bucketed density"
    val abis = listOf("x86")

    m When "the profile is built"
    val profile = deviceProfileOf(sdk = 30, abis = abis, isTv = true, bucketedDensity = 320)

    m Then "sdk, tv and density are unchanged"
    assertEquals(30, profile.sdk)
    assertEquals(true, profile.tv)
    assertEquals(320, profile.density)
  }

  @Test
  fun `Blank abis are left out`() = scenario {
    m Given "a device reporting a blank abi among real ones"
    val abis = listOf("arm64-v8a", "", " ")

    m When "the profile is built"
    val profile = deviceProfileOf(sdk = 34, abis = abis, isTv = false, bucketedDensity = 480)

    m Then "only the real one is sent"
    assertEquals("arm64-v8a", profile.abi)
  }

  @Test
  fun `No abi is sent when the device reports none`() = scenario {
    m Given "a device reporting no usable abi"
    val abis = listOf("", " ")

    m When "the profile is built"
    val profile = deviceProfileOf(sdk = 34, abis = abis, isTv = false, bucketedDensity = 480)

    m Then "the parameter is left out rather than sent empty"
    assertNull(profile.abi)
    assertEquals(emptyList<String>(), profile.abis)
  }
}
