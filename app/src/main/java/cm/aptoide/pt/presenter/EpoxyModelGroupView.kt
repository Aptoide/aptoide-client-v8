package cm.aptoide.pt.presenter

import androidx.annotation.LayoutRes
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelGroup
import com.airbnb.epoxy.ModelGroupHolder
import com.trello.rxlifecycle.LifecycleTransformer
import com.trello.rxlifecycle.RxLifecycle
import rx.Observable
import rx.subjects.BehaviorSubject

abstract class EpoxyModelGroupView(@LayoutRes layoutRes: Int, models: List<EpoxyModel<*>>) :
    EpoxyModelGroup(layoutRes, models), EpoxyView {
  private val lifecycleSubject = BehaviorSubject.create<EpoxyView.LifecycleEvent>()

  override fun getLifecycleEvent(): Observable<EpoxyView.LifecycleEvent> {
    return lifecycleSubject.asObservable()
  }

  override fun bind(holder: ModelGroupHolder) {
    lifecycleSubject.onNext(EpoxyView.LifecycleEvent.BIND)
    super.bind(holder)
  }

  override fun unbind(holder: ModelGroupHolder) {
    lifecycleSubject.onNext(EpoxyView.LifecycleEvent.UNBIND)
    super.unbind(holder)
  }

  override fun onViewAttachedToWindow(holder: ModelGroupHolder) {
    super.onViewAttachedToWindow(holder)
    lifecycleSubject.onNext(EpoxyView.LifecycleEvent.ATTACH)

  }

  override fun onViewDetachedFromWindow(holder: ModelGroupHolder) {
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