package cm.aptoide.pt.home;

import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.exceptions.OnErrorNotImplementedException;

/**
 * Created by jdandrade on 07/03/2018.
 */

public class HomePresenter implements Presenter {

  private final HomeView view;
  private final Home home;
  private final AptoideBottomNavigator aptoideBottomNavigator;

  public HomePresenter(HomeView view, Home home, AptoideBottomNavigator aptoideBottomNavigator) {
    this.view = view;
    this.home = home;
    this.aptoideBottomNavigator = aptoideBottomNavigator;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(created -> home.getHomeBundles())
        .doOnNext(view::showHomeBundles)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> aptoideBottomNavigator.navigationEvent())
        .doOnNext(navigated -> view.scrollToTop())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }
}
