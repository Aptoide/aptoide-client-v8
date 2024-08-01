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

  val unusedCampaignTuple = listOf(CampaignTuple("one", "dog"), CampaignTuple("two", "cats"))

  @ParameterizedTest(name = "{0}")
  @MethodSource("inputProvider")
  fun `Sending click events`(
    comment: String,
    type: String,
    urls: List<CampaignTuple>,
    sentEvents: List<String>,
  ) = coScenario {
    m Given "Repository mock"
    val repository = RepositoryMock()
    m And "Campaign has provided clicks urls"
    val campaign = CampaignImpl(
      impressions = unusedCampaignTuple,
      clicks = urls,
      downloads = unusedCampaignTuple,
      repository = repository,
    )

    m When "sending click event"
    campaign.sendClickEvent(type)

    m Then "Events sent as expected"
    assertEquals(sentEvents, repository.eventUrls)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("inputProvider")
  fun `Sending impression events`(
    comment: String,
    type: String,
    urls: List<CampaignTuple>,
    sentEvents: List<String>,
  ) = coScenario {
    m Given "Repository mock"
    val repository = RepositoryMock()
    m And "Campaign has provided installs urls"
    val campaign = CampaignImpl(
      impressions = urls,
      clicks = unusedCampaignTuple,
      downloads = unusedCampaignTuple,
      repository = repository,
    )

    m When "sending impression event"
    campaign.sendImpressionEvent(type)

    m Then "Events sent as expected"
    assertEquals(sentEvents, repository.eventUrls)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("inputProvider")
  fun `Sending download events`(
    comment: String,
    type: String,
    urls: List<CampaignTuple>,
    sentEvents: List<String>,
  ) = coScenario {
    m Given "Repository mock"
    val repository = RepositoryMock()
    m And "Campaign has provided installs urls"
    val campaign = CampaignImpl(
      impressions = unusedCampaignTuple,
      clicks = unusedCampaignTuple,
      downloads = urls,
      repository = repository,
    )

    m When "sending download event"
    campaign.sendDownloadEvent(type)

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
      val type = if (it % 2 == 0) "type" else "nonExistingType"
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
      val suffix = if (it % 2 == 0) "with type" else "without nonExistingType"
      Arguments.arguments(
        "$prefix $suffix",
        type,
        iUrls,
        iUrls.filter { it1 -> it1.name == type }
          .map(CampaignTuple::url),
      )
    }

    private val urls = listOf(
      CampaignTuple("type", "URL1"),
      CampaignTuple("otherType", "URL2"),
      CampaignTuple("type", "URL3")
    )
  }
}
