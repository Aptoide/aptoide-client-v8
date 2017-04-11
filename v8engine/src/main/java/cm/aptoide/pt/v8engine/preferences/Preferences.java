package cm.aptoide.pt.v8engine.preferences;

import android.content.SharedPreferences;
import rx.Completable;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

public class Preferences {

  private final SharedPreferences preferences;

  public Preferences(SharedPreferences preferences) {
    this.preferences = preferences;
  }

  public Completable save(String key, boolean value) {
    return Completable.fromAction(() -> preferences.edit().putBoolean(key, value).commit());
  }

  public Observable<Boolean> getBoolean(String key, boolean defaultValue) {
    return change(key).map(changed -> preferences.getBoolean(key, defaultValue))
        .startWith(preferences.getBoolean(key, defaultValue));
  }

  public Completable save(String key, String value) {
    return Completable.fromAction(() -> preferences.edit().putString(key, value).commit());
  }

  public Observable<String> getString(String key, String defaultValue) {
    return change(key).map(changed -> preferences.getString(key, defaultValue))
        .startWith(preferences.getString(key, defaultValue));
  }

  public Completable save(String key, int value) {
    return Completable.fromAction(() -> preferences.edit().putInt(key, value).commit());
  }

  public Observable<Integer> getInt(String key, int defaultValue) {
    return change(key).map(changed -> preferences.getInt(key, defaultValue))
        .startWith(preferences.getInt(key, defaultValue));
  }

  private Observable<Void> change(String key) {
    return Observable.create(new Observable.OnSubscribe<Void>() {
      @Override public void call(Subscriber<? super Void> subscriber) {

        final SharedPreferences.OnSharedPreferenceChangeListener listener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
              @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                  String changedKey) {
                if (!subscriber.isUnsubscribed() && changedKey.equals(key)) {
                  subscriber.onNext(null);
                }
              }
            };

        subscriber.add(Subscriptions.create(
            () -> preferences.unregisterOnSharedPreferenceChangeListener(listener)));

        preferences.registerOnSharedPreferenceChangeListener(listener);
      }
    });
  }

  public Completable remove(String key) {
    return Completable.fromAction(() -> preferences.edit().remove(key).commit());
  }
}
