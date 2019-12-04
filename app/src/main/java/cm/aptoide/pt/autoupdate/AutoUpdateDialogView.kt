package cm.aptoide.pt.autoupdate

import cm.aptoide.pt.presenter.View
import rx.Observable

interface AutoUpdateDialogView : View {

  fun updateClicked(): Observable<Void>

  fun notNowClicked(): Observable<Void>

  fun dismissDialog()
}