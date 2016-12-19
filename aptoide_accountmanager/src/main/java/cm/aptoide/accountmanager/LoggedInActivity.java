package cm.aptoide.accountmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.SetUserRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.GenericDialogs;
import com.jakewharton.rxbinding.view.RxView;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by pedroribeiro on 15/12/16.
 */

public class LoggedInActivity extends BaseActivity {

  private static final String TAG = LoggedInActivity.class.getSimpleName();

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

      ProgressDialog pleaseWaitDialog = GenericDialogs.createGenericPleaseWaitDialog(this,
          getApplicationContext().getString(R.string.please_wait));
      pleaseWaitDialog.show();

      SetUserRequest.of(new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
              DataProvider.getContext()).getAptoideClientUUID(), "PUBLIC",
          AptoideAccountManager.getAccessToken()).execute(answer -> {
        if (answer.isOk()) {
          Logger.v(TAG, "user is public");
          Toast.makeText(LoggedInActivity.this, R.string.successful, Toast.LENGTH_SHORT).show();
        } else {
          Logger.v(TAG, "user is public: error: " + answer.getError().getDescription());
          Toast.makeText(LoggedInActivity.this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
        }
        pleaseWaitDialog.show();

        startActivity(getIntent().setClass(this, CreateStoreActivity.class));
        finish();
      }, throwable -> {
        pleaseWaitDialog.show();
        startActivity(getIntent().setClass(this, CreateStoreActivity.class));
        finish();
      });
    }));
    mSubscriptions.add(RxView.clicks(mMoreInfoButton).subscribe(clicks -> {
      startActivity(getIntent().setClass(this, LoggedInActivity2ndStep.class));
      finish();
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
