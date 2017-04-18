package cm.aptoide.pt.v8engine.view.account;

import rx.Observable;

public interface LoginBottomSheet {

  void expand();

  void collapse();

  Observable<State> state();

  enum State {
    EXPANDED, COLLAPSED;
  }
}
