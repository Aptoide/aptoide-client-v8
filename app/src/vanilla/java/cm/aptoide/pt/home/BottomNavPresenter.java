package cm.aptoide.pt.home;

import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.exceptions.OnErrorNotImplementedException;

/**
 * Created by jdandrade on 05/03/2018.
 */

public class BottomNavPresenter implements Presenter {

  private final AptoideBottomNavigationView view;

  public BottomNavPresenter(AptoideBottomNavigationView bottomNavigationFragmentView) {
    this.view = bottomNavigationFragmentView;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.navigationEvent())
        .subscribe(menuItemId -> view.showFragment(menuItemId), throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }
}
