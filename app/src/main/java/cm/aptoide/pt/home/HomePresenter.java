package cm.aptoide.pt.home;

import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Scheduler;
import rx.exceptions.OnErrorNotImplementedException;

/**
 * Created by jdandrade on 07/03/2018.
 */

public class HomePresenter implements Presenter {

  private final HomeView view;
  private final Home home;
  private final Scheduler viewScheduler;

  public HomePresenter(HomeView view, Home home, Scheduler viewScheduler) {
    this.view = view;
    this.home = home;
    this.viewScheduler = viewScheduler;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> view.showLoading())
        .flatMapSingle(created -> home.getHomeBundles())
        .observeOn(viewScheduler)
        .doOnNext(view::showHomeBundles)
        .doOnNext(bundles -> view.hideLoading())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }
}
