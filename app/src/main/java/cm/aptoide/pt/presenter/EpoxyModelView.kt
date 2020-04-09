package cm.aptoide.pt.presenter

import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.trello.rxlifecycle.LifecycleTransformer
import com.trello.rxlifecycle.RxLifecycle
import rx.Observable
import rx.subjects.BehaviorSubject

abstract class EpoxyModelView<T : EpoxyHolder> : EpoxyModelWithHolder<T>(), EpoxyView {

  private val lifecycleSubject = BehaviorSubject.create<EpoxyView.LifecycleEvent>()

  override fun getLifecycleEvent(): Observable<EpoxyView.LifecycleEvent> {
    return lifecycleSubject.asObservable()
  }

  override fun bind(holder: T) {
    super.bind(holder)
    lifecycleSubject.onNext(EpoxyView.LifecycleEvent.BIND)
  }

  override fun unbind(holder: T) {
    lifecycleSubject.onNext(EpoxyView.LifecycleEvent.UNBIND)
    super.unbind(holder)
  }

  override fun onViewAttachedToWindow(holder: T) {
    super.onViewAttachedToWindow(holder)
    lifecycleSubject.onNext(EpoxyView.LifecycleEvent.ATTACH)
  }

  override fun onViewDetachedFromWindow(holder: T) {
    lifecycleSubject.onNext(EpoxyView.LifecycleEvent.DETACH)
    super.onViewDetachedFromWindow(holder)
  }

  override fun <T> bindUntilEvent(
      lifecycleEvent: EpoxyView.LifecycleEvent): LifecycleTransformer<T> {
    return RxLifecycle.bindUntilEvent(getLifecycleEvent(), lifecycleEvent)
  }

  override fun <T : EpoxyModelView<*>> attachPresenter(presenter: EpoxyModelPresenter<T>, view: T) {
    presenter.present(view)
  }
}