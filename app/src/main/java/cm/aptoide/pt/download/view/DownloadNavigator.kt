package cm.aptoide.pt.download.view

import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import cm.aptoide.pt.utils.AptoideUtils
import rx.Completable

class DownloadNavigator(val fragment: Fragment,
                        val packageManager: PackageManager) {

  fun openApp(packageName: String): Completable {
    return Completable.fromAction {
      AptoideUtils.SystemU.openApp(packageName, packageManager, fragment.context)
    }
  }
}