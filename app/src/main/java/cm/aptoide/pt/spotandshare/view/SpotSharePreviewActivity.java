package cm.aptoide.pt.spotandshare.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.view.ActivityView;
import javax.inject.Inject;

public class SpotSharePreviewActivity extends ActivityView {

  @Inject FragmentNavigator fragmentNavigator;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getActivityComponent().inject(this);
    setContentView(R.layout.frame_layout);

    fragmentNavigator.navigateToWithoutBackSave(AptoideApplication.getFragmentProvider()
        .newSpotShareFragment(true), true);
  }
}
