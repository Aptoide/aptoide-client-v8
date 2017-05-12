package cm.aptoide.pt.v8engine.spotandshare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.BaseActivity;

/**
 * Created by neuro on 10-04-2017.
 */

public class SpotSharePreviewActivity extends BaseActivity {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.frame_layout);

    getFragmentNavigator().navigateToWithoutBackSave(V8Engine.getFragmentProvider()
        .newSpotShareFragment(true));
  }
}
