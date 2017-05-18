package cm.aptoide.pt.v8engine.view.account;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.v8engine.view.BackButtonActivity;
import com.jakewharton.rxrelay.BehaviorRelay;
import rx.Observable;

public class LoginBottomSheetActivity extends BackButtonActivity implements LoginBottomSheet {

  private BehaviorRelay<State> stateSubject;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    stateSubject = BehaviorRelay.create();
  }

  @Override public void expand() {
    stateSubject.call(State.EXPANDED);
  }

  @Override public void collapse() {
    stateSubject.call(State.COLLAPSED);
  }

  @Override public Observable<State> state() {
    return stateSubject;
  }
}
