package cm.aptoide.pt.download.view.outofspace

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import cm.aptoide.pt.file.FileManager
import cm.aptoide.pt.install.InstallAppSizeValidator
import cm.aptoide.pt.install.InstallManager
import cm.aptoide.pt.logger.Logger
import cm.aptoide.pt.utils.AptoideUtils
import rx.Completable
import rx.Observable
import java.io.File


class OutOfSpaceManager(
    private val packageManager: PackageManager,
    private val installManager: InstallManager,
    private val requiredSpace: Long,
    private val fileManager: FileManager,
    private val installAppSizeValidator: InstallAppSizeValidator) {

  fun getInstalledApps(): Observable<List<InstalledApp>> {
    return Observable.from(
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA))
        .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) != 0 }
        .map {
          InstalledApp(packageManager.getApplicationLabel(it).toString(), it.packageName,
              AptoideUtils.SystemU.getApkIconPath(
                  packageManager.getPackageInfo(it.packageName, PackageManager.GET_META_DATA)),
              File(packageManager.getApplicationInfo(it.packageName, 0).publicSourceDir).length())
        }.toList()
  }

  fun uninstallApp(packageName: String?): Completable {
    return installManager.uninstallApp(packageName)
  }

  fun clearSpaceFromCache(): Observable<Boolean> {
    return fileManager.deleteCache(false)
        .map { requiredSpace < installAppSizeValidator.getAvailableSpace() }
  }
}
