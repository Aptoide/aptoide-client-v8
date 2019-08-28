package cm.aptoide.pt.view.rx;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
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
