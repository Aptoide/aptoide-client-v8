package com.aptoide.android.aptoidegames.play_and_earn.domain.mmp

import cm.aptoide.pt.feature_campaigns.CampaignRepository
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
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
  )

  private fun sender(
    repository: CampaignRepository,
    template: PaEMmpClickTemplate? = this.template,
    wallet: String? = "0xAbC123",
    scope: TestScope = TestScope(),
  ) = PaEMmpClickSender(
    urlSource = FakeUrlSource(template),
    campaignRepository = repository,
    walletAddress = { wallet },
    advertisingId = { "gaid-1" },
    newClickId = { "click-1" },
    scope = scope,
  )

  @Test
  fun `Sends the built click URL once`() = coScenario {
    m Given "a signed-in user and a click template for the game"
    val repository = FakeCampaignRepository()

    m When "the click is sent"
    sender(repository).sendNow("com.game")

    m Then "the filled-in URL is called once"
    assertEquals(
      listOf(
        "https://app.appsflyer.com/x?pid=aptoide_int&clickid=click-1" +
          "&advertising_id=gaid-1&af_c_id=campaign-7&af_sub1=0xAbC123"
      ),
      repository.knocked
    )
  }

  @Test
  fun `Sending in the background calls the click URL`() = coScenario {
    m Given "a signed-in user and a click template for the game"
    val repository = FakeCampaignRepository()
    val scope = TestScope()

    m When "the click is sent and the background work runs"
    sender(repository, scope = scope).send("com.game")
    scope.advanceUntilIdle()

    m Then "the URL is called once"
    assertEquals(1, repository.knocked.size)
  }

  @Test
  fun `A failing call does not escape the background send`() = coScenario {
    m Given "an HTTP client that rejects the URL"
    val repository = FakeCampaignRepository(failure = IllegalArgumentException("bad url"))
    val scope = TestScope()

    m When "the click is sent and the background work runs"
    sender(repository, scope = scope).send("com.game")
    scope.advanceUntilIdle()

    m Then "the failure is contained: the scope is still alive and nothing was called"
    assertTrue(scope.isActive)
    assertTrue(repository.knocked.isEmpty())
  }

  @ParameterizedTest(name = "wallet \"{0}\"")
  @NullSource
  @ValueSource(strings = ["", " "])
  fun `Sends nothing without a wallet`(wallet: String?) = coScenario {
    m Given "no wallet address"
    val repository = FakeCampaignRepository()

    m When "the click is sent"
    sender(repository, wallet = wallet).sendNow("com.game")

    m Then "nothing is called"
    assertTrue(repository.knocked.isEmpty())
  }

  @Test
  fun `Sends nothing without a click template`() = coScenario {
    m Given "a game with no MMP click link"
    val repository = FakeCampaignRepository()

    m When "the click is sent"
    sender(repository, template = null).sendNow("com.game")

    m Then "nothing is called"
    assertTrue(repository.knocked.isEmpty())
  }
}
