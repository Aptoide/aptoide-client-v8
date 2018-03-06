package cm.aptoide.pt.home;

import cm.aptoide.pt.presenter.View;
import rx.Observable;

/**
 * Created by jdandrade on 05/03/2018.
 */

interface AptoideBottomNavigationView extends View {

  Observable<Integer> navigationEvent();

  void showFragment(Integer type);
}
