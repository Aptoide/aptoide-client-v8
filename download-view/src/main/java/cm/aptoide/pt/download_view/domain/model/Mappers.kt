package cm.aptoide.pt.download_view.domain.model

import android.os.Environment
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.InstallationFile

fun App.getInstallPackageInfo(
  payloadMapper: PayloadMapper,
): InstallPackageInfo = getInstallPackageInfo(payloadMapper.getPayloadFrom(this))

fun App.getInstallPackageInfo(
  payload: String?,
): InstallPackageInfo =
  InstallPackageInfo(
    versionCode = versionCode.toLong(),
    installationFiles = mutableSetOf<InstallationFile>()
      .apply {
        with(file) {
          path?.let { path ->
            path_alt?.let { path_alt ->
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
          }
        }
        obb?.run {
          with(main) {
            path?.let { path ->
              path_alt?.let { path_alt ->
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
            }
          }
          patch?.run {
            path?.let { path ->
              path_alt?.let { path_alt ->
                add(
                  InstallationFile(
                    name = fileName,
                    type = InstallationFile.Type.OBB_PATCH,
                    md5 = md5,
                    fileSize = filesize,
                    url = path,
                    altUrl = path_alt,
                    localPath = Environment.getExternalStorageDirectory().absolutePath + "/.aptoide/"
                  )
                )
              }
            }
          }
        }
      },
    payload = payload
  )
