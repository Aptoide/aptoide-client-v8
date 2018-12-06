package cm.aptoide.pt.promotions;

import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Scheduler;
import rx.exceptions.OnErrorNotImplementedException;

public class PromotionsPresenter implements Presenter {

  private PromotionsView view;
  private PromotionsManager promotionsManager;
  private Scheduler viewScheduler;

  public PromotionsPresenter(PromotionsView view, PromotionsManager promotionsManager,
      Scheduler viewScheduler) {
    this.view = view;
    this.promotionsManager = promotionsManager;
    this.viewScheduler = viewScheduler;
  }

  @Override public void present() {
    getPromotionApps();
  }

  private void getPromotionApps() {

    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> promotionsManager.getPromotionApps())
        .flatMapIterable(promotionsList -> promotionsList)
        .flatMap(promotionViewApp -> promotionsManager.getDownload(promotionViewApp))
        .observeOn(viewScheduler)
        .doOnNext(promotionViewApp -> view.showPromotionApp(promotionViewApp))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }
}
