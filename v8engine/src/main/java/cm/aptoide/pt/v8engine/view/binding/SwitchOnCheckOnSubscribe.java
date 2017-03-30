package cm.aptoide.pt.v8engine.view.binding;

import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;
import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

import static rx.android.MainThreadSubscription.verifyMainThread;

/**
 * Created by marcelobenites on 09/03/17.
 */

class SwitchOnCheckOnSubscribe implements Observable.OnSubscribe<Boolean> {

  private final SwitchCompat switchCompat;

  public SwitchOnCheckOnSubscribe(SwitchCompat switchCompat) {
    this.switchCompat = switchCompat;
  }

  @Override public void call(Subscriber<? super Boolean> subscriber) {
    verifyMainThread();

    final CompoundButton.OnCheckedChangeListener listener =
        new CompoundButton.OnCheckedChangeListener() {
          @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!subscriber.isUnsubscribed()) {
              subscriber.onNext(isChecked);
            }
          }
        };

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        switchCompat.setOnCheckedChangeListener(null);
      }
    });

    switchCompat.setOnCheckedChangeListener(listener);
  }
}
