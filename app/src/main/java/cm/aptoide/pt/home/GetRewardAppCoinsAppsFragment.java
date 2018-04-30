package cm.aptoide.pt.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.WindowManager;
import cm.aptoide.pt.repository.request.RewardAppCoinsAppsRepository;
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment;
import cm.aptoide.pt.view.app.Application;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import cm.aptoide.pt.view.recycler.displayable.DisplayableGroup;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;

/**
 * Created by filipegoncalves on 4/27/18.
 */

public class GetRewardAppCoinsAppsFragment extends StoreTabGridRecyclerFragment {

  @Inject RewardAppCoinsAppsRepository rewardAppsRepository;
  @Inject GetRewardAppCoinsAppsNavigator rewardAppCoinsAppsNavigator;

  public static Fragment newInstance() {
    return new GetRewardAppCoinsAppsFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
  }

  @Nullable @Override
  protected Observable<List<Displayable>> buildDisplayables(boolean refresh, String url,
      boolean bypassServerCache) {
    return rewardAppsRepository.getAppCoinsRewardAppsFromHomeMore(refresh)
        .map(rewardApps -> {
          List<Displayable> displayables = new LinkedList<>();
          for (Application app : rewardApps) {
            displayables.add(new GridAppCoinsRewardAppsDisplayable(app, tag, navigationTracker,
                rewardAppCoinsAppsNavigator));
          }

          return Collections.singletonList(new DisplayableGroup(displayables,
              (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE),
              getContext().getResources()));
        });
  }
}
