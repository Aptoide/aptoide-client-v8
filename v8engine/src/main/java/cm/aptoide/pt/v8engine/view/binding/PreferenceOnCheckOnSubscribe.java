package cm.aptoide.pt.v8engine.view.binding;

import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

import static rx.android.MainThreadSubscription.verifyMainThread;

/**
 * Created by marcelobenites on 09/03/17.
 */

class PreferenceOnCheckOnSubscribe implements Observable.OnSubscribe<Boolean> {

  private final CheckBoxPreference preference;

  public PreferenceOnCheckOnSubscribe(CheckBoxPreference preference) {
    this.preference = preference;
  }

  @Override public void call(Subscriber<? super Boolean> subscriber) {
    verifyMainThread();

    Preference.OnPreferenceClickListener listener = new Preference.OnPreferenceClickListener() {
      @Override public boolean onPreferenceClick(Preference preference) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(((CheckBoxPreference) preference).isChecked());
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
