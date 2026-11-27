package com.aptoide.android.aptoidegames.play_and_earn.domain.mmp

import cm.aptoide.pt.campaigns.domain.PaEMmpPlatform
import cm.aptoide.pt.feature_campaigns.CampaignRepository
import cm.aptoide.pt.test.gherkin.coScenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource

internal class PaEMmpClickSenderTest {

  private class FakeCampaignRepository(private val failure: Exception? = null) :
    CampaignRepository {
    val knocked = mutableListOf<String>()
    override suspend fun knock(url: String) {
      failure?.let { throw it }
      knocked += url
    }
  }

  private class FakeUrlSource(private val template: PaEMmpClickTemplate?) : PaEMmpClickUrlSource {
    override suspend fun clickTemplate(packageName: String) = template
  }

  private val template = PaEMmpClickTemplate(
    url = "https://app.appsflyer.com/x?pid=aptoide_int&clickid={transaction_id}" +
      "&advertising_id={google_aid}&af_c_id={offer_id}",
    campaignId = "campaign-7",
    mmp = PaEMmpPlatform.APPSFLYER,
  )

  private fun sender(
    repository: CampaignRepository,
    template: PaEMmpClickTemplate? = this.template,
    wallet: String? = "0xAbC123",
  ) = PaEMmpClickSender(
    urlSource = FakeUrlSource(template),
    campaignRepository = repository,
    walletAddress = { wallet },
    advertisingId = { "gaid-1" },
  )

  @Test
  fun `Sends the built click URL once`() = coScenario {
    m Given "a signed-in user and a click template for the game"
    val repository = FakeCampaignRepository()

    m When "the click is sent"
    sender(repository).sendNow("com.game", clickId = "click-1")

    m Then "the filled-in URL, with the install's click id, is called once"
    assertEquals(
      listOf(
        "https://app.appsflyer.com/x?pid=aptoide_int&clickid=click-1" +
          "&advertising_id=gaid-1&af_c_id=campaign-7&af_sub1=0xAbC123"
      ),
      repository.knocked
    )
  }

  @ParameterizedTest(name = "wallet \"{0}\"")
  @NullSource
  @ValueSource(strings = ["", " "])
  fun `Sends nothing without a wallet`(wallet: String?) = coScenario {
    m Given "no wallet address"
    val repository = FakeCampaignRepository()

    m When "the click is sent"
    sender(repository, wallet = wallet).sendNow("com.game", clickId = "click-1")

    m Then "nothing is called"
    assertTrue(repository.knocked.isEmpty())
  }

  @Test
  fun `Sends nothing without a click template`() = coScenario {
    m Given "a game with no MMP click link"
    val repository = FakeCampaignRepository()

    m When "the click is sent"
    sender(repository, template = null).sendNow("com.game", clickId = "click-1")

    m Then "nothing is called"
    assertTrue(repository.knocked.isEmpty())
  }

  @Test
  fun `An Adjust template gets the wallet in label`() = coScenario {
    m Given "a signed-in user and an Adjust click template for the game"
    val repository = FakeCampaignRepository()
    val adjust = PaEMmpClickTemplate(
      url = "https://app.adjust.com/abc123?click_id={click_id}&gps_adid={gps_adid}",
      campaignId = "campaign-7",
      mmp = PaEMmpPlatform.ADJUST,
    )

    m When "the click is sent"
    sender(repository, template = adjust).sendNow("com.game", clickId = "click-1")

    m Then "the URL carries the wallet as label, with no af_sub1"
    assertEquals(
      listOf("https://app.adjust.com/abc123?click_id=click-1&gps_adid=gaid-1&label=0xAbC123"),
      repository.knocked
    )
  }
}
