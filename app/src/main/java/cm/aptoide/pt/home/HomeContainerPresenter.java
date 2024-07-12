package cm.aptoide.pt.home;

import androidx.annotation.VisibleForTesting;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Observable;
import rx.Scheduler;
import rx.exceptions.OnErrorNotImplementedException;

public class HomeContainerPresenter implements Presenter {

  private final HomeContainerView view;
  private final Scheduler viewScheduler;
  private final AptoideAccountManager accountManager;
  private final HomeContainerNavigator homeContainerNavigator;
  private final HomeNavigator homeNavigator;
  private final HomeAnalytics homeAnalytics;
  private final Home home;
  private final ChipManager chipManager;

  public HomeContainerPresenter(HomeContainerView view, Scheduler viewScheduler,
      AptoideAccountManager accountManager, HomeContainerNavigator homeContainerNavigator,
      HomeNavigator homeNavigator, HomeAnalytics homeAnalytics, Home home, ChipManager chipManager
  ) {
    this.view = view;
    this.viewScheduler = viewScheduler;
    this.accountManager = accountManager;
    this.homeContainerNavigator = homeContainerNavigator;
    this.homeNavigator = homeNavigator;
    this.homeAnalytics = homeAnalytics;
    this.home = home;
    this.chipManager = chipManager;
  }

  @Override public void present() {
    loadMainHomeContent();
    loadUserImage();
    handleUserImageClick();
    handlePromotionsClick();
    checkForPromotionApps();
    handleClickOnPromotionsDialogContinue();
    handleClickOnPromotionsDialogCancel();
    handleLoggedInAcceptTermsAndConditions();
    handleTermsAndConditionsContinueClicked();
    handleTermsAndConditionsLogOutClicked();
    handleClickOnTermsAndConditions();
    handleClickOnPrivacyPolicy();
    handleClickOnGamesChip();
    handleClickOnAppsChip();
    handleBottomNavigationEvents();
  }

  @VisibleForTesting public void loadMainHomeContent() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.isChipChecked())
        .doOnNext(checked -> {
          switch (checked) {
            case GAMES:
              homeContainerNavigator.loadGamesHomeContent();
              break;
            case APPS:
              homeContainerNavigator.loadAppsHomeContent();
              break;
            default:
              homeContainerNavigator.loadMainHomeContent();
              break;
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          throw new OnErrorNotImplementedException(err);
        });
  }

  private void handleBottomNavigationEvents() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> homeNavigator.bottomNavigation())
        .observeOn(viewScheduler)
        .flatMap(__ -> homeContainerNavigator.navigateHome())
        .doOnNext(shouldGoHome -> {
          view.expandChips();
          if (shouldGoHome) {
            homeContainerNavigator.loadMainHomeContent();
            chipManager.setCurrentChip(null);
            view.uncheckChips();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          throw new OnErrorNotImplementedException(err);
        });
  }

  @VisibleForTesting public void loadUserImage() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> accountManager.accountStatus())
        .flatMap(account -> getUserAvatar(account))
        .observeOn(viewScheduler)
        .doOnNext(userAvatarUrl -> {
          if (userAvatarUrl != null) {
            view.setUserImage(userAvatarUrl);
          } else {
            view.setDefaultUserImage();
          }
          view.showAvatar();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void handleUserImageClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.toolbarUserClick()
            .observeOn(viewScheduler)
            .doOnNext(account -> homeNavigator.navigateToMyAccount())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void handlePromotionsClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.toolbarPromotionsClick()
            .observeOn(viewScheduler)
            .doOnNext(account -> {
              homeAnalytics.sendPromotionsIconClickEvent();
              homeNavigator.navigateToPromotions();
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void checkForPromotionApps() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(__ -> home.hasPromotionApps())
        .filter(HomePromotionsWrapper::hasPromotions)
        .observeOn(viewScheduler)
        .doOnNext(apps -> {
          view.showPromotionsHomeIcon(apps);
          homeAnalytics.sendPromotionsImpressionEvent();
        })
        .filter(HomePromotionsWrapper::shouldShowDialog)
        .doOnNext(apps -> {
          homeAnalytics.sendPromotionsDialogImpressionEvent();
          home.setPromotionsDialogShown();
          view.showPromotionsHomeDialog(apps);
        })
        .doOnError(Throwable::printStackTrace)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> view.hidePromotionsIcon());
  }

  @VisibleForTesting public void handleClickOnPromotionsDialogContinue() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.promotionsHomeDialogClicked())
        .filter(action -> action.equals("navigate"))
        .doOnNext(__ -> {
          homeAnalytics.sendPromotionsDialogNavigateEvent();
          view.dismissPromotionsDialog();
          homeNavigator.navigateToPromotions();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void handleClickOnPromotionsDialogCancel() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.promotionsHomeDialogClicked())
        .filter(action -> action.equals("cancel"))
        .doOnNext(__ -> {
          homeAnalytics.sendPromotionsDialogDismissEvent();
          view.dismissPromotionsDialog();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void handleLoggedInAcceptTermsAndConditions() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> accountManager.accountStatus()
            .first())
        .filter(Account::isLoggedIn)
        .filter(
            account -> !(account.acceptedPrivacyPolicy() && account.acceptedTermsAndConditions()))
        .observeOn(viewScheduler)
        .doOnNext(__ -> view.showTermsAndConditionsDialog())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void handleTermsAndConditionsContinueClicked() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.gdprDialogClicked())
        .filter(action -> action.equals("continue"))
        .flatMapCompletable(__ -> accountManager.updateTermsAndConditions())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void handleTermsAndConditionsLogOutClicked() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.gdprDialogClicked())
        .filter(action -> action.equals("logout"))
        .flatMapCompletable(__ -> accountManager.logout())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @VisibleForTesting public void handleClickOnTermsAndConditions() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.gdprDialogClicked())
        .filter(action -> action.equals("terms"))
        .doOnNext(__ -> homeNavigator.navigateToTermsAndConditions())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          throw new OnErrorNotImplementedException(err);
        });
  }

  @VisibleForTesting public void handleClickOnPrivacyPolicy() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.gdprDialogClicked())
        .filter(action -> action.equals("privacy"))
        .doOnNext(__ -> homeNavigator.navigateToPrivacyPolicy())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          throw new OnErrorNotImplementedException(err);
        });
  }

  @VisibleForTesting public void handleClickOnGamesChip() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.gamesChipClicked())
        .doOnNext(isChecked -> {
          if (isChecked) {
            homeContainerNavigator.loadGamesHomeContent();
            chipManager.setCurrentChip(ChipManager.Chip.GAMES);
          } else {
            homeContainerNavigator.loadMainHomeContent();
            chipManager.setCurrentChip(null);
          }
          homeAnalytics.sendChipInteractEvent(ChipManager.Chip.GAMES.getName());
          homeAnalytics.sendChipHomeInteractEvent(ChipManager.Chip.GAMES.getName());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          throw new OnErrorNotImplementedException(err);
        });
  }

  @VisibleForTesting public void handleClickOnAppsChip() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.appsChipClicked())
        .doOnNext(isChecked -> {
          if (isChecked) {
            homeContainerNavigator.loadAppsHomeContent();
            chipManager.setCurrentChip(ChipManager.Chip.APPS);
          } else {
            homeContainerNavigator.loadMainHomeContent();
            chipManager.setCurrentChip(null);
          }
          homeAnalytics.sendChipInteractEvent(ChipManager.Chip.APPS.getName());
          homeAnalytics.sendChipHomeInteractEvent(ChipManager.Chip.APPS.getName());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          throw new OnErrorNotImplementedException(err);
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
