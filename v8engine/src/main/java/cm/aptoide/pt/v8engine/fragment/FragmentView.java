package cm.aptoide.pt.v8engine.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.navigation.NavigationManagerV4;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.view.MainActivity;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.FragmentEvent;
import rx.Observable;

public abstract class FragmentView extends LeakFragment
    implements cm.aptoide.pt.v8engine.view.View {

  private Presenter presenter;

  private NavigationManagerV4 navigator;

  /**
   * The navigator is only available after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
   *
   * @return Navigation manager to move between fragments.
   */
  public NavigationManagerV4 getNavigationManager() {
    return navigator;
  }

  // TODO: move navigation manager creation to here and fix all the extending classes
  //
  //@Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
  //  navigator = NavigationManagerV4.Builder.buildWith(getActivity());
  //  super.onActivityCreated(savedInstanceState);
  //}

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    navigator = NavigationManagerV4.Builder.buildWith(getActivity());
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    if (presenter != null) {
      presenter.saveState(outState);
    } else {
      Log.w(this.getClass().getName(), "No presenter was attached.");
    }

    super.onSaveInstanceState(outState);
  }

  /**
   * Do not override this method in fragments to handle back stack navigation, before deciding if
   * toolbar menu items should be handled in activity or the fragment.
   *
   * The back navigation menu item selection handling is currently done in the {@link
   * MainActivity}.
   */
  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @NonNull @Override
  public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull LifecycleEvent lifecycleEvent) {
    return RxLifecycle.bindUntilEvent(getLifecycle(), lifecycleEvent);
  }

  @Override public Observable<LifecycleEvent> getLifecycle() {
    return lifecycle().flatMap(event -> convertToEvent(event));
  }

  @Override public void attachPresenter(Presenter presenter, Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      presenter.restoreState(savedInstanceState);
    }
    this.presenter = presenter;
    this.presenter.present();
  }

  @NonNull private Observable<LifecycleEvent> convertToEvent(FragmentEvent event) {
    switch (event) {
      case CREATE:
        return Observable.empty();
      case CREATE_VIEW:
        return Observable.just(LifecycleEvent.CREATE);
      case START:
        return Observable.just(LifecycleEvent.START);
      case RESUME:
        return Observable.just(LifecycleEvent.RESUME);
      case PAUSE:
        return Observable.just(LifecycleEvent.PAUSE);
      case STOP:
        return Observable.just(LifecycleEvent.STOP);
      case DESTROY_VIEW:
        return Observable.just(LifecycleEvent.DESTROY);
      case DESTROY:
        return Observable.empty();
      default:
        throw new IllegalStateException("Unrecognized event: " + event.name());
    }
  }

  public boolean onBackPressed() {
    return false;
  }
}
