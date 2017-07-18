package cm.aptoide.pt.spotandshareapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;
import cm.aptoide.pt.spotandshareandroid.SpotAndShareSender;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareMainFragment;
import cm.aptoide.pt.v8engine.view.BaseActivity;
import rx.functions.Action1;

/**
 * Created by neuro on 27-06-2017.
 */

public class MainActivity extends BaseActivity implements JoinGroupView {

  private cm.aptoide.pt.spotandshareandroid.SpotAndShare spotAndShare;
  private Action1<SpotAndShareSender> onSuccess;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
    if (savedInstanceState == null) {
      spotAndShare = ((SpotAndShareApplication) getApplicationContext()).getSpotAndShare();
      openSpotAndShareStart();
    }
  }

  public void openSpotAndShareStart() {
    getFragmentNavigator().navigateTo(SpotAndShareMainFragment.newInstance());
  }

  public void joinGroup() {
    spotAndShare.joinGroup(onSuccess, error -> onJoinGroupError());
  }

  private void onJoinGroupError() {
    runOnUiThread(new Runnable() {
      @Override public void run() {
        Toast.makeText(getApplicationContext(), "There was an error inside the group",
            Toast.LENGTH_SHORT)
            .show();
        finish();
      }
    });
  }

  public void registerJoinGroupSuccessCallback(Action1<SpotAndShareSender> onSuccess) {
    this.onSuccess = onSuccess;
  }

  public void unregisterJoinGroupSuccessCallback() {
    this.onSuccess = null;
  }
}
