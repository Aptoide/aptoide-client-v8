package cm.aptoide.accountmanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.jakewharton.rxbinding.view.RxView;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by pedroribeiro on 15/12/16.
 */

public class LoggedInActivity  extends BaseActivity{

  private Button mContinueButton;
  private Button mMoreInfoButton;
  private CompositeSubscription mSubscriptions;


  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());
    mSubscriptions = new CompositeSubscription();
    bindViews();
    setupListeners();
  }

  private void bindViews() {
    mContinueButton = (Button) findViewById(R.id.logged_in_continue);
    mMoreInfoButton = (Button) findViewById(R.id.logged_in_more_info_button);
  }

  @Override protected String getActivityTitle() {
    return getString(R.string.create_profile_logged_in_activity_title);
  }

  @Override int getLayoutId() {
    return R.layout.logged_in_first_screen;
  }

  private void setupListeners() {
    mSubscriptions.add(RxView.clicks(mContinueButton).subscribe(clicks -> {
      finish();
    }));
    mSubscriptions.add(RxView.clicks(mMoreInfoButton).subscribe(clicks -> {
      Intent intent = new Intent(this, LoggedInActivity2ndStep.class);
      startActivityForResult(intent, LOGGED_IN_SECOND_STEP_CODE);
    }));
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    mSubscriptions.clear();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case LOGGED_IN_SECOND_STEP_CODE:
        finishActivity(125);
    }
  }
}
