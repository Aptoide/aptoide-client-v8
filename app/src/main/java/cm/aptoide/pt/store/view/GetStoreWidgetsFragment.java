package cm.aptoide.pt.store.view;

import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.store.view.home.AdultRowDisplayable;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import java.util.List;
import rx.functions.Action1;

/**
 * Created by neuro on 26-12-2016.
 */

public class GetStoreWidgetsFragment extends GetStoreEndlessFragment<GetStoreWidgets> {

  @Override
  protected V7<GetStoreWidgets, ? extends Endless> buildRequest(boolean refresh, String url) {
    return requestFactoryCdnPool.newStoreWidgets(url);
  }

  @Override protected Action1<GetStoreWidgets> buildAction() {
    return getStoreWidgets -> {
      List<Displayable> first = parseDisplayables(getStoreWidgets).toBlocking()
          .first();
      addDisplayables(first);
    };
  }

  @Override public void onResume() {
    super.onResume();
    //if (getUserVisibleHint() && alreadyRegistered) {
    //  navigationTracker.registerView(ScreenTagHistory.Builder.build(this.getClass()
    //      .getSimpleName(), "home", storeContext));
    //  pageViewsAnalytics.sendPageViewedEvent();
    //}
  }
}
