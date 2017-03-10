package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import cm.aptoide.pt.dataprovider.ws.v7.GetFollowersRequest;
import cm.aptoide.pt.dataprovider.ws.v7.GetFollowingRequest;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;

/**
 * Created by trinkes on 10/03/2017.
 */

public class TimeLineFollowStoreFragment extends TimeLineFollowFragment {

  private long storeId;

  public static TimeLineFollowStoreFragment newInstance(Long id, long followNumber,
      String storeTheme, FollowFragmentOpenMode openMode) {
    Bundle args = new Bundle();
    TimeLineFollowStoreFragment fragment = new TimeLineFollowStoreFragment();
    args.putLong(BundleKeys.STORE_ID, id);
    args.putString(TITLE_KEY,
        AptoideUtils.StringU.getFormattedString(R.string.social_timeline_followers_fragment_title,
            followNumber));
    args.putString(BundleCons.STORE_THEME, storeTheme);
    args.putSerializable(TimeLineFollowFragment.BundleKeys.OPEN_MODE, openMode);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    if (args.containsKey(BundleKeys.STORE_ID)) {
      storeId = args.getLong(BundleKeys.STORE_ID);
    }
  }

  @Override protected V7 buildRequest(FollowFragmentOpenMode openMode) {
    switch (openMode) {
      case FOLLOWERS:
        return GetFollowersRequest.ofStore(getBodyDecorator(), storeId);
      default:
      case FOLLOWING:
        return GetFollowingRequest.ofStore(getBodyDecorator(), storeId);
    }
  }

  public class BundleKeys {
    public static final String STORE_ID = "store_id";
  }
}
