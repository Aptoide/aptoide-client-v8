package cm.aptoide.pt.store.view.my;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Observable;
import rx.Scheduler;
import rx.exceptions.OnErrorNotImplementedException;

/**
 * Created by D01 on 14/03/18.
 */

public class MyStoresPresenter implements Presenter {

  private final MyStoresView view;
  private final Scheduler viewScheduler;
  private final AptoideAccountManager accountManager;
  private final MyStoresNavigator myStoresNavigator;

  public MyStoresPresenter(MyStoresView view, Scheduler viewScheduler,
      AptoideAccountManager accountManager, MyStoresNavigator myStoresNavigator) {
    this.view = view;
    this.viewScheduler = viewScheduler;
    this.accountManager = accountManager;
    this.myStoresNavigator = myStoresNavigator;
  }

  @Override public void present() {
    loadUserImage();
    handleBottomNavigationEvent();
    handleUserImageClick();
  }

  private void handleUserImageClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.imageClick()
            .observeOn(viewScheduler)
            .doOnNext(click -> myStoresNavigator.navigateToMyAccount())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void loadUserImage() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> accountManager.accountStatus()
            .first())
        .flatMap(account -> getUserAvatar(account))
        .observeOn(viewScheduler)
        .doOnNext(userAvatarUrl -> {
          if (userAvatarUrl != null) {
            view.setUserImage(userAvatarUrl);
          }
          view.showAvatar();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void handleBottomNavigationEvent() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> myStoresNavigator.bottomNavigationEvent()
            .observeOn(viewScheduler)
            .doOnNext(navigated -> view.scrollToTop())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private Observable<String> getUserAvatar(Account account) {
    String userAvatarUrl = null;
    if (account != null && account.isLoggedIn()) {
      userAvatarUrl = account.getAvatar();
    }
    return Observable.just(userAvatarUrl);
  }
}
