package cm.aptoide.pt.store.view.my;

import cm.aptoide.pt.home.AptoideBottomNavigator;
import cm.aptoide.pt.presenter.Presenter;
import rx.exceptions.OnErrorNotImplementedException;

/**
 * Created by D01 on 14/03/18.
 */

public class MyStoresPresenter implements Presenter {

  private MyStoresView view;
  private AptoideBottomNavigator aptoideBottomNavigator;

  public MyStoresPresenter(MyStoresView view, AptoideBottomNavigator aptoideBottomNavigator) {
    this.view = view;
    this.aptoideBottomNavigator = aptoideBottomNavigator;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(
            cm.aptoide.pt.presenter.View.LifecycleEvent.CREATE))
        .flatMap(created -> aptoideBottomNavigator.navigationEvent())
        .doOnNext(navigated -> view.scrollToTop())
        .compose(view.bindUntilEvent(cm.aptoide.pt.presenter.View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }
}
