package cm.aptoide.pt.v8engine.view.navigator;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.view.account.LoginBottomSheetActivity;
import com.jakewharton.rxrelay.BehaviorRelay;
import rx.Observable;

public class TabNavigatorActivity extends LoginBottomSheetActivity implements TabNavigator {

  private BehaviorRelay<TabNavigation> navigatorSubject;

  @Override protected void onCreate(Bundle savedInstanceState) {
    navigatorSubject = BehaviorRelay.create();
    super.onCreate(savedInstanceState);
  }

  @Override public void navigate(TabNavigation tabNavigation) {
    navigatorSubject.call(tabNavigation);
  }

  @Override public Observable<TabNavigation> navigation() {
    return navigatorSubject;
  }
}
