package cm.aptoide.pt.view.account;

import rx.Observable;

public interface LoginBottomSheet {

  void expand();

  void collapse();

  Observable<State> state();

  enum State {
    EXPANDED, COLLAPSED;
  }
}
