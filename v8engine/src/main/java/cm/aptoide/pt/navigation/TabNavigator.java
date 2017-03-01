package cm.aptoide.pt.navigation;

import rx.Observable;

/**
 * Created by marcelobenites on 01/03/17.
 */
public interface TabNavigator {

  void navigateToDownloads();

  Observable<Void> downloadNavigation();

}
