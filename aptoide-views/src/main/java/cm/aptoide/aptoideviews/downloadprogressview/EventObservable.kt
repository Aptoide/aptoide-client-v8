package cm.aptoide.aptoideviews.downloadprogressview

import rx.Observable
import rx.Subscriber
import rx.android.MainThreadSubscription
import rx.android.MainThreadSubscription.verifyMainThread

/**
 * Rx Binding for DownloadProgressView
 * This follows the same implementation used in RxBinding@1.0.0
 *
 * Note: Newer RxBinding versions use a different method that should be replicated
 * if we are to bump our version.
 */
class DownloadProgressViewEventOnSubscribe(val view: DownloadProgressView) :
    Observable.OnSubscribe<DownloadEventListener.Action> {

  override fun call(subscriber: Subscriber<in DownloadEventListener.Action>) {
    verifyMainThread()

    val eventListener = object : DownloadEventListener {
      override fun onActionClick(action: DownloadEventListener.Action) {
        if (!subscriber.isUnsubscribed) {
          subscriber.onNext(action)
        }
      }
    }

    subscriber.add(
        object : MainThreadSubscription() {
          override fun onUnsubscribe() {
            view.setEventListener(null)
          }
        }
    )

    view.setEventListener(eventListener)

  }
}
