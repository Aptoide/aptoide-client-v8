package cm.aptoide.pt.home.more.appcoins

import android.app.Activity
import cm.aptoide.pt.download.view.outofspace.OutOfSpaceDialogFragment
import cm.aptoide.pt.download.view.outofspace.OutOfSpaceNavigatorWrapper
import cm.aptoide.pt.navigator.FragmentNavigator
import cm.aptoide.pt.navigator.Result
import rx.Completable
import rx.Observable

class EarnAppcNavigator(val fragmentNavigator: FragmentNavigator) {

  fun openOutOfSpaceDialog(requiredSpace: Long, packageName: String): Completable {
    return Completable.fromAction {
      fragmentNavigator.navigateToDialogForResult(
          OutOfSpaceDialogFragment.newInstance(requiredSpace, packageName),
          OutOfSpaceDialogFragment.OUT_OF_SPACE_REQUEST_CODE)
    }
  }

  fun outOfSpaceDialogResult(): Observable<OutOfSpaceNavigatorWrapper> {
    return fragmentNavigator.results(OutOfSpaceDialogFragment.OUT_OF_SPACE_REQUEST_CODE)
        .map { result: Result ->
          OutOfSpaceNavigatorWrapper(result.resultCode == Activity.RESULT_OK,
              if (result.data != null) result.data!!
                .getPackage()!! else "")
        }
  }
}