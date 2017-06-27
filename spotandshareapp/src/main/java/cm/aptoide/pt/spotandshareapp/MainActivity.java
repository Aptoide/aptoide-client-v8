package cm.aptoide.pt.spotandshareapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareMainFragment;
import cm.aptoide.pt.v8engine.view.BaseActivity;

/**
 * Created by neuro on 27-06-2017.
 */

public class MainActivity extends BaseActivity {

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
    if (savedInstanceState == null) {
      openSpotAndShareStart();
    }
  }

  public void openSpotAndShareStart() {
    getFragmentNavigator().navigateTo(SpotAndShareMainFragment.newInstance());
  }
}
