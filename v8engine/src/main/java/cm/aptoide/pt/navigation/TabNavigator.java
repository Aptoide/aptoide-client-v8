package cm.aptoide.pt.navigation;

import rx.Observable;

/**
 * Created by marcelobenites on 01/03/17.
 */
public interface TabNavigator {

  int DOWNLOADS = 1;
  int UPDATES = 2;
  int TIMELINE = 3;
  int STORES = 4;

  void navigate(int tab);

  Observable<Integer> navigation();
}
