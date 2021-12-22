package cm.aptoide.pt.notification

import cm.aptoide.pt.install.InstalledAppsRepository
import cm.aptoide.pt.notification.policies.CampaignPolicy
import io.reactivex.Single
import org.junit.Test

class CampaignPolicyTest {
  private val installedAppsRepository: InstalledAppsRepository =
    object : InstalledAppsRepository {
      override fun getInstalledAppsNames(): Single<List<String>> {
        return Single.just(listOf("second.package", "third.package"))
      }
    }
  private val installedAppsRepository2: InstalledAppsRepository = object : InstalledAppsRepository {
    override fun getInstalledAppsNames(): Single<List<String>> {
      return Single.just(listOf("forth.package", "fifth.package"))
    }
  }
  private val installedAppsRepository3: InstalledAppsRepository = object : InstalledAppsRepository {
    override fun getInstalledAppsNames(): Single<List<String>> {
      return Single.just(listOf("wut.package"))
    }
  }

  @Test
  fun shouldShowNotificationPolicy_exactlyone() {
    val test = CampaignPolicy(listOf("third.package"), installedAppsRepository).shouldShow().test()
    test.assertValue(true)
  }

  @Test
  fun shouldShowNotificationPolicy_atleastone() {
    val test = CampaignPolicy(
      listOf("first.package", "second.package", "third.package"),
      installedAppsRepository
    ).shouldShow().test()
    test.assertValue(true)
  }

  @Test
  fun shouldNotShowNotificationPolicy_missmatch() {
    val test =
      CampaignPolicy(
        listOf("first.package", "second.package"),
        installedAppsRepository2
      ).shouldShow()
        .test()
    test.assertValue(false)
  }

  @Test
  fun shouldShowNotificationPolicy_emptyWhitelist() {
    val test = CampaignPolicy(emptyList(), installedAppsRepository3).shouldShow().test()
    test.assertValue(true)
  }
}