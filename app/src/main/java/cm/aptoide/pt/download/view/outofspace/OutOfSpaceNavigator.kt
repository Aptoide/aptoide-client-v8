package cm.aptoide.pt.download.view.outofspace

import android.app.Activity
import cm.aptoide.pt.navigator.FragmentNavigator
import cm.aptoide.pt.navigator.Result

class OutOfSpaceNavigator(private val fragmentNavigator: FragmentNavigator) {

  fun backToDownload() {
    fragmentNavigator.popDialogWithResult(
        Result(OutOfSpaceDialogFragment.OUT_OF_SPACE_REQUEST_CODE, Activity.RESULT_OK, null))
  }
}