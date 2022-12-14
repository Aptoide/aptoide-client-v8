package cm.aptoide.pt.feature_campaigns

import cm.aptoide.pt.util.gherkin.coScenario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * AS a Project Developer,
 * I WANT to send analytics events on open conversion, download and install,
 * FOR tracking the campaign
 */

@ExperimentalCoroutinesApi
internal class CampaignImplTest {

  @Test
  fun `Without clicks urls`() = coScenario {
    m Given "Repository mock"
    val repository = RepositoryMock()
    m And "Campaign has no clicks urls"
    val campaign =
      CampaignImpl(
        impressions = listOf("dog", "cat"),
        clicks = listOf(),
        repository = repository
      )

    m When "sending open conversion event"
    campaign.sendClickEvent()

    m Then "No event sent"
    assertEquals(listOf<String>(), repository.eventUrls)
  }

  @Test
  fun `With 1 clicks url`() = coScenario {
    m Given "Repository mock"
    val repository = RepositoryMock()
    m And "Campaign has 1 click url"
    val campaign =
      CampaignImpl(
        impressions = listOf("dog", "cat"),
        clicks = listOf("URL"),
        repository = repository
      )

    m When "sending open conversion event"
    campaign.sendClickEvent()

    m Then "One event sent"
    assertEquals(listOf("URL"), repository.eventUrls)
  }

  @Test
  fun `With several clicks urls`() = coScenario {
    m Given "Repository mock"
    val repository = RepositoryMock()
    m And "Campaign has 3 click urls"
    val campaign =
      CampaignImpl(
        impressions = listOf("dog", "cat"),
        clicks = listOf("URL1", "URL2", "URL3"),
        repository = repository
      )

    m When "sending open conversion event"
    campaign.sendClickEvent()

    m Then "3 events sent"
    assertEquals(listOf("URL1", "URL2", "URL3"), repository.eventUrls)
  }

  @Test
  fun `Without impressions urls`() = coScenario {
    m Given "Repository mock"
    val repository = RepositoryMock()
    m And "Campaign has no installs urls"
    val campaign =
      CampaignImpl(
        impressions = listOf(),
        clicks = listOf("one", "bird"),
        repository = repository
      )

    m When "sending install event"
    campaign.sendImpressionEvent()

    m Then "No event sent"
    assertEquals(listOf<String>(), repository.eventUrls)
  }

  @Test
  fun `With 1 impression url`() = coScenario {
    m Given "Repository mock"
    val repository = RepositoryMock()
    m And "Campaign has 1 installs url"
    val campaign =
      CampaignImpl(
        impressions = listOf("URL"),
        clicks = listOf("one", "bird"),
        repository = repository
      )

    m When "sending install event"
    campaign.sendImpressionEvent()

    m Then "One event sent"
    assertEquals(listOf("URL"), repository.eventUrls)
  }

  @Test
  fun `With several impression urls`() = coScenario {
    m Given "Repository mock"
    val repository = RepositoryMock()
    m And "Campaign has 3 installs urls"
    val campaignImpl =
      CampaignImpl(
        impressions = listOf("URL1", "URL2", "URL3"),
        clicks = listOf("one", "bird"),
        repository = repository
      )

    m When "sending install event"
    campaignImpl.sendImpressionEvent()

    m Then "3 events sent"
    assertEquals(listOf("URL1", "URL2", "URL3"), repository.eventUrls)
  }

  private class RepositoryMock(
    val eventUrls: MutableList<String> = mutableListOf(),
  ) : CampaignRepository {
    override suspend fun knock(url: String) = withContext(Dispatchers.IO) {
      eventUrls.add(url)
      Unit
    }
  }
}
