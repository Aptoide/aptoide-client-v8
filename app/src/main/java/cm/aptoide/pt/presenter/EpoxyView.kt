package cm.aptoide.pt.presenter

import androidx.annotation.CheckResult
import com.trello.rxlifecycle.LifecycleTransformer
import rx.Observable

interface EpoxyView {
  @CheckResult
  fun <T> bindUntilEvent(
      lifecycleEvent: LifecycleEvent): LifecycleTransformer<T>

  fun getLifecycleEvent(): Observable<LifecycleEvent>

  fun <T : EpoxyModelView<*>> attachPresenter(presenter: EpoxyModelPresenter<T>, view: T)

  enum class LifecycleEvent {
    ATTACH, DETACH, BIND, UNBIND
  }
}