package cm.aptoide.pt.v8engine.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxFragment;
import rx.Observable;

public abstract class FragmentView extends RxFragment implements cm.aptoide.pt.v8engine.view.View {

  private Presenter presenter;

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

  @NonNull private LifecycleEvent convertToEvent(FragmentEvent event) {
    switch (event) {
      case CREATE:
        return LifecycleEvent.CREATE;
      case CREATE_VIEW:
        return LifecycleEvent.CREATE_VIEW;
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
      case DESTROY_VIEW:
        return LifecycleEvent.DESTROY_VIEW;
      default:
        throw new IllegalStateException("Unrecognized event: " + event.name());
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    if (presenter != null) {
      presenter.saveState(outState);
    } else {
      Log.w(this.getClass().getName(), "No presenter was attached.");
    }

    super.onSaveInstanceState(outState);
  }
}
