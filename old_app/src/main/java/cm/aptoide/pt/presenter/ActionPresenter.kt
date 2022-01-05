package cm.aptoide.pt.presenter

import rx.Observable

abstract class ActionPresenter<T> {

  protected lateinit var eventObservable: Observable<T>
  protected lateinit var lifecycleView: View

  fun present(eventObservable: Observable<T>, lifecycleView: View) {
    this.eventObservable = eventObservable
    this.lifecycleView = lifecycleView
    present()
  }

  protected abstract fun present()
}