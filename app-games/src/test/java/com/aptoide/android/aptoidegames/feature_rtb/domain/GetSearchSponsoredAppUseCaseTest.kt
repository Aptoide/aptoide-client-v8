package com.aptoide.android.aptoidegames.feature_rtb.domain

import cm.aptoide.pt.feature_campaigns.CampaignImpl
import cm.aptoide.pt.test.gherkin.coScenario
import com.aptoide.android.aptoidegames.feature_rtb.data.RTBApp
import com.aptoide.android.aptoidegames.feature_rtb.data.randomRTBApp
import com.aptoide.android.aptoidegames.feature_rtb.repository.RTBRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
internal class GetSearchSponsoredAppUseCaseTest {

  @Test
  fun `Returns first RTB app when repository has apps`() = coScenario {
    m Given "a repository with multiple RTB apps"
    val firstApp = randomRTBApp
    val secondApp = randomRTBApp
    val repository = FakeRTBRepository(apps = listOf(firstApp, secondApp))

    m When "the use case is invoked"
    val useCase = GetSearchSponsoredAppUseCase(repository)
    val result = useCase.invoke()

    m Then "the first app from the list is returned"
    assertEquals(firstApp, result)
  }

  @Test
  fun `Returns null when repository returns empty list`() = coScenario {
    m Given "a repository that returns an empty list"
    val repository = FakeRTBRepository(apps = emptyList())

    m When "the use case is invoked"
    val useCase = GetSearchSponsoredAppUseCase(repository)
    val result = useCase.invoke()

    m Then "null is returned"
    assertNull(result)
  }

  @Test
  fun `Returns null when repository throws IOException`() = coScenario {
    m Given "a repository that throws an IOException"
    val repository = FakeRTBRepository(exception = IOException("Network error"))

    m When "the use case is invoked"
    val useCase = GetSearchSponsoredAppUseCase(repository)
    val result = useCase.invoke()

    m Then "null is returned gracefully"
    assertNull(result)
  }

  @Test
  fun `Returns null when repository throws HttpException`() = coScenario {
    m Given "a repository that throws an HttpException"
    val repository = FakeRTBRepository(
      exception = HttpException(Response.error<Any>(500, "".toResponseBody()))
    )

    m When "the use case is invoked"
    val useCase = GetSearchSponsoredAppUseCase(repository)
    val result = useCase.invoke()

    m Then "null is returned gracefully"
    assertNull(result)
  }

  private class FakeRTBRepository(
    private val apps: List<RTBApp> = emptyList(),
    private val exception: Exception? = null,
  ) : RTBRepository {
    override suspend fun getRTBApps(placement: String): List<RTBApp> {
      if (exception != null) throw exception
      return apps
    }

    override fun getCachedCampaigns(packageName: String): CampaignImpl? = null
  }
}
