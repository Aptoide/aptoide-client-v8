package cm.aptoide.pt.themes

import cm.aptoide.pt.presenter.View
import rx.Observable

interface NewFeatureDialogView : View {
  fun clickDismiss(): Observable<Void>
  fun clickTurnItOn(): Observable<Void>
  fun dismissView()
}