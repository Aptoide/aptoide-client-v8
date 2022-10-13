package cm.aptoide.pt.feature_campaigns

import cm.aptoide.pt.util.gherkin.coScenario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * AS a Developer,
 * I WANT to get and track campaigns for provided packages,
 * FOR campaigns usage
 */

@ExperimentalCoroutinesApi
internal class CampaignsUseCaseTest {

  @Test
  fun `No campaigns for the package`() = coScenario {
    m Given "Repository mock without campaigns to return"
    val repository = RepositoryMock()
    m And "Campaigns created with the repository"
    val campaignsUseCase = CampaignsUseCase(repository = repository)

    m When "getting campaigns for the package"
    val result = campaignsUseCase.getCampaign("package")

    m Then "Nothing returned"
    assertNull(result)
  }


  @Test
  fun `One campaign for the package`() = coScenario {
    m Given "Repository mock with one campaign to return"
    val repository = RepositoryMock(mutableListOf(listOf(CampaignMock(1, "campaign1", "label1"))))
    m And "Campaigns created with the repository"
    val campaignsUseCase = CampaignsUseCase(repository = repository)

    m When "getting campaigns for the package"
    val result = campaignsUseCase.getCampaign("package")

    m Then "One campaign returned"
    assertEquals(CampaignMock(1, "campaign1", "label1"), result)
  }


  @Test
  fun `Multiple campaigns for the package`() = coScenario {
    m Given "Repository mock with multiple campaigns to return"
    val repository = RepositoryMock(
      mutableListOf(
        listOf(
          CampaignMock(1, "campaign1", "label1"),
          CampaignMock(2, "campaign2", "label2")
        )
      )
    )
    m And "Campaigns created with the repository"
    val campaignsUseCase = CampaignsUseCase(repository = repository)

    m When "getting campaigns for the package"
    val result = campaignsUseCase.getCampaign("package")

    m Then "First campaign is returned"
    assertEquals(CampaignMock(1, "campaign1", "label1"), result)
  }

  @Test
  fun `Error to get campaigns for the package`() = coScenario {
    m Given "Repository mock with error when returning campaigns"
    val repository = RepositoryMock(
      mutableListOf()
    )
    m And "Campaigns created with the repository"
    val campaignsUseCase = CampaignsUseCase(repository = repository)

    m When "getting campaigns for the package"
    val result = campaignsUseCase.getCampaign("package")

    m Then "Null is returned"
    assertNull(result)
  }

  @Test
  fun `Cached campaign for the package`() = coScenario {
    m Given "Repository mock with multiple campaigns to return"
    val repository = RepositoryMock(
      mutableListOf(
        listOf(
          CampaignMock(1, "campaign1", "label1"),
          CampaignMock(2, "campaign2", "label2")
        )
      )
    )
    m And "Campaigns created with the repository"
    val campaignsUseCase = CampaignsUseCase(repository = repository)

    m When "getting campaigns for the package"
    val result = campaignsUseCase.getCampaign("package")
    m And "again"
    val result2 = campaignsUseCase.getCampaign("package")
    m And "again and again"
    val result3 = campaignsUseCase.getCampaign("package")

    m Then "First campaign is cached returned always"
    assertEquals(CampaignMock(1, "campaign1", "label1"), result)
    assertEquals(CampaignMock(1, "campaign1", "label1"), result2)
    assertEquals(CampaignMock(1, "campaign1", "label1"), result3)
  }


  @Test
  fun `Null cached for the package`() = coScenario {
    m Given "Repository mock without campaigns to return"
    val repository = RepositoryMock()
    m And "Campaigns created with the repository"
    val campaignsUseCase = CampaignsUseCase(repository = repository)

    m When "getting campaigns for the package"
    val result = campaignsUseCase.getCampaign("package")
    m And "again"
    val result2 = campaignsUseCase.getCampaign("package")
    m And "again and again"
    val result3 = campaignsUseCase.getCampaign("package")

    m Then "Null campaign is cached returned always"
    assertNull(result)
    assertNull(result2)
    assertNull(result3)
  }

  @Test
  fun `Cached campaigns for two packages`() = coScenario {
    m Given "Repository mock with multiple campaigns to return"
    val repository = RepositoryMock(
      mutableListOf(
        listOf(
          CampaignMock(1, "campaign1", "label1"),
          CampaignMock(2, "campaign2", "label2")
        ), listOf(
          CampaignMock(3, "campaign3", "label3"),
          CampaignMock(4, "campaign4", "label4")
        )
      )
    )
    m And "Campaigns created with the repository"
    val campaignsUseCase = CampaignsUseCase(repository = repository)

    m When "getting campaigns for the package"
    val result = campaignsUseCase.getCampaign("package")
    m And "again for the first package"
    val result2 = campaignsUseCase.getCampaign("package")

    m And "getting campaigns for the package2"
    val result3 = campaignsUseCase.getCampaign("package2")
    m And "again for the second package"
    val result4 = campaignsUseCase.getCampaign("package2")

    m Then "First campaign is cached returned always"
    assertEquals(CampaignMock(1, "campaign1", "label1"), result)
    assertEquals(CampaignMock(1, "campaign1", "label1"), result2)
    assertEquals(CampaignMock(3, "campaign3", "label3"), result3)
    assertEquals(CampaignMock(3, "campaign3", "label3"), result4)
  }

  private class RepositoryMock(
    val campaigns: MutableList<List<CampaignMock>> = mutableListOf(listOf())
  ) : CampaignsRepository {

    override suspend fun getCampaigns(appPackage: String): List<Campaign> =
      withContext(Dispatchers.IO) {
        campaigns.removeFirst()
      }
  }

  private data class CampaignMock(
    override val id: Long,
    override val name: String,
    override val label: String
  ) : Campaign {
    override suspend fun sendImpressionEvent() = Unit
    override suspend fun sendClickEvent() = Unit
    override suspend fun sendDownloadEvent() = Unit
    override suspend fun sendInstallEvent() = Unit
  }
}
