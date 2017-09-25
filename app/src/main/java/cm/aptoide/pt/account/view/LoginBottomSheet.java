package cm.aptoide.pt.account.view;

import rx.Observable;

public interface LoginBottomSheet {

  void expand();

  void collapse();

  Observable<State> state();

  enum State {
    EXPANDED, COLLAPSED;
  }
}
