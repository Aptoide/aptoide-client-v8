package com.aptoide.android.aptoidegames

import cm.aptoide.pt.feature_apps.domain.AppSource
import cm.aptoide.pt.test.gherkin.scenario
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

// Other apps open their store page through the Play-style `market://` intent. Direct builds join
// Android's store chooser for it, so a market URI has to land on the matching in-app screen: the
// app view for `details`, the search screen for `search`, and nothing for anything else.
internal class MarketDeepLinkTest {

  private val scheme = "ag://"
  private val royalMatch = "com.dreamgames.royalmatch"
  private val royalMatchAppView =
    scheme + buildAppViewRoute(AppSource.of(appId = null, packageName = royalMatch))
  private val royalMatchSearch = scheme + "search?query=royal%20match"

  @Test
  fun `market details opens the app view by package name`() = scenario {
    m Given "a market details URI for a package"
    val uri = "market://details?id=$royalMatch"

    m When "it is mapped to the client's deep link"
    val deepLink = marketUriToDeepLink(uri, scheme)

    m Then "it is the app view deep link for that package"
    assertEquals(royalMatchAppView, deepLink)
  }

  @Test
  fun `the client's own scheme is used for the deep link`() = scenario {
    m Given "a market details URI and the vanilla scheme"
    val uri = "market://details?id=$royalMatch"

    m When "it is mapped"
    val deepLink = marketUriToDeepLink(uri, "aptoide://")

    m Then "the deep link starts with that scheme"
    assertEquals(
      "aptoide://" + buildAppViewRoute(AppSource.of(appId = null, packageName = royalMatch)),
      deepLink
    )
  }

  @Test
  fun `extra market parameters such as the referrer are ignored`() = scenario {
    m Given "a market details URI carrying a referrer"
    val uri = "market://details?referrer=utm_source%3Dgame&id=$royalMatch&hl=en"

    m When "it is mapped"
    val deepLink = marketUriToDeepLink(uri, scheme)

    m Then "only the package name is used"
    assertEquals(royalMatchAppView, deepLink)
  }

  @Test
  fun `a fragment and a trailing slash on the market URI are ignored`() = scenario {
    m Given "market URIs with a fragment or a trailing slash after the host"
    val fragment = "market://details?id=$royalMatch#ref"
    val slash = "market://details/?id=$royalMatch"
    val searchFragment = "market://search?q=royal%20match#top"

    m When "they are mapped"

    m Then "the fragment and the slash play no part"
    assertEquals(royalMatchAppView, marketUriToDeepLink(fragment, scheme))
    assertEquals(royalMatchAppView, marketUriToDeepLink(slash, scheme))
    assertEquals(royalMatchSearch, marketUriToDeepLink(searchFragment, scheme))
  }

  @Test
  fun `the first of repeated market parameters wins, as Android's Uri does`() = scenario {
    m Given "a market details URI with two ids"
    val uri = "market://details?id=$royalMatch&id=com.other.app"

    m When "it is mapped"
    val deepLink = marketUriToDeepLink(uri, scheme)

    m Then "the first id is used"
    assertEquals(royalMatchAppView, deepLink)
  }

  @Test
  fun `market search opens the search screen with the query`() = scenario {
    m Given "a market search URI with a percent-encoded query"
    val uri = "market://search?q=royal%20match"

    m When "it is mapped"
    val deepLink = marketUriToDeepLink(uri, scheme)

    m Then "it is the search deep link with the query percent-encoded"
    assertEquals(royalMatchSearch, deepLink)
  }

  @Test
  fun `a plus in the market search query is a space`() = scenario {
    m Given "a market search URI with a form-encoded query"
    val uri = "market://search?q=royal+match"

    m When "it is mapped"
    val deepLink = marketUriToDeepLink(uri, scheme)

    m Then "the space survives as a percent-encoded space"
    assertEquals(royalMatchSearch, deepLink)
  }

