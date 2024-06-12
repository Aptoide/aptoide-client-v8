package cm.aptoide.pt.installer

import android.os.Environment
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.domain.AppMetaUseCase
import cm.aptoide.pt.install_info_mapper.domain.InstallPackageInfoMapper
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.InstallationFile
import cm.aptoide.pt.install_manager.dto.InstallationFile.Type.OBB_PATCH

class InstallPackageInfoMapperImpl(private val appMetaUseCase: AppMetaUseCase) :
  InstallPackageInfoMapper {
  override suspend fun map(app: App): InstallPackageInfo = InstallPackageInfo(
    versionCode = app.versionCode.toLong(),
    installationFiles = mutableSetOf<InstallationFile>()
      .apply {
        with(
          app.file.takeIf { it.path.isNotEmpty() }
            ?: appMetaUseCase.getMetaInfo(app.packageName).file
        ) {
          add(
            InstallationFile(
              name = fileName,
              type = InstallationFile.Type.BASE,
              md5 = md5,
              fileSize = filesize,
              url = path,
              altUrl = path_alt,
              localPath = Environment.getExternalStorageDirectory().absolutePath + "/.aptoide/"
            )
          )
        }
        app.obb?.run {
          with(main) {
            add(
              InstallationFile(
                name = fileName,
                type = InstallationFile.Type.OBB_MAIN,
                md5 = md5,
                fileSize = filesize,
                url = path,
                altUrl = path_alt,
                localPath = Environment.getExternalStorageDirectory().absolutePath + "/.aptoide/"
              )
            )
          }
          patch?.run {
            add(
              InstallationFile(
                name = fileName,
                type = OBB_PATCH,
                md5 = md5,
                fileSize = filesize,
                url = path,
                altUrl = path_alt,
                localPath = Environment.getExternalStorageDirectory().absolutePath + "/.aptoide/"
              )
            )
          }
        }
      },
    payload = null
  )
}
