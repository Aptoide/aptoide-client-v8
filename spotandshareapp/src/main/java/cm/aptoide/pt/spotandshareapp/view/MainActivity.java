package cm.aptoide.pt.spotandshareapp.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.spotandshareapp.R;

public class MainActivity extends ActivityView implements MainActivityView {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (savedInstanceState == null) {
      openSpotAndShareStart();
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }

  @Override public void openSpotAndShareStart() {
    getFragmentNavigator().navigateTo(SpotAndShareMainFragment.newInstance());
  }
}
