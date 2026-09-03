package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppRepository
import cm.aptoide.pt.feature_apps.data.SplitsRepository
import cm.aptoide.pt.feature_apps.data.isPixelBlast
import cm.aptoide.pt.feature_apps.data.model.DynamicSplitJSON
import cm.aptoide.pt.feature_apps.data.toDomainModel
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppMetaUseCase @Inject constructor(
  private val appRepository: AppRepository,
  private val splitsRepository: SplitsRepository,
  private val featureFlags: FeatureFlags,
) {
  suspend fun getMetaInfo(source: String): App {
    val app = appRepository.getAppMeta(source = source.filterMetaSource()).copy(hasMeta = true)
    if (app.isPixelBlast()) return app.withPixelBlastApkOverride()

    return app.run {
      aab
        ?.let {
          val dSplits = splitsRepository.getAppsDynamicSplits(md5)
          it.copy(dynamicSplits = dSplits.map(DynamicSplitJSON::toDomainModel))
        }
        ?.let { copy(aab = it) }
        ?: this
    }
  }

  private suspend fun App.withPixelBlastApkOverride(): App {
    val override = featureFlags.getObject(PIXEL_BLAST_APP_FLAG, PixelBlastAppOverride::class.java)
      ?.takeIf { it.url.isNotBlank() && it.md5.isNotBlank() && it.size > 0 }
      ?: return this

    // The pool APK is a single self-contained build (no split delivery), unlike the
    // backend-reported aab/obb splits, which belong to a different, differently-signed
    // build. Mixing the two in one install session causes an INSTALL_FAILED_INVALID_APK
    // "signatures are inconsistent" failure, so both must be dropped here. size must also
    // be overridden: it feeds the pre-download free-space check and the metered-data size
    // warning, both computed from File.size before any bytes are actually downloaded.
    return copy(
      file = file.copy(path = override.url, path_alt = override.url, md5 = override.md5, size = override.size),
      aab = null,
      obb = null,
    )
  }

  private companion object {
    const val PIXEL_BLAST_APP_FLAG = "pixelblast_app"
  }
}

private data class PixelBlastAppOverride(
  val url: String,
  val md5: String,
  val size: Long,
)

fun String.filterMetaSource(): String {
  if (this.contains("getMeta")) {
    return this.split("getMeta/")[1]
  } else if (this.contains("getApp")) {
    return this.split("getApp/")[1]
  } else {
    return this
  }
}
