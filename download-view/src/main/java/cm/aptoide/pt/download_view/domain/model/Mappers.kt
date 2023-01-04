package cm.aptoide.pt.download_view.domain.model

import android.os.Environment
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_manager.Task
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.InstallationFile
import cm.aptoide.pt.install_manager.dto.Version

interface AppDetailsMapper<D> {
  fun toDetails(app: App): D
}

fun App.getInstallPackageInfo(): InstallPackageInfo =
  InstallPackageInfo(
    version = Version(
      versionName = versionName,
      versionCode = versionCode.toLong()
    ),
    downloadSize = appSize,
    installationFiles = mutableSetOf<InstallationFile>()
      .apply {
        with(file) {
          path?.let { path ->
            path_alt?.let { path_alt ->
              add(
                InstallationFile(
                  name = md5,
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
                    name = md5,
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
                    name = md5,
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
      }
  )

fun <D> cm.aptoide.pt.install_manager.App<D>.lastTaskState() =
  if (installedVersionCode == null) {
    null
  } else {
    Pair(Task.State.COMPLETED, 0)
  }
