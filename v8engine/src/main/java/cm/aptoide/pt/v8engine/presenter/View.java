/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 19/08/2016.
 */

package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import com.trello.rxlifecycle.LifecycleTransformer;
import rx.Observable;

/**
 * Created by marcelobenites on 8/19/16.
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
