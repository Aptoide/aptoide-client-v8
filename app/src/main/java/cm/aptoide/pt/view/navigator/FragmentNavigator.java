package cm.aptoide.pt.view.navigator;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.view.store.home.HomeFragment;
import rx.Observable;

public interface FragmentNavigator {

  String REQUEST_CODE_EXTRA = "cm.aptoide.pt.view.navigator.extra.REQUEST_CODE";

  void navigateForResult(Fragment fragment, int requestCode);

  void navigateToWithoutBackSave(Fragment fragment);

  void navigateToCleaningBackStack(Fragment fragment);

  String navigateTo(Fragment fragment);

  Observable<Result> results(int requestCode);

  void popWithResult(Result result);

  boolean popBackStack();

  void cleanBackStack();

  boolean cleanBackStackUntil(String fragmentTag);

  Fragment peekLast();

  Fragment getFragment();
}
