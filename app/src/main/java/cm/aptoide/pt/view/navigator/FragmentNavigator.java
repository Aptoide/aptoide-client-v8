package cm.aptoide.pt.view.navigator;

import android.support.v4.app.Fragment;
import rx.Observable;

public interface FragmentNavigator {

  String REQUEST_CODE_EXTRA = "cm.aptoide.pt.view.navigator.extra.REQUEST_CODE";

  void navigateForResult(Fragment fragment, int requestCode, boolean replace);

  void navigateToWithoutBackSave(Fragment fragment, boolean replace);

  void navigateToCleaningBackStack(Fragment fragment, boolean replace);

  String navigateTo(Fragment fragment, boolean replace);

  Observable<Result> results(int requestCode);

  void popWithResult(Result result);

  boolean popBackStack();

  void cleanBackStack();

  boolean cleanBackStackUntil(String fragmentTag);

  Fragment peekLast();

  Fragment getFragment();
}