  @Test
  fun `a literal plus, reserved characters and non-ASCII in the query are percent-encoded`() =
    scenario {
      m Given "market search URIs with a literal plus, route-breaking characters and an accent"
      val plus = "market://search?q=c%2B%2B"
      val reserved = "market://search?q=a%26previousScreen%3Dx%2Fy%3F%7Bz%7D"
      val accent = "market://search?q=caf%C3%A9"

      m When "they are mapped"

      m Then "every one of them travels percent-encoded inside the search deep link"
      assertEquals(scheme + "search?query=c%2B%2B", marketUriToDeepLink(plus, scheme))
      assertEquals(
        scheme + "search?query=a%26previousScreen%3Dx%2Fy%3F%7Bz%7D",
        marketUriToDeepLink(reserved, scheme)
      )
      assertEquals(scheme + "search?query=caf%C3%A9", marketUriToDeepLink(accent, scheme))
    }

  @Test
  fun `surrounding whitespace in the query is trimmed`() = scenario {
    m Given "a market search URI with spaces around the query"
    val uri = "market://search?q=%20royal%20match%20"

    m When "it is mapped"
    val deepLink = marketUriToDeepLink(uri, scheme)

    m Then "the query is trimmed"
    assertEquals(royalMatchSearch, deepLink)
  }

  @Test
  fun `a malformed percent escape is kept as typed instead of crashing`() = scenario {
    m Given "market URIs whose values are not valid percent-encoding"
    val percentQuery = "market://search?q=100%"
    val badEscapeQuery = "market://search?q=%zz"
    val badEscapeId = "market://details?id=com.a.b%zz"

    m When "they are mapped"

    m Then "the query survives re-encoded and the id is rejected"
    assertEquals(scheme + "search?query=100%25", marketUriToDeepLink(percentQuery, scheme))
    assertEquals(scheme + "search?query=%25zz", marketUriToDeepLink(badEscapeQuery, scheme))
    assertNull(marketUriToDeepLink(badEscapeId, scheme))
  }

  @Test
  fun `oversized values are capped`() = scenario {
    m Given "a package name over Android's 255-character limit and a very long query"
    val longPackage = "market://details?id=com." + "a".repeat(260)
    val longQuery = "market://search?q=" + "q".repeat(1000)

    m When "they are mapped"

    m Then "the package is rejected and the query is cut to the cap"
    assertNull(marketUriToDeepLink(longPackage, scheme))
    assertEquals(
      scheme + "search?query=" + "q".repeat(MAX_SEARCH_QUERY_LENGTH),
      marketUriToDeepLink(longQuery, scheme)
    )
  }

  @Test
  fun `a market host the client does not handle maps to nothing`() = scenario {
    m Given "a market URI for a collection"
    val uri = "market://apps/collection/topselling_free"

    m When "it is mapped"
    val deepLink = marketUriToDeepLink(uri, scheme)

    m Then "there is no deep link"
    assertNull(deepLink)
  }

  @Test
  fun `market details without a package name maps to nothing`() = scenario {
    m Given "market details URIs with no usable id"
    val missing = "market://details"
    val blank = "market://details?id="

    m When "they are mapped"

    m Then "there is no deep link"
    assertNull(marketUriToDeepLink(missing, scheme))
    assertNull(marketUriToDeepLink(blank, scheme))
  }

  @Test
  fun `market details with an invalid package name maps to nothing`() = scenario {
    m Given "market details URIs whose id is not an Android package name"
    val path = "market://details?id=../../etc"
    val single = "market://details?id=royalmatch"
    val spaced = "market://details?id=com.dream%20games.royalmatch"

    m When "they are mapped"

    m Then "there is no deep link"
    assertNull(marketUriToDeepLink(path, scheme))
    assertNull(marketUriToDeepLink(single, scheme))
    assertNull(marketUriToDeepLink(spaced, scheme))
  }

  @Test
  fun `market search with a blank query maps to nothing`() = scenario {
    m Given "market search URIs with no usable query"
    val missing = "market://search"
    val blank = "market://search?q=%20%20"

    m When "they are mapped"

    m Then "there is no deep link"
    assertNull(marketUriToDeepLink(missing, scheme))
    assertNull(marketUriToDeepLink(blank, scheme))
  }

  @Test
  fun `a URI that is not a market URI maps to nothing`() = scenario {
    m Given "a URI with another scheme"
    val uri = "https://play.google.com/store/apps/details?id=com.dreamgames.royalmatch"

    m When "it is mapped"
    val deepLink = marketUriToDeepLink(uri, scheme)

    m Then "there is no deep link"
    assertNull(deepLink)
  }
}
