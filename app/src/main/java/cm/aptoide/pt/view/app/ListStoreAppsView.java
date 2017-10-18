package cm.aptoide.pt.view.app;

import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Observable;

/**
 * Created by trinkes on 17/10/2017.
 */

public interface ListStoreAppsView extends View {

  void setApps(List<Application> appsList);

  Observable<Application> getAppClick();
}
