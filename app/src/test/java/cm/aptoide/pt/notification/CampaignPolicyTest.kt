package cm.aptoide.pt.notification

import cm.aptoide.pt.install.InstalledApps
import cm.aptoide.pt.notification.policies.CampaignPolicy
import io.reactivex.Single
import org.junit.Test

class CampaignPolicyTest {
  private val installedApps: InstalledApps =
    object : InstalledApps {
      override fun getInstalledAppsNames(): Single<List<String>> {
        return Single.just(listOf("second.package", "third.package"))
      }
    }
  private val installedAppsOther: InstalledApps = object : InstalledApps {
    override fun getInstalledAppsNames(): Single<List<String>> {
      return Single.just(listOf("forth.package", "fifth.package"))
    }
  }

  @Test
  fun shouldShowNotificationPolicy_exactlyone() {
    val test = CampaignPolicy(listOf("third.package"), installedApps).shouldShow().test()
    test.assertValue(true)
  }

  @Test
  fun shouldShowNotificationPolicy_atleastone() {
    val test = CampaignPolicy(
      listOf("first.package", "second.package", "third.package"),
      installedApps
    ).shouldShow().test()
    test.assertValue(true)
  }

  @Test
  fun shouldNotShowNotificationPolicy_missmatch() {
    val test =
      CampaignPolicy(listOf("first.package", "second.package"), installedAppsOther).shouldShow()
        .test()
    test.assertValue(false)
  }

  @Test
  fun shouldNotShowNotificationPolicy_emptyWhitelist() {
    val test = CampaignPolicy(emptyList(), installedApps).shouldShow().test()
    test.assertValue(false)
  }
}