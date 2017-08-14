/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/08/2016.
 */

package cm.aptoide.pt.v8engine.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.ActivityEvent;
import rx.Observable;

public abstract class ActivityView extends AnalyticsActivity implements View {

  private Presenter presenter;

  @Override protected void onDestroy() {
    super.onDestroy();
    presenter = null;
  }

  @NonNull @Override
  public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull LifecycleEvent lifecycleEvent) {
    return RxLifecycle.bindUntilEvent(getLifecycle(), lifecycleEvent);
  }

  @Override public Observable<LifecycleEvent> getLifecycle() {
    return lifecycle().map(event -> convertToEvent(event));
  }

  @Override public void attachPresenter(Presenter presenter, Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      presenter.restoreState(savedInstanceState);
    }
    this.presenter = presenter;
    this.presenter.present();
  }

  @NonNull private LifecycleEvent convertToEvent(ActivityEvent event) {
    switch (event) {
      case CREATE:
        return LifecycleEvent.CREATE;
      case START:
        return LifecycleEvent.START;
      case RESUME:
        return LifecycleEvent.RESUME;
      case PAUSE:
        return LifecycleEvent.PAUSE;
      case STOP:
        return LifecycleEvent.STOP;
      case DESTROY:
        return LifecycleEvent.DESTROY;
      default:
        throw new IllegalStateException("Unrecognized event: " + event.name());
    }
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    if (presenter != null) {
      presenter.saveState(outState);
    } else {
      Logger.w(this.getClass()
          .getName(), "No presenter was attached.");
    }

    super.onSaveInstanceState(outState);
  }
}
