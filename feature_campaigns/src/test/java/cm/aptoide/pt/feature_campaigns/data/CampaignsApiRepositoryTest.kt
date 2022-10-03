package cm.aptoide.pt.feature_campaigns.data

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.aptoide_network.data.network.base_response.DataList
import cm.aptoide.pt.feature_campaigns.Campaign
import cm.aptoide.pt.feature_campaigns.CampaignImpl
import cm.aptoide.pt.feature_campaigns.CampaignRepository
import cm.aptoide.pt.util.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@ExperimentalCoroutinesApi
internal class CampaignsApiRepositoryTest {

  @Test
  fun `Without Campaigns`() = coScenario {
    m Given "api mock without campaigns"
    val api = ApiMock()
    m And "campaign repository"
    val campaignRepository = CampaignRepositoryMock()
    m And "repository with Api"
    val repository = CampaignsApiRepository(api, campaignRepository)

    m When "requesting for a campaign"
    val result = repository.getCampaigns("package")

    m Then "no campaigns available"
    Assertions.assertEquals(listOf<Campaign>(), result)
  }


  @Test
  fun `With 1 Campaign`() = coScenario {
    m Given "api mock with one campaign"
    val api = ApiMock(
      listOf(
        CampaignJson(
          campaign = CampaignDataJson(
            id = 1,
            name = "campaign1",
            label = "campaign1"
          ),
          urls = CampaignUrlsJson(
            clicks = listOf("url1"),
            downloads = listOf("url2"),
            installs = listOf("url3")
          )
        )
      )
    )
    m And "campaign repository"
    val campaignRepository = CampaignRepositoryMock()
    m And "repository with Api"
    val repository = CampaignsApiRepository(api, campaignRepository)

    m When "requesting for a campaign"
    val result = repository.getCampaigns("package")

    m Then "one campaigns available"
    Assertions.assertEquals(
      listOf(
        CampaignImpl(
          id = 1,
          name = "campaign1",
          label = "campaign1",
          clicks = listOf("url1"),
          downloads = listOf("url2"),
          installs = listOf("url3"),
          repository = campaignRepository
        )
      ), result
    )
  }

  @Test
  fun `With 2 Campaign`() = coScenario {
    m Given "api mock with multiple campaign"
    val api = ApiMock(
      listOf(
        CampaignJson(
          campaign = CampaignDataJson(
            id = 1,
            name = "campaign1",
            label = "campaign1"
          ),
          urls = CampaignUrlsJson(
            clicks = listOf("url1"),
            downloads = listOf("url2"),
            installs = listOf("url3")
          )
        ), CampaignJson(
          campaign = CampaignDataJson(
            id = 2,
            name = "campaign2",
            label = "campaign2"
          ),
          urls = CampaignUrlsJson(
            clicks = listOf("url4"),
            downloads = listOf("url5"),
            installs = listOf("url6")
          )
        )
      )
    )
    m And "campaign repository"
    val campaignRepository = CampaignRepositoryMock()
    m And "repository with Api"
    val repository = CampaignsApiRepository(api, campaignRepository)

    m When "requesting for campaigns"
    val result = repository.getCampaigns("package")

    m Then "multiple campaigns available"
    Assertions.assertEquals(
      listOf(
        CampaignImpl(
          id = 1,
          name = "campaign1",
          label = "campaign1",
          clicks = listOf("url1"),
          downloads = listOf("url2"),
          installs = listOf("url3"),
          repository = campaignRepository
        ), CampaignImpl(
          id = 2,
          name = "campaign2",
          label = "campaign2",
          clicks = listOf("url4"),
          downloads = listOf("url5"),
          installs = listOf("url6"),
          repository = campaignRepository
        )
      ), result
    )
  }

  @Test
  fun `Error in getting Campaigns`() = coScenario {
    m Given "api mock campaigns"
    val api = ApiMock(null)
    m And "campaign repository"
    val campaignRepository = CampaignRepositoryMock()
    m And "repository with Api"
    val repository = CampaignsApiRepository(api, campaignRepository)

    m When "requesting for a campaign"
    val result =
      assertThrows<java.lang.IllegalArgumentException> { repository.getCampaigns("package") }

    m Then "an error is forwarded"
    Assertions.assertEquals("error message", result.message)
  }

  private class ApiMock(
    private val result: List<CampaignJson>? = emptyList(),
  ) : CampaignsApi {

    override suspend fun getCampaigns(packageName: String): BaseV7DataListResponse<CampaignJson> =
      if (result == null) {
        throw IllegalArgumentException("error message")
      } else {
        BaseV7DataListResponse<CampaignJson>().apply {
          datalist =
            DataList<CampaignJson>().apply {
              list = result
            }
        }
      }
  }

  private class CampaignRepositoryMock : CampaignRepository {
    override suspend fun knock(url: String) = Unit
  }

}