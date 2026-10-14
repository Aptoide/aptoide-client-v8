package com.aptoide.android.aptoidegames.play_and_earn.domain.mmp

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource

internal class PaEMmpClickUrlBuilderTest {

  private val template = "https://app.appsflyer.com/com.game-Aptoide?pid=aptoide_int" +
    "&af_siteid={affiliate_id}&c={offer_ref_id}&af_c_id={offer_id}&af_click_lookback=7d" +
    "&clickid={transaction_id}&advertising_id={google_aid}&idfa={ios_ifa}"

  private val params = PaEMmpClickParams(
    wallet = "0xAbC123",
    clickId = "click-1",
    advertisingId = "38400000-8cf0-11bd-b23e-10b96e40000d",
    campaignId = "campaign-7",
    channel = "aptoide_games",
  )

  @Test
  fun `Fills every placeholder of our template and adds the wallet in af_sub1`() = scenario {
    m Given "our AppsFlyer template and the click values"
    m When "the click URL is built"
    val url = PaEMmpClickUrlBuilder.build(template, params)

    m Then "each placeholder has its value, iOS idfa is gone and the wallet is in af_sub1"
    assertEquals(
      "https://app.appsflyer.com/com.game-Aptoide?pid=aptoide_int" +
        "&af_siteid=aptoide_games&c=campaign-7&af_c_id=campaign-7&af_click_lookback=7d" +
        "&clickid=click-1&advertising_id=38400000-8cf0-11bd-b23e-10b96e40000d" +
        "&af_sub1=0xAbC123",
      url
    )
  }

  private val withoutAdvertisingId =
    "https://app.appsflyer.com/com.game-Aptoide?pid=aptoide_int" +
    "&af_siteid=aptoide_games&c=campaign-7&af_c_id=campaign-7&af_click_lookback=7d" +
    "&clickid=click-1&af_sub1=0xAbC123"

  @ParameterizedTest(name = "advertising id \"{0}\"")
  @NullSource
  @ValueSource(strings = ["", " ", "00000000-0000-0000-0000-000000000000"])
  fun `Drops advertising_id when there is no usable advertising id`(gaid: String?) = scenario {
    m Given "a missing, blank or zeroed advertising id"
    m When "the click URL is built"
    val url = PaEMmpClickUrlBuilder.build(template, params.copy(advertisingId = gaid))

    m Then "only the advertising_id parameter is left out"
    assertEquals(withoutAdvertisingId, url)
  }

  @Test
  fun `Removes parameters whose placeholder we do not know`() = scenario {
    m Given "a template with an unknown placeholder, also glued to other text"
    val withUnknown =
      "https://app.appsflyer.com/x?pid=aptoide_int&foo={bar}&af_sub5={offer_id}af_ad_id"

    m When "the click URL is built"
    val url = PaEMmpClickUrlBuilder.build(withUnknown, params)

    m Then "only known values are kept, no literal placeholder is sent"
    assertEquals(
      "https://app.appsflyer.com/x?pid=aptoide_int&af_sub5=campaign-7af_ad_id&af_sub1=0xAbC123",
      url
    )
  }

  @Test
  fun `Replaces every af_sub1 already in the template`() = scenario {
    m Given "a template that already carries fixed af_sub1 values"
    val withSub1 = "https://app.appsflyer.com/x?pid=aptoide_int&af_sub1=a&af_sub1=b"

    m When "the click URL is built"
    val url = PaEMmpClickUrlBuilder.build(withSub1, params)

    m Then "af_sub1 appears once, with the wallet"
    assertEquals("https://app.appsflyer.com/x?pid=aptoide_int&af_sub1=0xAbC123", url)
  }

  @Test
  fun `Keeps the fragment at the end`() = scenario {
    m Given "a template with a fragment"
    m When "the click URL is built"
    val url =
      PaEMmpClickUrlBuilder.build("https://app.appsflyer.com/x?pid=aptoide_int#top", params)

    m Then "the wallet stays in the query, before the fragment"
    assertEquals("https://app.appsflyer.com/x?pid=aptoide_int&af_sub1=0xAbC123#top", url)
  }

  @Test
  fun `Does not build a link with a placeholder outside the query`() = scenario {
    m Given "a template with a placeholder in its path"
    m When "the click URL is built"
    val url = PaEMmpClickUrlBuilder.build(
      "https://app.appsflyer.com/{offer_id}?pid=aptoide_int",
      params
    )

    m Then "there is no URL to send"
    assertNull(url)
  }

  @Test
  fun `Does not build a link that is not https`() = scenario {
    m Given "a plain http template"
    m When "the click URL is built"
    val url = PaEMmpClickUrlBuilder.build("http://app.appsflyer.com/x?pid=aptoide_int", params)

    m Then "there is no URL to send"
    assertNull(url)
  }

  @Test
  fun `Encodes the values it fills in`() = scenario {
    m Given "a campaign id and a wallet with characters that are not URL-safe"
    m When "the click URL is built"
    val url = PaEMmpClickUrlBuilder.build(
      "https://app.appsflyer.com/x?c={offer_ref_id}",
      params.copy(campaignId = "Dice Roll & Co", wallet = "a=b&c")
    )

    m Then "the values are URL-encoded"
    assertEquals("https://app.appsflyer.com/x?c=Dice+Roll+%26+Co&af_sub1=a%3Db%26c", url)
  }

  @Test
  fun `Adds the query when the template has none`() = scenario {
    m Given "a template without parameters"
    m When "the click URL is built"
    val url = PaEMmpClickUrlBuilder.build("https://app.appsflyer.com/x", params)

    m Then "af_sub1 starts the query"
    assertEquals("https://app.appsflyer.com/x?af_sub1=0xAbC123", url)
  }
}
