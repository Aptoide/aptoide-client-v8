package cm.aptoide.pt.download.view.outofspace

import cm.aptoide.pt.presenter.View
import rx.Observable

interface OutOfSpaceDialogView : View {
  fun setupViews()
  fun showInstalledApps(installedApps: List<InstalledApp>)
  fun uninstallClick(): Observable<String>

}
