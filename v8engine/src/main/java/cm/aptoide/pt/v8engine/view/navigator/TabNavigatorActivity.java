package cm.aptoide.pt.v8engine.view.navigator;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.view.account.LoginBottomSheetActivity;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

public abstract class TabNavigatorActivity extends LoginBottomSheetActivity
    implements TabNavigator {

  private PublishRelay<TabNavigation> navigatorSubject;
  private TabNavigation cache;

  @Override protected void onCreate(Bundle savedInstanceState) {
    navigatorSubject = PublishRelay.create();
    super.onCreate(savedInstanceState);
  }

  @Override public void navigate(TabNavigation tabNavigation) {
    cache = tabNavigation;
    navigatorSubject.call(tabNavigation);
  }

  @Override public Observable<TabNavigation> navigation() {
    return navigatorSubject.startWith(Observable.defer(() -> {
      if (cache != null) {
        return Observable.just(cache);
      }
      return Observable.empty();
    }));
  }

  @Override public void clearNavigation() {
    cache = null;
  }
}
