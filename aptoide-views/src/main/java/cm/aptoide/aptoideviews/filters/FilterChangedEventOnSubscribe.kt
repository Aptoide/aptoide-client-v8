package cm.aptoide.aptoideviews.filters

import rx.Observable
import rx.Subscriber
import rx.android.MainThreadSubscription

class FilterChangedEventOnSubscribe(val view: FiltersView) :
    Observable.OnSubscribe<List<Filter>> {

  override fun call(subscriber: Subscriber<in List<Filter>>) {
    MainThreadSubscription.verifyMainThread()

    val eventListener = object : FiltersChangedEventListener {

      override fun onFiltersChanged(filters: List<Filter>) {
        if (!subscriber.isUnsubscribed) {
          subscriber.onNext(filters)
        }
      }
    }

    subscriber.add(
        object : MainThreadSubscription() {
          override fun onUnsubscribe() {
            view.setFiltersChangedEventsListener(null)
          }
        }
    )

    view.setFiltersChangedEventsListener(eventListener)
  }
}