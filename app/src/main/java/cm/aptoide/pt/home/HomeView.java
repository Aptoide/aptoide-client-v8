package cm.aptoide.pt.home;

import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.app.Application;
import java.util.List;
import rx.Observable;

/**
 * Created by jdandrade on 07/03/2018.
 */

public interface HomeView extends View {
  void showHomeBundles(List<HomeBundle> bundles);

  void showLoading();

  void hideLoading();

  void showGenericError();

  Observable<HomeClick> moreClicked();

  Observable<Application> appClicked();

  Observable<GetAdsResponse.Ad> adClicked();
}
