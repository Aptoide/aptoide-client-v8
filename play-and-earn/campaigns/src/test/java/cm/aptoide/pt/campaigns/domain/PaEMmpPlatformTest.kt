package cm.aptoide.pt.campaigns.domain

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource

// AND-912: the backend's `mmp` value, read leniently so a casing choice on their side never
// turns a known MMP into an unknown one.
internal class PaEMmpPlatformTest {

  @ParameterizedTest(name = "\"{0}\"")
  @ValueSource(strings = ["ADJUST", "adjust", " Adjust "])
  fun `Reads Adjust in any case, with surrounding spaces`(value: String) = scenario {
    m Given "the backend's value for Adjust, spelled one way or another"
    m When "it is read"
    val platform = PaEMmpPlatform.fromApi(value)

    m Then "it is Adjust"
    assertEquals(PaEMmpPlatform.ADJUST, platform)
  }

  @Test
  fun `Reads AppsFlyer`() = scenario {
    m Given "the backend's value for AppsFlyer"
    m When "it is read"
    val platform = PaEMmpPlatform.fromApi("AppsFlyer")

    m Then "it is AppsFlyer"
    assertEquals(PaEMmpPlatform.APPSFLYER, platform)
  }

  @ParameterizedTest(name = "\"{0}\"")
  @NullSource
  @ValueSource(strings = ["", "  ", "branch"])
  fun `Anything else is unknown`(value: String?) = scenario {
    m Given "no value, a blank one, or an MMP this app doesn't know"
    m When "it is read"
    val platform = PaEMmpPlatform.fromApi(value)

    m Then "there is no platform"
    assertNull(platform)
  }
}
