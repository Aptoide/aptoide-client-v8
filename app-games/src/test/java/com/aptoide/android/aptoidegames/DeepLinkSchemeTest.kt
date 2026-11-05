package com.aptoide.android.aptoidegames

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// The app's own notifications carry a deep link built with the brand's scheme (ag:// on Aptoide
// Games, aptoide:// on Vanilla). MainActivity must follow the brand's scheme, whichever it is, and
// nothing else: the promocode scheme has its own handling and http(s) is an app link.
internal class DeepLinkSchemeTest {

  @Test
  fun `the Aptoide Games scheme is followed on Aptoide Games`() = scenario {
    m Given "a deep link with the ag scheme"
    val scheme = "ag"

    m When "it is checked against ag://"
    val followed = isBrandDeepLinkScheme(scheme, deepLinkSchema = "ag://")

    m Then "it is followed"
    assertTrue(followed)
  }

  @Test
  fun `the Vanilla scheme is followed on Vanilla`() = scenario {
    m Given "a deep link with the aptoide scheme"
    val scheme = "aptoide"

    m When "it is checked against aptoide://"
    val followed = isBrandDeepLinkScheme(scheme, deepLinkSchema = "aptoide://")

    m Then "it is followed"
    assertTrue(followed)
  }

  @Test
  fun `another brand's scheme is not followed`() = scenario {
    m Given "a deep link with the ag scheme on Vanilla"
    val scheme = "ag"

    m When "it is checked against aptoide://"
    val followed = isBrandDeepLinkScheme(scheme, deepLinkSchema = "aptoide://")

    m Then "it is not followed"
    assertFalse(followed)
  }

  @Test
  fun `schemes with their own handling are not followed`() = scenario {
    m Given "the promocode, http and https schemes"
    val schemes = listOf("promocode", "http", "https")

    m When "each is checked against ag://"
    val followed = schemes.map { isBrandDeepLinkScheme(it, deepLinkSchema = "ag://") }

    m Then "none is followed"
    assertEquals(listOf(false, false, false), followed)
  }

  @Test
  fun `a missing scheme is not followed`() = scenario {
    m Given "a URI without a scheme"
    val scheme: String? = null

    m When "it is checked"
    val followed = isBrandDeepLinkScheme(scheme, deepLinkSchema = "ag://")

    m Then "it is not followed"
    assertFalse(followed)
  }

  @Test
  fun `the scheme comparison is exact`() = scenario {
    m Given "a deep link typed with an upper-case scheme"
    val scheme = "AG"

    m When "it is checked against ag://"
    val followed = isBrandDeepLinkScheme(scheme, deepLinkSchema = "ag://")

    m Then "it is not followed, like before the brand scheme became configurable"
    assertFalse(followed)
  }
}
