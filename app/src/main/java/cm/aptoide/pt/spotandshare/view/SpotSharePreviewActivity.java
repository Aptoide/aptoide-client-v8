package cm.aptoide.pt.spotandshare.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.ActivityView;

public class SpotSharePreviewActivity extends ActivityView {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.frame_layout);

    getFragmentNavigator().navigateToWithoutBackSave(AptoideApplication.getFragmentProvider()
        .newSpotShareFragment(true));
  }
}
