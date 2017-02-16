package cm.aptoide.pt.v8engine.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.navigation.NavigationManagerV4;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxFragment;
import rx.Observable;

public abstract class FragmentView extends RxFragment implements cm.aptoide.pt.v8engine.view.View {

  private Presenter presenter;

  private NavigationManagerV4 navigator;

  public NavigationManagerV4 getNavigationManager() {
    return navigator;
  }

  @CallSuper @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    navigator = NavigationManagerV4.Builder.buildWith(getActivity());
    return super.onCreateView(inflater, container, savedInstanceState);
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
