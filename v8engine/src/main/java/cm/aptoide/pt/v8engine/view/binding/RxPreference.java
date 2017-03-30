package cm.aptoide.pt.v8engine.view.binding;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import rx.Observable;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;

/**
 * Created by marcelobenites on 07/03/17.
 */

public class RxPreference {

  private RxPreference() {
    throw new AssertionError("No instances.");
  }

  @CheckResult @NonNull public static Observable<Preference> clicks(Preference preference) {
    checkNotNull(preference, "preference == null");
    return Observable.create(new PreferenceClickOnSubscribe(preference));
  }

  @CheckResult @NonNull public static Observable<Boolean> checks(CheckBoxPreference preference) {
    checkNotNull(preference, "preference == null");
    return Observable.create(new PreferenceOnCheckOnSubscribe(preference));
  }
}
