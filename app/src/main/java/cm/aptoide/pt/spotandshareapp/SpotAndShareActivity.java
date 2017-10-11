package cm.aptoide.pt.spotandshareapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.spotandshareandroid.SpotAndShareSender;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareMainFragment;
import cm.aptoide.pt.view.BackButtonActivity;
import rx.functions.Action1;

/**
 * Created by neuro on 27-06-2017.
 */

public class SpotAndShareActivity extends BackButtonActivity implements JoinGroupView {

  private cm.aptoide.pt.spotandshareandroid.SpotAndShare spotAndShare;
  private Action1<SpotAndShareSender> onSuccess;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_spotandshare_main);
    spotAndShare = ((AptoideApplication) getApplicationContext()).getSpotAndShare();
    if (savedInstanceState == null) {
      openSpotAndShareStart();
    }
  }

  public void openSpotAndShareStart() {
    getFragmentNavigator().navigateToWithoutBackSave(SpotAndShareMainFragment.newInstance(), true);
  }

  public void joinGroup() {
    spotAndShare.joinGroup(onSuccess, error -> onJoinGroupError());
  }

  private void onJoinGroupError() {
    runOnUiThread(new Runnable() {
      @Override public void run() {
        spotAndShare.leaveGroup(err -> showLeaveGroupErrorMessage());
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

  private void showLeaveGroupErrorMessage() {
    Toast.makeText(getApplicationContext(),
        R.string.spotandshare_message_waiting_to_receive_leave_group_error,
        Toast.LENGTH_SHORT)
        .show();
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }
}
