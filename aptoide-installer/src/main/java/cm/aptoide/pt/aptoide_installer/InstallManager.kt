package cm.aptoide.pt.aptoide_installer

interface InstallManager {
  fun download(packageName: String)

  fun install(packageName: String)

  fun getDownload(packageName: String)

  fun cancelDownload(packageName: String)

  fun getActiveDownloads(packageName: String)

}