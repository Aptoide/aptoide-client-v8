package cm.aptoide.pt.v8engine.view.wizard;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.Completable;
import rx.Observable;

interface WizardView extends View {
  Completable createWizardAdapter(Account account);

  Observable<Void> goToNextPageClick();

  Observable<Void> skipWizardClick();

  void goToNextPage();

  void skipWizard();

  void handleSelectedPage(int selectedPage);

  int getWizardButtonsCount();

  void showArrow();

  void showSkipButton();
}
