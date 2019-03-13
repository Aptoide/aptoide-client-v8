package cm.aptoide.pt.view.splashscreen;

import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.splashscreen.SplashScreenNavigator;
import java.util.concurrent.TimeUnit;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorNotImplementedException;

public class SplashScreenPresenter implements Presenter {

  private SplashScreenNavigator splashScreenNavigator;
  private SplashScreenView view;

  public SplashScreenPresenter(SplashScreenView splashScreenView,
      SplashScreenNavigator splashScreenNavigator) {
    this.view = splashScreenView;
    this.splashScreenNavigator = splashScreenNavigator;
  }

  @Override public void present() {
    handleLoad();
  }

  private void handleLoad() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .delay(3000, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> splashScreenNavigator.navigateToHome())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }
}
