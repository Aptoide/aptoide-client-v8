package cm.aptoide.pt.download_view.domain.usecase

import cm.aptoide.pt.aptoide_installer.InstallManager
import cm.aptoide.pt.aptoide_installer.model.*
import cm.aptoide.pt.feature_apps.data.App
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class DownloadAppUseCase @Inject constructor(
  private val installManager: InstallManager,
  private val cachePath: String
) {

  suspend fun downloadApp(app: App) {
    installManager.download(
      Download(
        app.name,
        app.packageName,
        app.md5,
        app.icon,
        app.versionName,
        app.versionCode,
        app.isAppCoins,
        app.appSize,
        DownloadState.INSTALL,
        0,
        getDownloadFiles(app),
        DownloadAction.INSTALL,
        app.malware!!,
        app.store.storeName
      )
    )
  }

  private fun getDownloadFiles(app: App): List<DownloadFile> {
    val downloadFilesList = ArrayList<DownloadFile>()
    downloadFilesList.add(
      DownloadFile(
        app.file.md5,
        app.file.path,
        app.file.path_alt,
        app.packageName,
        app.versionCode,
        app.versionName,
        app.file.md5,
        FileType.APK,
        SubFileType.SUBTYPE_APK,
        cachePath
      )
    )

    if (app.obb != null) {
      val main = app.obb!!.main
      downloadFilesList.add(
        DownloadFile(
          app.file.md5,
          main.path,
          main.path_alt,
          app.packageName,
          app.versionCode,
          app.versionName,
          main.md5, FileType.OBB, SubFileType.MAIN, cachePath
        )
      )
      if (app.obb!!.patch != null) {
        val patch = app.obb!!.patch
        patch?.let {
          downloadFilesList.add(
            DownloadFile(
              app.file.md5,
              it.path,
              patch.path_alt,
              app.packageName,
              app.versionCode,
              app.versionName,
              patch.md5,
              FileType.OBB,
              SubFileType.PATCH,
              cachePath
            )
          )
        }
      }
    }
    return downloadFilesList
  }
}