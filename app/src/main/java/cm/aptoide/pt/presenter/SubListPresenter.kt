package cm.aptoide.pt.presenter

import rx.Observable

abstract class SubListPresenter<T> {

  protected lateinit var eventObservable: Observable<T>
  protected lateinit var lifecycleView: View // This could be replaced by CompositeDisposable in Rx2

  fun present(eventObservable: Observable<T>, lifecycleView: View) {
    this.eventObservable = eventObservable
    this.lifecycleView = lifecycleView
    present()
  }

  protected abstract fun present()
}