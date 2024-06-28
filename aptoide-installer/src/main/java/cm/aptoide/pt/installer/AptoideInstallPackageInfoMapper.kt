package cm.aptoide.pt.installer

import android.os.Environment
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.File
import cm.aptoide.pt.feature_apps.domain.AppMetaUseCase
import cm.aptoide.pt.feature_apps.domain.DynamicSplitsUseCase
import cm.aptoide.pt.install_info_mapper.domain.InstallPackageInfoMapper
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.InstallationFile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideInstallPackageInfoMapper @Inject constructor(
  private val appMetaUseCase: AppMetaUseCase,
  private val dynamicSplitsUseCase: DynamicSplitsUseCase,
) : InstallPackageInfoMapper {
  override suspend fun map(app: App): InstallPackageInfo = InstallPackageInfo(
    versionCode = app.versionCode.toLong(),
    installationFiles = mutableSetOf<InstallationFile>()
      .apply {
        val appMeta = appMetaUseCase.getMetaInfo(app.packageName)

        add(appMeta.file.toInstallationFile(InstallationFile.Type.BASE))

        appMeta.obb?.run {
          add(main.toInstallationFile(InstallationFile.Type.OBB_MAIN))
          patch?.let { add(it.toInstallationFile(InstallationFile.Type.OBB_PATCH)) }
        }

        appMeta.aab?.run {
          //Checks if all the required split types are present in the splits list.
          //For example, if an ABI split is required, it checks if there is at least one ABI split present
          requiredSplitTypes.forEach { type ->
            if (splits.find { it.type == type } == null) {
              throw IllegalStateException("AAB required split types not found")
            }
          }
          splits.forEach {
            add(it.file.toInstallationFile(InstallationFile.Type.PFD_INSTALL_TIME))
          }

          val dynamicSplits = dynamicSplitsUseCase.getDynamicSplits(app.md5)

          dynamicSplits.forEach {
            val type = mapDynamicSplitType(it.type, it.deliveryTypes)
            add(it.file.toInstallationFile(type))
            addAll(it.splits.map { it.toInstallationFile(type) })
          }
        }
      },
    payload = null
  ).filter()
}

private fun File.toInstallationFile(type: InstallationFile.Type) = InstallationFile(
  name = fileName,
  type = type,
  md5 = md5,
  fileSize = filesize,
  url = path,
  altUrl = path_alt,
  localPath = Environment.getExternalStorageDirectory().absolutePath + "/.aptoide/"
)

private fun mapDynamicSplitType(
  splitType: String,
  deliveryTypes: List<String>,
) = deliveryTypes.toMutableSet()
  .let {
    if (it.remove("INSTALL_TIME")) {
      "INSTALL_TIME"
    } else {
      it.first()
    }
  }
  .let {
    when (splitType) {
      "FEATURE" -> "PFD_$it"
      "ASSET" -> "PAD_$it"
      else -> throw IllegalArgumentException("Unknown split type")
    }
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
