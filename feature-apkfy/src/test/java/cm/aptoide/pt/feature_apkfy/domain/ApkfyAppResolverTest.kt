package cm.aptoide.pt.feature_apkfy.domain

import cm.aptoide.pt.device_api.error.DeviceApiException
import cm.aptoide.pt.feature_apps.data.Aab
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.File
import cm.aptoide.pt.feature_apps.data.Obb
import cm.aptoide.pt.feature_apps.data.Split
import cm.aptoide.pt.feature_apps.data.emptyApp
import cm.aptoide.pt.feature_apps.domain.AppOrigin
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

// Apkfy keeps resolving through v7 and asks the device API only for what v7 could not resolve,
// so cohort titles Aptoide never distributed can still be shown and, once served, installed.
// What each catalog's answer turns into - shown, shown but not installable, nothing - is
// pinned here, as it decides both the screen and what gets reported.
@OptIn(ExperimentalCoroutinesApi::class)
internal class ApkfyAppResolverTest {

  @Test
  fun `A title v7 resolves never reaches the device API`() = coScenario {
    m Given "v7 holds the title and a device API fallback is bound"
    val fallback = FakeFallback { error("must not be called") }
    val resolver = resolver(v7 = { v7App }, fallback = fallback)

    m When "apkfy resolves it"
    val resolution = resolver.resolve(model)

    m Then "it is v7's title, installable, and the device API was not asked"
    assertEquals(ApkfyResolution.Resolved(v7App), resolution)
    assertEquals(0, fallback.calls)
  }

  @Test
  fun `A v7 title is passed through untouched even when it looks empty`() = coScenario {
    m Given "v7 answers with a title that has no version code"
    val bare = v7App.copy(versionCode = 0)
    val resolver = resolver(v7 = { bare }, fallback = FakeFallback { deviceApiApp })

    m When "apkfy resolves it"
    val resolution = resolver.resolve(model)

    m Then "it is installable, as v7 titles are not judged"
    assertEquals(ApkfyResolution.Resolved(bare), resolution)
  }

  @Test
  fun `A title v7 does not know resolves through the device API`() = coScenario {
    m Given "v7 does not know the title and the device API serves it with files"
    val fallback = FakeFallback { deviceApiApp }
    val resolver = resolver(v7 = { throw v7NotFound() }, fallback = fallback)

    m When "apkfy resolves it"
    val resolution = resolver.resolve(model)

    m Then "it is the device API's title, installable, asked once by package name"
    assertEquals(ApkfyResolution.Resolved(deviceApiApp.complete()), resolution)
    assertEquals(listOf("com.spotify.music"), fallback.packageNames)
  }

