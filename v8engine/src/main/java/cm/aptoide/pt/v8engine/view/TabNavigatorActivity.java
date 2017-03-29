package cm.aptoide.pt.v8engine.view;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.activity.BaseActivity;
import cm.aptoide.pt.navigation.TabNavigator;
import com.jakewharton.rxrelay.BehaviorRelay;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

/**
 * Created by marcelobenites on 01/03/17.
 */

public class TabNavigatorActivity extends BaseActivity implements TabNavigator {

  private BehaviorRelay<Integer> navigatorSubject;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    navigatorSubject = BehaviorRelay.create();
  }

  @Override public void navigate(int tab) {
    navigatorSubject.call(tab);
  }

  @Override public Observable<Integer> navigation() {
    return navigatorSubject;
  }
}
