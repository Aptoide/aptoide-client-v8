package cm.aptoide.pt.promotions;

import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.exceptions.OnErrorNotImplementedException;

public class PromotionsPresenter implements Presenter {

  private PromotionsView view;
  private PromotionsManager promotionsManager;

  public PromotionsPresenter(PromotionsView view, PromotionsManager promotionsManager) {
    this.view = view;
    this.promotionsManager = promotionsManager;
  }

  @Override public void present() {
    getPromotionApps();
  }

  private void getPromotionApps() {

    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> promotionsManager.getPromotionApps())
        .doOnNext(appsList -> view.showPromotionApps(appsList))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }
}
