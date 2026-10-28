package cm.aptoide.pt.feature_apkfy.domain

import cm.aptoide.pt.device_api.error.DeviceApiException
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.domain.AppOrigin
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

/**
 * Resolves apkfy's title through v7 first, as it always has. Only when v7 fails, and a build
 * binds a [fallback], is the title asked of the second catalog - once, by package name.
 *
 * v7 titles are passed through as they are. A fallback title is judged installable or not
 * here, as the catalog lists titles it does not serve files for yet.
 */
class ApkfyAppResolver(
  private val v7: suspend (source: String) -> App,
  private val fallback: ApkfyCatalogFallback?,
  private val deviceSdk: () -> Int,
) {

  suspend fun resolve(model: ApkfyModel): ApkfyResolution {
    val v7Error = try {
      return ApkfyResolution.Resolved(v7(model.asSource()))
    } catch (e: CancellationException) {
      throw e
    } catch (e: Throwable) {
      e
    }
    val v7NotFound = v7Error is HttpException && v7Error.code() == HTTP_NOT_FOUND
    val packageName = model.packageName
    if (fallback == null || packageName == null) {
      return v7Failure(v7Error, v7NotFound, packageName)
    }
    val app = try {
      fallback.detail(packageName)
    } catch (e: CancellationException) {
      throw e
    } catch (e: DeviceApiException.NotInVariant) {
      return v7Failure(v7Error, v7NotFound, packageName)
    } catch (e: Throwable) {
      return ApkfyResolution.Failed(AppOrigin.DEVICE_API, e)
    }
    return ApkfyResolution.Resolved(app, app.unavailableReason(deviceSdk()))
  }

  // Not found only when v7 said so too: after a v7 outage, whether the title exists is unknown
  private fun v7Failure(error: Throwable, notFound: Boolean, packageName: String?) =
    if (notFound) {
      ApkfyResolution.NotFound(packageName)
    } else {
      ApkfyResolution.Failed(AppOrigin.V7, error)
    }

  private companion object {
    const val HTTP_NOT_FOUND = 404
  }
}

/**
 * Why this title cannot be installed on a device running [deviceSdk], or null when it can.
 * The installer names and verifies every file by its checksum, so a file without one is as
 * good as missing.
 */
internal fun App.unavailableReason(deviceSdk: Int): UnavailableReason? {
  val hasBase = file.path.isNotBlank()
  val files = listOfNotNull(file.takeIf { hasBase }) + aab?.baseSplits.orEmpty().map { it.file }
  return when {
    versionCode == 0 || files.isEmpty() || files.any { it.md5.isBlank() } ->
      UnavailableReason.NOT_INSTALLABLE_YET

    (minSdk ?: 0) > deviceSdk -> UnavailableReason.MIN_SDK
    !hasBase -> UnavailableReason.NO_COMPATIBLE_SPLIT
    else -> null
  }
}
