package cm.aptoide.pt.store.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

  public static Fragment newInstance(boolean addAdultFilter) {
    Bundle args = new Bundle();
    args.putBoolean(BundleKeys.ADD_ADULT_FILTER, addAdultFilter);
    Fragment fragment = new GetStoreWidgetsFragment();
    Bundle arguments = fragment.getArguments();
    if (arguments != null) {
      args.putAll(arguments);
    }
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);

    if (getArguments().getBoolean(BundleKeys.ADD_ADULT_FILTER, false)) {
      endlessRecyclerOnScrollListener.addOnEndlessFinishListener(__ -> {
        final AdultRowDisplayable displayable =
            new AdultRowDisplayable(GetStoreWidgetsFragment.this);
        if (!GetStoreWidgetsFragment.this.contains(displayable)) {
          GetStoreWidgetsFragment.this.addDisplayable(displayable, true);
        }
      });
    }
  }

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

  private static class BundleKeys {
    private static final String ADD_ADULT_FILTER = "addAdultFilter";
  }
}
