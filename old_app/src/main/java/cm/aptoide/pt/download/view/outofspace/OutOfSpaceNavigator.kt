package cm.aptoide.pt.download.view.outofspace

import android.app.Activity
import android.content.Intent
import cm.aptoide.pt.navigator.FragmentNavigator
import cm.aptoide.pt.navigator.Result

class OutOfSpaceNavigator(private val fragmentNavigator: FragmentNavigator,
                          private val packageName: String) {

  fun clearedEnoughSpace() {
    fragmentNavigator.popDialogWithResult(
        Result(OutOfSpaceDialogFragment.OUT_OF_SPACE_REQUEST_CODE, Activity.RESULT_OK,
            Intent().setPackage(packageName)))
  }
}