package cm.aptoide.pt.download.view

import android.app.Activity
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import cm.aptoide.pt.download.view.outofspace.OutOfSpaceDialogFragment
import cm.aptoide.pt.download.view.outofspace.OutOfSpaceDialogFragment.Companion.newInstance
import cm.aptoide.pt.download.view.outofspace.OutOfSpaceNavigatorWrapper
import cm.aptoide.pt.navigator.FragmentNavigator
import cm.aptoide.pt.navigator.Result
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

  fun openOutOfSpaceDialog(requiredSpace: Long, packageName: String): Completable {
    return Completable.fromAction {
      fragmentNavigator.navigateToDialogForResult(
          newInstance(requiredSpace, packageName),
          OutOfSpaceDialogFragment.OUT_OF_SPACE_REQUEST_CODE)
    }
  }

  fun outOfSpaceDialogResult(): Observable<OutOfSpaceNavigatorWrapper> {
    return fragmentNavigator.results(OutOfSpaceDialogFragment.OUT_OF_SPACE_REQUEST_CODE)
        .map { result: Result ->
          OutOfSpaceNavigatorWrapper(result.resultCode == Activity.RESULT_OK,
              if (result.data != null) result.data!!
                  .getPackage() else "")
        }
  }
}