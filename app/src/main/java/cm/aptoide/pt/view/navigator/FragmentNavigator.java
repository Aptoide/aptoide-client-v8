package cm.aptoide.pt.view.navigator;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import rx.Observable;

/**
 * Created by trinkes on 08/09/2017.
 */

public interface FragmentNavigator {
  void navigateWithoutReplace(Fragment fragment);

  void navigateForResultWithoutReplace(NavigateFragment fragment, int requestCode);

  Observable<Result> results(int requestCode);

  void popWithResult(Result result);

  void navigateUsing(Event event, String storeTheme, String title, String tag,
      StoreContext storeContext);

  String navigateTo(Fragment fragment);

  String navigateToAllowingStateLoss(Fragment fragment);

  void navigateToWithoutBackSave(Fragment fragment);

  void navigateToHomeCleaningBackStack();

  boolean popBackStack();

  void cleanBackStack();

  boolean cleanBackStackUntil(String fragmentTag);

  Fragment peekLast();

  Fragment getFragment();
}
