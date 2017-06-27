package cm.aptoide.pt.spotandshareapp.view;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import cm.aptoide.pt.spotandshareapp.presenter.Presenter;
import com.trello.rxlifecycle.LifecycleTransformer;
import rx.Observable;

/**
 * Created by filipe on 07-06-2017.
 */

public interface View {

  @NonNull @CheckResult <T> LifecycleTransformer<T> bindUntilEvent(
      @NonNull LifecycleEvent lifecycleEvent);

  Observable<LifecycleEvent> getLifecycle();

  void attachPresenter(Presenter presenter, Bundle savedInstanceState);

  enum LifecycleEvent {
    CREATE, START, RESUME, PAUSE, STOP, DESTROY,
  }
}
