package com.aptoide.android.aptoidegames.appview

import cm.aptoide.pt.feature_apps.data.File
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_apps.domain.AppOrigin
import cm.aptoide.pt.feature_apps.domain.Rating
import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// The Play catalog describes an app without rating, downloads, version, size or dates. Such
// gaps are hidden rather than shown as dashes and zeros - but only for apps read from the
// new services, so every v7 app keeps rendering exactly as it does today.
internal class AppDataPresenceTest {

  private val bare = randomApp.copy(
    origin = AppOrigin.DEVICE_API,
    rating = Rating(0.0, 0, null),
    pRating = Rating(0.0, 0, null),
    downloads = 0,
    pDownloads = 0,
    versionName = "",
    versionCode = 0,
    file = File(md5 = "", size = 0, path = "", path_alt = ""),
    aab = null,
    obb = null,
    releaseDate = null,
    updateDate = null,
  )

  @Test
  fun `A device API app without data hides all of it`() = scenario {
    m Given "an app read from the device API with none of the optional data"

    m When "what to show is decided"

    m Then "rating, downloads, version, size and dates are all hidden"
    assertFalse(bare.showsRating)
    assertFalse(bare.showsDownloads)
    assertFalse(bare.showsVersion)
    assertFalse(bare.showsSize)
    assertFalse(bare.showsReleaseDate)
    assertFalse(bare.showsUpdateDate)
  }

  @Test
  fun `A device API app with data shows it`() = scenario {
    m Given "an app read from the device API with every optional field"
    val full = bare.copy(
      pRating = Rating(4.2, 300, null),
      pDownloads = 2_000,
      versionName = "37.1",
      file = File(md5 = "x", size = 10, path = "https://dl/app.apk", path_alt = ""),
      releaseDate = "2026-08-18 13:33:50",
      updateDate = "2026-08-18 13:33:50",
    )

    m When "what to show is decided"

    m Then "everything is shown"
    assertTrue(full.showsRating)
    assertTrue(full.showsDownloads)
    assertTrue(full.showsVersion)
    assertTrue(full.showsSize)
    assertTrue(full.showsReleaseDate)
    assertTrue(full.showsUpdateDate)
  }

  @Test
  fun `A v7 app shows everything as it always did`() = scenario {
    m Given "a v7 app with none of the optional data"
    val v7 = bare.copy(origin = AppOrigin.V7)

    m When "what to show is decided"

    m Then "nothing is hidden, so the other builds render as before"
    assertTrue(v7.showsRating)
    assertTrue(v7.showsDownloads)
    assertTrue(v7.showsVersion)
    assertTrue(v7.showsSize)
    assertTrue(v7.showsReleaseDate)
    assertTrue(v7.showsUpdateDate)
  }

  @Test
  fun `An average without a vote count is still a rating`() = scenario {
    m Given "an app read from the device API with an average and no count"
    val rated = bare.copy(pRating = Rating(4.0, 0, null))

    m When "what to show is decided"

    m Then "the rating is shown"
    assertTrue(rated.showsRating)
  }

  @Test
  fun `A rating with votes but no average is still a rating`() = scenario {
    m Given "an app read from the device API rated by people, averaging zero"
    val rated = bare.copy(pRating = Rating(0.0, 3, null))

    m When "what to show is decided"

    m Then "the rating is shown"
    assertTrue(rated.showsRating)
  }
}
