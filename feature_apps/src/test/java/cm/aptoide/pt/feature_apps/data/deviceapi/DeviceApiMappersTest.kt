package cm.aptoide.pt.feature_apps.data.deviceapi

import cm.aptoide.pt.feature_apps.data.deviceapi.model.AppSummaryResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.RatingBucketResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.RatingResponse
import cm.aptoide.pt.feature_apps.domain.AppOrigin
import cm.aptoide.pt.feature_apps.domain.Rating
import cm.aptoide.pt.feature_apps.domain.Votes
import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// A device API summary describes an app with far fewer fields than a v7 one. What the rest of
// the app reads from the resulting App - above all where it came from and whether it installs
// through Aptoide - is pinned here.
internal class DeviceApiMappersTest {

  private val summary = AppSummaryResponse(
    packageName = "com.kiloo.subwaysurf",
    name = "Subway Surfers",
    iconUrl = "https://img/icon",
    downloads = 1_000L,
    rating = RatingResponse(average = 4.5, count = 10, distribution = null),
    featureGraphicUrl = "https://img/graphic",
    updatedAt = "2026-08-13T12:02:28.423000Z",
    aptoideBilling = false,
  )

  @Test
  fun `A summary keeps what identifies and shows the app`() = scenario {
    m Given "a complete summary"

    m When "it is mapped"
    val app = summary.toApp(storeName = "a-store")!!

    m Then "name, package and images are carried over"
    assertEquals("Subway Surfers", app.name)
    assertEquals("com.kiloo.subwaysurf", app.packageName)
    assertEquals("https://img/icon", app.icon)
    assertEquals("https://img/graphic", app.featureGraphic)
    assertEquals("a-store", app.store.storeName)
  }

  @Test
  fun `A mapped app is marked as read from the device API`() = scenario {
    m Given "any summary"

    m When "it is mapped"
    val app = summary.toApp(storeName = "a-store")!!

    m Then "its origin says so, and it is addressed by package as it has no v7 id"
    assertEquals(AppOrigin.DEVICE_API, app.origin)
    assertEquals("package_name=com.kiloo.subwaysurf", app.asSource())
  }

  @Test
  fun `A summary flagged for Aptoide billing maps to an AppCoins app`() = scenario {
    m Given "a summary with the billing flag on"
    val flagged = summary.copy(aptoideBilling = true)

    m When "it is mapped"
    val app = flagged.toApp(storeName = "a-store")!!

    m Then "it is an AppCoins app, without pretending to carry v7 store flags"
    assertTrue(app.isAppCoins)
    assertNull(app.bdsFlags)
  }

  @Test
  fun `A summary without the billing flag is not an AppCoins app`() = scenario {
    m Given "a summary where the flag is absent"
    val unflagged = summary.copy(aptoideBilling = null)

    m When "it is mapped"
    val app = unflagged.toApp(storeName = "a-store")!!

    m Then "it is not an AppCoins app"
    assertFalse(app.isAppCoins)
  }

  @Test
  fun `A mapped app carries no campaigns`() = scenario {
    m Given "any summary, as the device API sends no campaign urls"

    m When "it is mapped"
    val app = summary.toApp(storeName = "a-store")!!

    m Then "there is nothing to report campaign events with"
    assertNull(app.campaigns)
  }

  @Test
  fun `The rating is carried over with its distribution`() = scenario {
    m Given "a summary rated 4.5 by 10 people, with a distribution"
    val rated = summary.copy(
      rating = RatingResponse(
        average = 4.5,
        count = 10,
        distribution = listOf(RatingBucketResponse(stars = 5, count = 7)),
      )
    )

    m When "it is mapped"
    val app = rated.toApp(storeName = "a-store")!!

    m Then "both ratings hold the same values"
    val expected = Rating(avgRating = 4.5, totalVotes = 10, votes = listOf(Votes(5, 7)))
    assertEquals(expected, app.rating)
    assertEquals(expected, app.pRating)
  }

  @Test
  fun `A missing rating maps to no votes`() = scenario {
    m Given "a summary without a rating, as the Play catalog sends"
    val unrated = summary.copy(rating = null)

    m When "it is mapped"
    val app = unrated.toApp(storeName = "a-store")!!

    m Then "the rating has no votes, which is how its absence is told"
    assertEquals(Rating(avgRating = 0.0, totalVotes = 0, votes = null), app.rating)
  }

  @Test
  fun `Downloads beyond what the app model holds are capped`() = scenario {
    m Given "a summary with more downloads than an Int can hold"
    val popular = summary.copy(downloads = 5_000_000_000L)

    m When "it is mapped"
    val app = popular.toApp(storeName = "a-store")!!

    m Then "they are capped instead of wrapping around to a negative number"
    assertEquals(Int.MAX_VALUE, app.downloads)
    assertEquals(Int.MAX_VALUE, app.pDownloads)
  }

  @Test
  fun `Missing downloads map to zero`() = scenario {
    m Given "a summary without downloads"
    val unknown = summary.copy(downloads = null)

    m When "it is mapped"
    val app = unknown.toApp(storeName = "a-store")!!

    m Then "they are zero, which is how their absence is told"
    assertEquals(0, app.downloads)
    assertEquals(0, app.pDownloads)
  }

  @Test
  fun `The update date is converted to the format the app uses`() = scenario {
    m Given "a summary updated at an ISO-8601 instant"

    m When "it is mapped"
    val app = summary.toApp(storeName = "a-store")!!

    m Then "the dates are in the v7 format, in UTC"
    assertEquals("2026-08-13 12:02:28", app.updateDate)
    assertEquals("2026-08-13 12:02:28", app.modifiedDate)
  }

  @Test
  fun `An update date that cannot be read is left out`() = scenario {
    m Given "a summary whose update date is not a date"
    val broken = summary.copy(updatedAt = "yesterday")

    m When "it is mapped"
    val app = broken.toApp(storeName = "a-store")!!

    m Then "there is no date rather than a wrong one"
    assertNull(app.updateDate)
    assertEquals("", app.modifiedDate)
  }

  @Test
  fun `A summary carries no version nor file`() = scenario {
    m Given "any summary"

    m When "it is mapped"
    val app = summary.toApp(storeName = "a-store")!!

    m Then "version and file are empty, and it still needs its details fetched"
    assertEquals("", app.versionName)
    assertEquals(0, app.versionCode)
    assertEquals("", app.file.path)
    assertEquals(0L, app.appSize)
    assertFalse(app.hasMeta)
  }

  @Test
  fun `A summary without a package cannot become an app`() = scenario {
    m Given "a summary with no package name"
    val anonymous = summary.copy(packageName = null)

    m When "it is mapped"
    val app = anonymous.toApp(storeName = "a-store")

    m Then "there is no app"
    assertNull(app)
  }
}