  @Test
  fun `Any v7 failure falls back to the device API`() = coScenario {
    m Given "v7 fails with a server error and the device API serves the title"
    val resolver = resolver(v7 = { throw httpError(503) }, fallback = FakeFallback { deviceApiApp })

    m When "apkfy resolves it"
    val resolution = resolver.resolve(model)

    m Then "it is the device API's title"
    assertEquals(ApkfyResolution.Resolved(deviceApiApp.complete()), resolution)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("unavailableReleases")
  fun `A device API title that cannot be installed is shown with its reason`(
    case: String,
    app: App,
    reason: UnavailableReason,
  ) = coScenario {
    m Given "v7 does not know the title and the device API serves it as: $case"
    val resolver = resolver(v7 = { throw v7NotFound() }, fallback = FakeFallback { app })

    m When "apkfy resolves it"
    val resolution = resolver.resolve(model)

    m Then "the title resolves, unavailable for that reason"
    assertEquals(ApkfyResolution.Resolved(app.complete(), reason), resolution)
  }

  @Test
  fun `A release the device runs exactly at its minimum is installable`() = coScenario {
    m Given "a device API title whose minimum Android is the device's"
    val app = deviceApiApp.copy(minSdk = DEVICE_SDK)
    val resolver = resolver(v7 = { throw v7NotFound() }, fallback = FakeFallback { app })

    m When "apkfy resolves it"
    val resolution = resolver.resolve(model)

    m Then "it is installable"
    assertEquals(ApkfyResolution.Resolved(app.complete()), resolution)
  }

  @Test
  fun `A title neither catalog knows is not found`() = coScenario {
    m Given "v7 and the device API both answer that the title does not exist"
    val resolver = resolver(
      v7 = { throw v7NotFound() },
      fallback = FakeFallback { throw DeviceApiException.NotInVariant("not in the catalog") },
    )

    m When "apkfy resolves it"
    val resolution = resolver.resolve(model)

    m Then "it is not found"
    assertEquals(ApkfyResolution.NotFound("com.spotify.music"), resolution)
  }

  @Test
  fun `A device API miss after a v7 outage reports the outage, not an unknown title`() =
    coScenario {
      m Given "v7 fails with a server error and the device API does not hold the title"
      val outage = httpError(503)
      val resolver = resolver(
        v7 = { throw outage },
        fallback = FakeFallback { throw DeviceApiException.NotInVariant("not in the catalog") },
      )

      m When "apkfy resolves it"
      val resolution = resolver.resolve(model)

      m Then "it failed in v7, with v7's error"
      assertEquals(ApkfyResolution.Failed(AppOrigin.V7, outage), resolution)
    }

  @ParameterizedTest(name = "{0}")
  @MethodSource("deviceApiFailures")
  fun `A device API failure is a failure, asked once`(case: String, error: Throwable) =
    coScenario {
      m Given "v7 does not know the title and the device API fails with $case"
      val fallback = FakeFallback { throw error }
      val resolver = resolver(v7 = { throw v7NotFound() }, fallback = fallback)

      m When "apkfy resolves it"
      val resolution = resolver.resolve(model)

      m Then "it failed in the device API, which was not asked again"
      assertEquals(ApkfyResolution.Failed(AppOrigin.DEVICE_API, error), resolution)
      assertEquals(1, fallback.calls)
    }

  @Test
  fun `Without a fallback a title v7 does not know is not found`() = coScenario {
    m Given "a build with no fallback bound, and v7 does not know the title"
    val resolver = resolver(v7 = { throw v7NotFound() }, fallback = null)

    m When "apkfy resolves it"
    val resolution = resolver.resolve(model)

    m Then "it is not found"
    assertEquals(ApkfyResolution.NotFound("com.spotify.music"), resolution)
  }

  @Test
  fun `Without a fallback a v7 failure is a failure`() = coScenario {
    m Given "a build with no fallback bound, and v7 is down"
    val outage = IOException("offline")
    val resolver = resolver(v7 = { throw outage }, fallback = null)

    m When "apkfy resolves it"
    val resolution = resolver.resolve(model)

    m Then "it failed in v7"
    assertEquals(ApkfyResolution.Failed(AppOrigin.V7, outage), resolution)
  }

  @Test
  fun `A title known by its id alone does not fall back`() = coScenario {
    m Given "an apkfy model with an app id but no package name, which v7 does not know"
    val fallback = FakeFallback { deviceApiApp }
    val resolver = resolver(v7 = { throw v7NotFound() }, fallback = fallback)

    m When "apkfy resolves it"
    val resolution = resolver.resolve(model.copy(packageName = null, appId = 42L))

    m Then "it is not found, as the device API is asked by package name only"
    assertEquals(ApkfyResolution.NotFound(null), resolution)
    assertEquals(0, fallback.calls)
  }

  @Test
  fun `v7 is asked by the model's source`() = coScenario {
    m Given "an apkfy model with an app id and a package name"
    val sources = mutableListOf<String>()
    val resolver = resolver(v7 = { sources += it; v7App }, fallback = null)

    m When "apkfy resolves it"
    resolver.resolve(model.copy(appId = 42L))

    m Then "v7 is asked as before, by app id"
    assertEquals(listOf("app_id=42"), sources)
  }

  @Test
  fun `Cancelling the v7 lookup does not fall back`() = coScenario {
    m Given "the v7 lookup is cancelled"
    val fallback = FakeFallback { deviceApiApp }
    val resolver = resolver(v7 = { throw CancellationException("left") }, fallback = fallback)

    m When "apkfy resolves it"
    val thrown = assertThrows<CancellationException> { resolver.resolve(model) }

    m Then "the cancellation propagates and the device API is not asked"
    assertEquals("left", thrown.message)
    assertEquals(0, fallback.calls)
  }

  @Test
  fun `Cancelling the device API lookup propagates`() = coScenario {
    m Given "v7 does not know the title and the device API lookup is cancelled"
    val cancellation = CancellationException("left")
    val resolver = resolver(
      v7 = { throw v7NotFound() },
      fallback = FakeFallback { throw cancellation },
    )

    m When "apkfy resolves it"
    val thrown = assertThrows<CancellationException> { resolver.resolve(model) }

    m Then "the cancellation propagates, it is no failure to report"
    assertSame(cancellation, thrown)
  }

  @Test
  fun `A device API title keeps where it came from`() = coScenario {
    m Given "v7 does not know the title and the device API serves it"
    val resolver = resolver(v7 = { throw v7NotFound() }, fallback = FakeFallback { deviceApiApp })

    m When "apkfy resolves it"
    val resolution = resolver.resolve(model) as ApkfyResolution.Resolved

    m Then "its origin says device API, which the install analytics report"
    assertTrue(resolution.app.origin == AppOrigin.DEVICE_API)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("malformedFiles")
  fun `A device API title with files the installer cannot trust is not installable`(
    case: String,
    malform: (App) -> App,
  ) = coScenario {
    m Given "v7 does not know the title and the device API serves it with $case"
    val app = malform(deviceApiApp)
    val resolver = resolver(v7 = { throw v7NotFound() }, fallback = FakeFallback { app })

    m When "apkfy resolves it"
    val resolution = resolver.resolve(model) as ApkfyResolution.Resolved

    m Then "it is shown but not installable yet"
    assertEquals(UnavailableReason.NOT_INSTALLABLE_YET, resolution.unavailableReason)
  }

  @Test
  fun `A device API title with well-formed expansion files is installable`() = coScenario {
    m Given "v7 does not know the title and the device API serves it with both expansion files"
    val app = deviceApiApp.copy(obb = Obb(main = file("main"), patch = file("patch")))
    val resolver = resolver(v7 = { throw v7NotFound() }, fallback = FakeFallback { app })

    m When "apkfy resolves it"
    val resolution = resolver.resolve(model) as ApkfyResolution.Resolved

    m Then "it is installable"
    assertEquals(null, resolution.unavailableReason)
  }

  @Test
  fun `A device API title needs no further lookup before it installs`() = coScenario {
    m Given "v7 does not know the title and the device API serves it"
    val resolver = resolver(v7 = { throw v7NotFound() }, fallback = FakeFallback { deviceApiApp })

    m When "apkfy resolves it"
    val resolution = resolver.resolve(model) as ApkfyResolution.Resolved

    m Then "it is complete, so the installer does not ask v7 again, which does not know it"
    assertTrue(resolution.app.hasMeta)
  }

  @Test
  fun `A device API answer for another package is a failure`() = coScenario {
    m Given "v7 does not know the title and the device API answers with a different package"
    val other = deviceApiApp.copy(packageName = "com.evil.app")
    val resolver = resolver(v7 = { throw v7NotFound() }, fallback = FakeFallback { other })

    m When "apkfy resolves it"
    val resolution = resolver.resolve(model)

    m Then "it failed in the device API, and nothing is shown or installed"
    assertTrue(resolution is ApkfyResolution.Failed)
    assertEquals(AppOrigin.DEVICE_API, (resolution as ApkfyResolution.Failed).source)
  }

  private fun resolver(
    v7: suspend (String) -> App,
    fallback: ApkfyCatalogFallback?,
  ) = ApkfyAppResolver(v7 = v7, fallback = fallback, deviceSdk = { DEVICE_SDK })

  private class FakeFallback(private val answer: suspend (String) -> App) : ApkfyCatalogFallback {
    val packageNames = mutableListOf<String>()
    val calls get() = packageNames.size

    override suspend fun detail(packageName: String): App {
      packageNames += packageName
      return answer(packageName)
    }
  }

  companion object {
    private const val DEVICE_SDK = 30

    // What the resolver hands on for a device API title: complete, needing no further lookup
    private fun App.complete() = copy(hasMeta = true)

    private val model = ApkfyModel(
      packageName = "com.spotify.music",
      appId = null,
      oemId = null,
      guestUid = "guest",
      utmSource = null,
      utmMedium = null,
      utmCampaign = null,
      utmTerm = null,
      utmContent = null,
    )

    private val v7App = emptyApp.copy(
      packageName = "com.roblox.client",
      versionCode = 1,
      file = file("base"),
    )

    private val deviceApiApp = emptyApp.copy(
      packageName = "com.spotify.music",
      versionCode = 9,
      file = file("base"),
      aab = Aab(
        requiredSplitTypes = emptyList(),
        baseSplits = listOf(Split(type = "arm64", file = file("arm64"))),
      ),
      origin = AppOrigin.DEVICE_API,
      minSdk = 24,
    )

    private fun file(name: String) = File(
      md5 = name.md5(),
      size = 10,
      path = "https://dl/$name.apk",
      path_alt = "",
    )

    // A distinct, well-formed checksum per file name
    private fun String.md5() = "%032x".format(hashCode().toLong() and 0xffffffffL)

    private val noFile = File(md5 = "", size = 0, path = "", path_alt = "")

    @JvmStatic
    fun unavailableReleases() = listOf(
      arrayOf(
        "the sentinel release, no version and no files",
        deviceApiApp.copy(versionCode = 0, file = noFile, aab = null),
        UnavailableReason.NOT_INSTALLABLE_YET,
      ),
      arrayOf(
        "a version but no files",
        deviceApiApp.copy(file = noFile, aab = null),
        UnavailableReason.NOT_INSTALLABLE_YET,
      ),
      arrayOf(
        "files but no version",
        deviceApiApp.copy(versionCode = 0),
        UnavailableReason.NOT_INSTALLABLE_YET,
      ),
      arrayOf(
        "a base apk without a checksum",
        deviceApiApp.copy(file = file("base").copy(md5 = "")),
        UnavailableReason.NOT_INSTALLABLE_YET,
      ),
      arrayOf(
        "a split without a checksum",
        deviceApiApp.copy(
          aab = deviceApiApp.aab!!.copy(
            baseSplits = listOf(Split(type = "arm64", file = file("arm64").copy(md5 = "")))
          )
        ),
        UnavailableReason.NOT_INSTALLABLE_YET,
      ),
      arrayOf(
        "a minimum Android above the device's",
        deviceApiApp.copy(minSdk = DEVICE_SDK + 1),
        UnavailableReason.MIN_SDK,
      ),
      arrayOf(
        "splits but no base apk",
        deviceApiApp.copy(file = noFile),
        UnavailableReason.NO_COMPATIBLE_SPLIT,
      ),
      arrayOf(
        "the sentinel release with a minimum above the device's",
        deviceApiApp.copy(versionCode = 0, file = noFile, aab = null, minSdk = DEVICE_SDK + 5),
        UnavailableReason.NOT_INSTALLABLE_YET,
      ),
    )

    @JvmStatic
    fun malformedFiles() = listOf(
      arrayOf("a base apk whose checksum is not an md5", { a: App ->
        a.copy(file = a.file.copy(md5 = "../../databases/x"))
      }),
      arrayOf("a base apk served over http", { a: App ->
        a.copy(file = a.file.copy(path = "http://dl/base.apk"))
      }),
      arrayOf("a split with no url", { a: App ->
        a.copy(aab = a.aab!!.copy(baseSplits = listOf(Split("arm64", file("arm64").copy(path = "")))))
      }),
      arrayOf("two files with the same checksum", { a: App ->
        a.copy(aab = a.aab!!.copy(baseSplits = listOf(Split("arm64", file("base")))))
      }),
      arrayOf("an expansion file without a checksum", { a: App ->
        a.copy(obb = Obb(main = file("main").copy(md5 = ""), patch = null))
      }),
      arrayOf("an expansion patch served over http", { a: App ->
        a.copy(obb = Obb(main = file("main"), patch = file("patch").copy(path = "http://dl/p")))
      }),
    )

    @JvmStatic
    fun deviceApiFailures() = listOf(
      arrayOf("410 removed", DeviceApiException.Removed("taken down")),
      arrayOf("451 unavailable in the country", DeviceApiException.CountryUnavailable("blocked")),
      arrayOf("403 age restricted", DeviceApiException.AgeRestricted(18, "adults only")),
      arrayOf("502 upstream down", DeviceApiException.Upstream("source down")),
      arrayOf("500", DeviceApiException.Generic(500, "boom")),
      arrayOf("no connectivity", IOException("offline")),
    )

    private fun v7NotFound() = httpError(404)

    private fun httpError(code: Int) =
      HttpException(Response.error<Any>(code, "{}".toResponseBody()))
  }
}
