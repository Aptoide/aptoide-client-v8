package cm.aptoide.pt.download.view

import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import cm.aptoide.pt.download.view.outofspace.OutOfSpaceDialogFragment
import cm.aptoide.pt.download.view.outofspace.OutOfSpaceDialogFragment.Companion.newInstance
import cm.aptoide.pt.navigator.FragmentNavigator
import cm.aptoide.pt.utils.AptoideUtils
import rx.Completable
import rx.Observable

class DownloadNavigator(val fragment: Fragment,
                        val packageManager: PackageManager,
                        val fragmentNavigator: FragmentNavigator) {

  fun openApp(packageName: String): Completable {
    return Completable.fromAction {
      AptoideUtils.SystemU.openApp(packageName, packageManager, fragment.context)
    }
  }

  fun openOutOfSpaceDialog(requiredSpace: Long): Completable {
    return Completable.fromAction {
      fragmentNavigator.navigateToDialogForResult(
          newInstance(requiredSpace),
          OutOfSpaceDialogFragment.OUT_OF_SPACE_REQUEST_CODE)
    }
  }

  fun outOfSpaceDialogResults(): Observable<Int> {
    return fragmentNavigator.results(OutOfSpaceDialogFragment.OUT_OF_SPACE_REQUEST_CODE)
        .map { it.resultCode }
  }
}