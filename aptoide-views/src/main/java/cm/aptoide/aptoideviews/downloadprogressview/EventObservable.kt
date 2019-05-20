package cm.aptoide.aptoideviews.downloadprogressview

import android.support.annotation.CheckResult
import rx.Observable
import rx.Subscriber
import rx.android.MainThreadSubscription
import rx.android.MainThreadSubscription.verifyMainThread

@CheckResult
fun DownloadProgressView.events(): Observable<EventListener.Action> {
  return Observable.create(DownloadProgressViewEventOnSubscribe(this))
}

/**
 * Rx Binding for DownloadProgressView
 * This follows the same implementation used in RxBinding@1.0.0
 *
 * Note: Newer RxBinding versions use a different method that should be implemented
 * if we are to bump our version.
 */
class DownloadProgressViewEventOnSubscribe(val view: DownloadProgressView) :
    Observable.OnSubscribe<EventListener.Action> {

  override fun call(subscriber: Subscriber<in EventListener.Action>) {
    verifyMainThread()

    view.setEventListener(
        object : EventListener {
          override fun onActionClick(action: EventListener.Action) {
            if (!subscriber.isUnsubscribed) {
              subscriber.onNext(action)
            }
          }
        }
    )

    subscriber.add(
        object : MainThreadSubscription() {
          override fun onUnsubscribe() {
            view.setEventListener(null)
          }
        }
    )
  }
}
