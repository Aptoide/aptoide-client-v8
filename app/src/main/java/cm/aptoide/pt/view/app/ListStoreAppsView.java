package cm.aptoide.pt.view.app;

import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by trinkes on 17/10/2017.
 */

public interface ListStoreAppsView extends View {

  void addApps(List<AptoideApp> appsList);

  Observable<AptoideApp> getAppClick();

  Observable<Object> reachesBottom();

  void hideLoading();

  void showLoading();

  PublishSubject<Void> getRefreshEvent();

  void hideRefreshLoading();

  void setApps(List<AptoideApp> applications);

  void showNetworkError();

  void showGenericError();

  Observable<Void> getRetryEvent();

  void showStartingLoading();
}
