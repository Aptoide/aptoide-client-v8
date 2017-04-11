package cm.aptoide.pt.v8engine.view.rx;

import android.support.v7.preference.Preference;
import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

import static rx.android.MainThreadSubscription.verifyMainThread;

/**
 * Created by marcelobenites on 07/03/17.
 */

class PreferenceClickOnSubscribe implements Observable.OnSubscribe<Preference> {

  private final Preference preference;

  public PreferenceClickOnSubscribe(Preference preference) {
    this.preference = preference;
  }

  @Override public void call(Subscriber<? super Preference> subscriber) {
    verifyMainThread();

    Preference.OnPreferenceClickListener listener = new Preference.OnPreferenceClickListener() {
      @Override public boolean onPreferenceClick(Preference preference) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(preference);
          return true;
        }
        return true;
      }
    };

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        preference.setOnPreferenceClickListener(null);
      }
    });

    preference.setOnPreferenceClickListener(listener);
  }
}
