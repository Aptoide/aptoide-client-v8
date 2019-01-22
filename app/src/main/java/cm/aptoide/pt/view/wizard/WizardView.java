package cm.aptoide.pt.view.wizard;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.presenter.View;
import rx.Completable;
import rx.Observable;

public interface WizardView extends View {
  Completable createWizardAdapter(Account account);

  Observable<Void> skipWizardClick();

  void skipWizard();

  void handleSelectedPage(int selectedPage);

  void handleColorTransitions(int position, float positionOffset, int positionOffsetPixels);

  int getCount();
}
