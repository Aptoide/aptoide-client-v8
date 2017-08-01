package cm.aptoide.pt.spotandshare.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.R;
import cm.aptoide.pt.V8Engine;
import cm.aptoide.pt.view.ActivityView;

public class SpotSharePreviewActivity extends ActivityView {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.frame_layout);

    getFragmentNavigator().navigateToWithoutBackSave(V8Engine.getFragmentProvider()
        .newSpotShareFragment(true));
  }
}
