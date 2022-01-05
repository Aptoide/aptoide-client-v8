package cm.aptoide.pt.account.view.magiclink

import cm.aptoide.pt.presenter.View
import rx.Observable

interface CheckYourEmailView : View {

  fun getCheckYourEmailClick(): Observable<Void>
}