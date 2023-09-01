package cm.aptoide.pt.feature_campaigns

import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

/**
 * AS a Project Developer,
 * I WANT to send analytics events on open conversion, download and install,
 * FOR tracking the campaign
 */

@ExperimentalCoroutinesApi
internal class CampaignImplTest {

  @ParameterizedTest(name = "{0}")
  @MethodSource("inputProvider")
  fun `Sending click events`(
    comment: String,
    adListId: String?,
    urls: List<String>,
    sentEvents: List<String>,
  ) = coScenario {
    m Given "Repository mock"
    val repository = RepositoryMock()
    m And "Campaign has provided clicks urls"
    val campaign = CampaignImpl(
      impressions = listOf("dog", "cat"),
      clicks = urls,
      repository = repository,
      normalizeImpression = { url, _ -> url },
      normalizeClick = { url, _ , _-> url }
    )
    m And "Campaign has provided adListId"
    campaign.adListId = adListId

    m When "sending open conversion event"
    campaign.sendInstallClickEvent()

    m Then "Events sent as expected"
    assertEquals(sentEvents, repository.eventUrls)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("inputProvider")
  fun `Sending impression events`(
    comment: String,
    adListId: String?,
    urls: List<String>,
    sentEvents: List<String>,
  ) = coScenario {
    m Given "Repository mock"
    val repository = RepositoryMock()
    m And "Campaign has provided installs urls"
    val campaign = CampaignImpl(
      impressions = urls,
      clicks = listOf("one", "bird"),
      repository = repository,
      normalizeImpression = { url, _ -> url },
      normalizeClick = { url, _ , _-> url }
    )
    m And "Campaign has provided adListId"
    campaign.adListId = adListId

    m When "sending install event"
    campaign.sendImpressionEvent()

    m Then "Events sent as expected"
    assertEquals(sentEvents, repository.eventUrls)
  }

  private class RepositoryMock(
    val eventUrls: MutableList<String> = mutableListOf(),
  ) : CampaignRepository {
    override suspend fun knock(url: String) = withContext(Dispatchers.IO) {
      eventUrls.add(url)
      Unit
    }
  }

  companion object {
    @JvmStatic
    fun inputProvider(): List<Arguments> = List(6) {
      val adListId = if (it % 2 == 0) "" else null
      val iUrls = when (it % 3) {
        0 -> emptyList()
        1 -> urls.take(1)
        2 -> urls
        else -> emptyList()
      }
      val prefix = when (it % 3) {
        0 -> "without URLs"
        1 -> "with 1 URL"
        2 -> "with several URLs"
        else -> ""
      }
      val suffix = if (it % 2 == 0) "with adListId" else "without adListId"
      Arguments.arguments(
        "$prefix $suffix",
        adListId,
        iUrls,
        if (it % 2 == 0) iUrls else emptyList(),
      )
    }

    private val urls = listOf("URL1", "URL2", "URL3")
  }
}
