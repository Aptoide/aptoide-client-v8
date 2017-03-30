package cm.aptoide.pt.v8engine.view.binding;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import rx.Observable;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;

/**
 * Created by marcelobenites on 09/03/17.
 */

public class RxSwitch {

  private RxSwitch() {
    throw new AssertionError("No instances.");
  }

  @CheckResult @NonNull public static Observable<Boolean> checks(SwitchCompat switchCompat) {
    checkNotNull(switchCompat, "switchCompat == null");
    return Observable.create(new SwitchOnCheckOnSubscribe(switchCompat));
  }
}
