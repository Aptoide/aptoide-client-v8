package cm.aptoide.accountmanager;

import android.os.Bundle;
import android.widget.Button;
import com.jakewharton.rxbinding.view.RxView;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by pedroribeiro on 15/12/16.
 */

public class LoggedInActivity2ndStep extends BaseActivity {

  private Button mContinueButton;
  private Button mPrivateProfile;

  private CompositeSubscription mSubscriptions;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());
    mSubscriptions = new CompositeSubscription();
    bindViews();
    setupListeners();
  }

  @Override protected String getActivityTitle() {
    return getString(R.string.create_profile_logged_in_activity_title);
  }

  @Override int getLayoutId() {
    return R.layout.logged_in_second_screen;
  }

  private void bindViews() {
    mContinueButton = (Button) findViewById(R.id.logged_in_continue);
    mPrivateProfile = (Button) findViewById(R.id.logged_in_private_button);
  }

  private void setupListeners() {
    mSubscriptions.add(RxView.clicks(mContinueButton).subscribe(clicks -> {
      finish();
    }));
    mSubscriptions.add(RxView.clicks(mPrivateProfile).subscribe(clicks -> {
      finish();
    }));
  }

}
