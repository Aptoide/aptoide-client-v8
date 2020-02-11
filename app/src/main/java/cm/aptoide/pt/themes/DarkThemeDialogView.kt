package cm.aptoide.pt.themes

import cm.aptoide.pt.presenter.View
import rx.Observable

interface DarkThemeDialogView : View {
  fun clickDismiss(): Observable<Void>
  fun clickTurnItOn(): Observable<Void>
  fun dismissView()
}