package cm.aptoide.pt.v8engine.view.fragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import cm.aptoide.pt.v8engine.view.ActivityView;
import cm.aptoide.pt.v8engine.view.MainActivity;
import cm.aptoide.pt.v8engine.view.leak.LeakFragment;
import cm.aptoide.pt.v8engine.view.navigator.ActivityNavigator;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.FragmentEvent;
import rx.Observable;

public abstract class FragmentView extends LeakFragment implements View {

  private Presenter presenter;
  private FragmentNavigator fragmentNavigator;
  private ActivityNavigator activityNavigator;

  public FragmentNavigator getFragmentNavigator() {
    return fragmentNavigator;
  }

  public ActivityNavigator getActivityNavigator() {
    return activityNavigator;
  }

  public FragmentNavigator getFragmentChildNavigator(@IdRes int containerId) {
    return new FragmentNavigator(getChildFragmentManager(), containerId, android.R.anim.fade_in,
        android.R.anim.fade_out);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    fragmentNavigator = ((ActivityView) getActivity()).getFragmentNavigator();
    activityNavigator = ((ActivityView) getActivity()).getActivityNavigator();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    if (presenter != null) {
      presenter.saveState(outState);
    } else {
      Logger.w(this.getClass()
          .getName(), "No presenter was attached.");
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
      fragmentNavigator.popBackStack();
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
}
