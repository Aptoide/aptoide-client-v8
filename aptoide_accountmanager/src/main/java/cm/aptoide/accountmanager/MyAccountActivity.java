package cm.aptoide.accountmanager;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.SimpleSubscriber;

/**
 * Created by trinkes on 5/2/16.
 */
public class MyAccountActivity extends BaseActivity {

  private static final String TAG = MyAccountActivity.class.getSimpleName();

  private Button mLogout;
  private Toolbar mToolbar;
  private TextView mUsernameTextview;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());
    bindViews();
    setupToolbar();
    AptoideAccountManager.setupLogout(this, mLogout);
    mUsernameTextview.setText(AptoideAccountManager.getUserName());

    findViewById(R.id.btn_user_name).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Snackbar.make(v, AptoideAccountManager.getUserName(), Snackbar.LENGTH_LONG).show();
      }
    });

    findViewById(R.id.btn_access_token).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Snackbar.make(v, AptoideAccountManager.getAccessToken(), Snackbar.LENGTH_LONG).show();
      }
    });

    findViewById(R.id.btn_invalidate_token).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        AptoideAccountManager.invalidateAccessToken(MyAccountActivity.this)
            .subscribe(new SimpleSubscriber<String>() {
              @Override public void onNext(String s) {
                Snackbar.make(v, s, Snackbar.LENGTH_LONG).show();
              }
            });
      }
    });

    findViewById(R.id.btn_refresh_token).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Snackbar.make(v, SecurePreferences.getString("aptoide_account_manager_refresh_token_key"),
            Snackbar.LENGTH_LONG).show();
      }
    });
  }

  @Override protected String getActivityTitle() {
    return "My Account";
  }

  @Override int getLayoutId() {
    return R.layout.my_account_activity;
  }

  @Override protected void onResume() {
    super.onResume();
    Log.d(TAG, "onResume: ");
  }

  private void setupToolbar() {
    if (mToolbar != null) {
      setSupportActionBar(mToolbar);
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowTitleEnabled(true);
      getSupportActionBar().setTitle(getActivityTitle());
    }
  }

  private void bindViews() {
    mToolbar = (Toolbar) findViewById(R.id.toolbar_login);
    mLogout = (Button) findViewById(R.id.button_logout);
    mUsernameTextview = (TextView) findViewById(R.id.username);
  }
}
