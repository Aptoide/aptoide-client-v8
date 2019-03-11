package cm.aptoide.pt.view.splashscreen;

import cm.aptoide.pt.home.BottomNavigationNavigator;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import java.util.concurrent.TimeUnit;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorNotImplementedException;

public class SplashScreenPresenter implements Presenter {

  private BottomNavigationNavigator bottomNavigationNavigator;
  private SplashScreenView view;

  public SplashScreenPresenter(SplashScreenView splashScreenView,
      BottomNavigationNavigator bottomNavigationNavigator) {
    this.view = splashScreenView;
    this.bottomNavigationNavigator = bottomNavigationNavigator;
  }

  @Override public void present() {
    handleLoad();
  }

  private void handleLoad() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .delay(3000, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> bottomNavigationNavigator.navigateToHome())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }
}
