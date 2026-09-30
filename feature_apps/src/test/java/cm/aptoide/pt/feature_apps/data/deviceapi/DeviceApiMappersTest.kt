package cm.aptoide.pt.feature_apps.data.deviceapi

import cm.aptoide.pt.feature_apps.data.deviceapi.model.AppResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.AppSummaryResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.ArtifactResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.RatingBucketResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.RatingResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.ReleaseResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.ScreenshotResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.VideoResponse
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

  private val detail = AppResponse(
    packageName = "com.my.defense",
    name = "Rush Royale",
    iconUrl = "https://img/icon",
    publisherName = "MY.GAMES",
    summary = "Tower defense",
    description = "A long description",
    downloads = 2_000L,
    aptoideDownloads = 10L,
    rating = RatingResponse(average = 4.2, count = 300, distribution = null),
    screenshots = listOf(ScreenshotResponse(url = "https://img/s1", width = 1920, height = 1080)),
    videos = listOf(VideoResponse(url = "https://video/v1", thumbnailUrl = null, kind = null)),
    featureGraphicUrl = "https://img/graphic",
    whatsNew = "Update 37.1",
    developerWebsite = "https://rr.my.games",
    developerEmail = "support@my.games",
    privacyPolicyUrl = "https://my.games/privacy",
    aptoideBilling = true,
    release = ReleaseResponse(
      versionName = "37.1.135194",
      versionCode = 135194,
      minSdk = 24,
      sizeBytes = 902_932_742L,
      releasedAt = "2026-08-18T13:33:50Z",
      artifacts = listOf(
        ArtifactResponse(
          kind = "apk",
          url = "https://dl/app.apk",
          sizeBytes = 902_932_742L,
          md5 = "96fb146242d4cbffd306e1c8464d133f",
          filename = null,
        )
      ),
      permissions = listOf("android.permission.INTERNET"),
    ),
  )

  @Test
  fun `A detail keeps what the app view shows`() = scenario {
    m Given "a complete detail"

    m When "it is mapped"
    val app = detail.toApp(storeName = "a-store")!!

    m Then "description, news, developer and links are carried over"
    assertEquals("A long description", app.description)
    assertEquals("Update 37.1", app.news)
    assertEquals("MY.GAMES", app.developerName)
    assertEquals("https://rr.my.games", app.website)
    assertEquals("support@my.games", app.email)
    assertEquals("https://my.games/privacy", app.privacyPolicy)
    assertEquals(listOf("https://img/s1"), app.screenshots?.map { it.url })
    assertEquals(listOf("https://video/v1"), app.videos)
    assertEquals(listOf("android.permission.INTERNET"), app.permissions)
    assertEquals(AppOrigin.DEVICE_API, app.origin)
  }

  @Test
  fun `A screenshot without dimensions gets a landscape shape`() = scenario {
    m Given "a detail whose screenshots come without width and height, as the Play catalog sends"
    val undimensioned = detail.copy(
      screenshots = listOf(ScreenshotResponse(url = "https://img/s1", width = null, height = null))
    )

    m When "it is mapped"
    val screenshot = undimensioned.toApp(storeName = "a-store")!!.screenshots!!.single()

    m Then "it has a landscape shape to be laid out with, as a zero one cannot be"
    assertEquals(16, screenshot.width / (screenshot.height / 9))
    assertTrue(screenshot.width > 0 && screenshot.height > 0)
  }

  @Test
  fun `A detail carries its version and its release date`() = scenario {
    m Given "a detail with a release"

    m When "it is mapped"
    val app = detail.toApp(storeName = "a-store")!!

    m Then "the version and the dates are those of the release"
    assertEquals("37.1.135194", app.versionName)
    assertEquals(135194, app.versionCode)
    assertEquals("2026-08-18 13:33:50", app.releaseDate)
    assertEquals("2026-08-18 13:33:50", app.updateDate)
    assertEquals("2026-08-18 13:33:50", app.modifiedDate)
  }

  @Test
  fun `A detail with an apk artifact carries it as its file`() = scenario {
    m Given "a detail whose release has an apk artifact"

    m When "it is mapped"
    val app = detail.toApp(storeName = "a-store")!!

    m Then "the file points at it, with its size and checksum"
    assertEquals("https://dl/app.apk", app.file.path)
    assertEquals(902_932_742L, app.file.size)
    assertEquals("96fb146242d4cbffd306e1c8464d133f", app.file.md5)
    assertEquals("96fb146242d4cbffd306e1c8464d133f", app.md5)
    assertNull(app.aab)
    assertNull(app.obb)
  }

  @Test
  fun `Split and expansion artifacts map to the app bundle and its expansion files`() =
    scenario {
      m Given "a detail whose release ships a base apk, two splits and both expansion files"
      val artifact = ArtifactResponse(kind = "apk", url = "https://dl/base.apk", sizeBytes = 100)
      val bundled = detail.copy(
        release = detail.release?.copy(
          artifacts = listOf(
            artifact,
            artifact.copy(kind = "split", url = "https://dl/config.arm64.apk", filename = "arm64"),
            artifact.copy(
              kind = "split",
              url = "https://dl/config.xxhdpi.apk",
              filename = "xxhdpi",
            ),
            artifact.copy(kind = "obb_main", url = "https://dl/main.obb", sizeBytes = 1_000),
            artifact.copy(kind = "obb_patch", url = "https://dl/patch.obb", sizeBytes = 10),
          )
        )
      )

      m When "it is mapped"
      val app = bundled.toApp(storeName = "a-store")!!

      m Then "the splits, the expansion files and the size follow"
      assertEquals(listOf("arm64", "xxhdpi"), app.aab?.baseSplits?.map { it.type })
      assertEquals("https://dl/main.obb", app.obb?.main?.path)
      assertEquals("https://dl/patch.obb", app.obb?.patch?.path)
      assertEquals(100L + 100 + 100 + 1_000 + 10, app.appSize)
    }

  @Test
  fun `A detail without artifacts has nothing to install with`() = scenario {
    m Given "a detail as the Play catalog sends it, with no version and no artifacts"
    val playCatalog = detail.copy(
      release = detail.release?.copy(
        versionName = "",
        versionCode = 0,
        sizeBytes = 0,
        artifacts = emptyList(),
      ),
      rating = null,
    )

    m When "it is mapped"
    val app = playCatalog.toApp(storeName = "a-store")!!

    m Then "version, size and file are empty, and it still needs no further details"
    assertEquals("", app.versionName)
    assertEquals(0, app.versionCode)
    assertEquals("", app.file.path)
    assertEquals(0L, app.appSize)
    assertEquals(0L, app.rating.totalVotes)
  }

  @Test
  fun `Empty permissions read as unknown`() = scenario {
    m Given "a detail whose release lists no permissions"
    val bare = detail.copy(release = detail.release?.copy(permissions = emptyList()))

    m When "it is mapped"
    val app = bare.toApp(storeName = "a-store")!!

    m Then "there is no list, as the app view hides an absent one"
    assertNull(app.permissions)
  }

  @Test
  fun `A detail flagged for Aptoide billing maps to an AppCoins app`() = scenario {
    m Given "a detail with the billing flag on"

    m When "it is mapped"
    val app = detail.toApp(storeName = "a-store")!!

    m Then "it is an AppCoins app"
    assertTrue(app.isAppCoins)
  }
}
