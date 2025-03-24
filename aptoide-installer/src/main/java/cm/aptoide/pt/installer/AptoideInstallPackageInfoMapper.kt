package cm.aptoide.pt.installer

import android.os.Environment
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.DynamicSplit
import cm.aptoide.pt.feature_apps.data.File
import cm.aptoide.pt.feature_apps.data.Split
import cm.aptoide.pt.feature_apps.data.hasObb
import cm.aptoide.pt.feature_apps.data.isAab
import cm.aptoide.pt.feature_apps.data.isInCatappult
import cm.aptoide.pt.feature_apps.domain.AppMetaUseCase
import cm.aptoide.pt.install_info_mapper.domain.InstallPackageInfoMapper
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.InstallationFile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideInstallPackageInfoMapper @Inject constructor(
  private val appMetaUseCase: AppMetaUseCase,
) : InstallPackageInfoMapper {
  override suspend fun map(app: App): InstallPackageInfo {
    val appMeta = app.takeIf(App::hasMeta) ?: appMetaUseCase.getMetaInfo(source = app.asSource())

    return InstallPackageInfo(
      versionCode = appMeta.versionCode.toLong(),
      installationFiles = mutableSetOf<InstallationFile>()
        .apply {
          add(appMeta.file.toInstallationFile(InstallationFile.Type.BASE))

          appMeta.obb?.run {
            add(main.toInstallationFile(InstallationFile.Type.OBB_MAIN))
            patch?.let { add(it.toInstallationFile(InstallationFile.Type.OBB_PATCH)) }
          }

          appMeta.aab
            ?.takeIf { it.requiredSplitTypes.isNotEmpty() || it.baseSplits.isNotEmpty() }
            ?.run {
              //Checks if all the required split types are present in the splits list.
              //For example, if an ABI split is required, it checks if there is at least one ABI split present
              (requiredSplitTypes - baseSplits.map(Split::type))
                .takeIf { it.isNotEmpty() }
                ?.also { throw IllegalStateException("AAB required $it splits not found") }
              baseSplits.forEach {
                add(it.file.toInstallationFile(InstallationFile.Type.BASE))
              }

              dynamicSplits.forEach {
                val type = it.installationFileType
                add(it.file.toInstallationFile(type))
                addAll(it.splits.map { it.file.toInstallationFile(type) })
              }
            }
        },
      payload = TemporaryPayload(
        isInCatappult = appMeta.isInCatappult(),
        isAab = appMeta.isAab(),
        hasObb = appMeta.hasObb(),
        trustedBadge = appMeta.malware
      ).toString()
    ).filter()
  }
}

private fun File.toInstallationFile(type: InstallationFile.Type) = InstallationFile(
  name = fileName.takeIf { it.endsWith(".apk") } ?: "$fileName.apk",
  type = type,
  md5 = md5,
  fileSize = size,
  url = path,
  altUrl = path_alt,
  localPath = Environment.getExternalStorageDirectory().absolutePath + "/.aptoide/"
)

private val DynamicSplit.installationFileType
  get() =
    when (type) {
      DynamicSplit.Type.ASSET -> "PAD_$deliveryType"
      DynamicSplit.Type.FEATURE -> "PFD_$deliveryType"
      else -> throw IllegalArgumentException("Unknown split type")
    }
      .let(InstallationFile.Type::valueOf)

private fun InstallPackageInfo.filter() = this.installationFiles.let {
  val containsSplits = this.installationFiles.find {
    it.type.toString().run { startsWith("PFD_") || startsWith("PAD_") }
  } != null

  this.copy(
    installationFiles = this.installationFiles.filter {
      it.type == InstallationFile.Type.BASE
        || it.type in allowedSplitTypes
        || (it.type in obbInstallationFileTypes && !containsSplits)
    }.toSet()
  )
}

private val allowedSplitTypes = listOf(
  InstallationFile.Type.PFD_INSTALL_TIME,
  InstallationFile.Type.PAD_INSTALL_TIME
)

private val obbInstallationFileTypes = listOf(
  InstallationFile.Type.OBB_MAIN,
  InstallationFile.Type.OBB_PATCH
)
