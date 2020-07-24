package cm.aptoide.pt.download.view

import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import cm.aptoide.pt.R
import cm.aptoide.pt.themes.ThemeManager
import cm.aptoide.pt.utils.AptoideUtils
import cm.aptoide.pt.utils.GenericDialogs
import com.google.android.material.snackbar.Snackbar
import rx.Completable
import rx.Observable

class DownloadDialogManager(val fragment: Fragment,
                            val packageManager: PackageManager,
                            val themeManager: ThemeManager) {


  fun showRootInstallWarningPopup(): Observable<Boolean> {
    return GenericDialogs.createGenericYesNoCancelMessage(fragment.requireContext(), null,
        fragment.resources.getString(R.string.root_access_dialog),
        themeManager.getAttributeForTheme(R.attr.dialogsTheme).resourceId)
        .map { response -> response == GenericDialogs.EResponse.YES }
  }

  fun showDowngradeDialog(): Observable<Boolean> {
    return GenericDialogs.createGenericContinueCancelMessage(fragment.requireContext(), null,
        fragment.resources.getString(R.string.downgrade_warning_dialog),
        themeManager.getAttributeForTheme(R.attr.dialogsTheme).resourceId)
        .map { eResponse -> eResponse == GenericDialogs.EResponse.YES }
  }

  fun showDowngradingSnackBar() {
    fragment.view?.let { v ->
      Snackbar.make(v, R.string.downgrading_msg, Snackbar.LENGTH_SHORT)
          .show()
    }
  }

  fun openApp(packageName: String): Completable {
    return Completable.fromAction {
      AptoideUtils.SystemU.openApp(packageName, packageManager, fragment.context)
    }
  }
}