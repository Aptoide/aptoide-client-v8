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

  @Test
  fun `market details opens the app view by package name`() = scenario {
    m Given "a market details URI for a package"
    val uri = "market://details?id=com.dreamgames.royalmatch"

    m When "it is mapped to the client's deep link"
    val deepLink = marketUriToDeepLink(uri, scheme)

    m Then "it is the app view deep link for that package"
    assertEquals(
      scheme + buildAppViewRoute(AppSource.of(appId = null, packageName = "com.dreamgames.royalmatch")),
      deepLink
    )
  }

  @Test
  fun `extra market parameters such as the referrer are ignored`() = scenario {
    m Given "a market details URI carrying a referrer"
    val uri = "market://details?referrer=utm_source%3Dgame&id=com.dreamgames.royalmatch&hl=en"

    m When "it is mapped"
    val deepLink = marketUriToDeepLink(uri, scheme)

    m Then "only the package name is used"
    assertEquals(
      scheme + buildAppViewRoute(AppSource.of(appId = null, packageName = "com.dreamgames.royalmatch")),
      deepLink
    )
  }

  @Test
  fun `market search opens the search screen with the query`() = scenario {
    m Given "a market search URI with a percent-encoded query"
    val uri = "market://search?q=royal%20match"

    m When "it is mapped"
    val deepLink = marketUriToDeepLink(uri, scheme)

    m Then "it is the search deep link with the query percent-encoded"
    assertEquals("ag://search?query=royal%20match", deepLink)
  }

  @Test
  fun `a plus in the market search query is a space`() = scenario {
    m Given "a market search URI with a form-encoded query"
    val uri = "market://search?q=royal+match"

    m When "it is mapped"
    val deepLink = marketUriToDeepLink(uri, scheme)

    m Then "the space survives as a percent-encoded space"
    assertEquals("ag://search?query=royal%20match", deepLink)
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
