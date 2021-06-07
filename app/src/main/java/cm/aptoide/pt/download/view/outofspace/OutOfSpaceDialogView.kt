package cm.aptoide.pt.download.view.outofspace

import cm.aptoide.pt.presenter.View
import rx.Observable

interface OutOfSpaceDialogView : View {
  fun setupViews(requiredSpace: Long?)
  fun showInstalledApps(installedApps: List<InstalledApp>)
  fun uninstallClick(): Observable<String>
  fun dismiss()
  fun dismissDialogClick(): Observable<Void>
  fun requiredSpaceToInstall(removedAppsize: Long)
  fun showGeneralOutOfSpaceError()

}
