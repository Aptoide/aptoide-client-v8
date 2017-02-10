/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.v8engine.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.Toast;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.SetUserRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import com.jakewharton.rxbinding.view.RxView;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by pedroribeiro on 15/12/16.
 */

public class LoggedInActivity2ndStep extends AccountBaseActivity {

  private static final String TAG = LoggedInActivity2ndStep.class.getSimpleName();

  private final AptoideClientUUID aptoideClientUUID =
      new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
          DataProvider.getContext());

  private Button mContinueButton;
  private Button mPrivateProfile;
  private CompositeSubscription mSubscriptions;
  private Toolbar mToolbar;
  private ProgressDialog pleaseWaitDialog;
  private AptoideAccountManager accountManager;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());
    accountManager = ((V8Engine) getApplicationContext()).getAccountManager();
    mSubscriptions = new CompositeSubscription();
    bindViews();
    setupToolbar();
    setupListeners();
  }

  @Override public String getActivityTitle() {
    return getString(cm.aptoide.accountmanager.R.string.create_profile_logged_in_activity_title);
  }

  @Override public int getLayoutId() {
    return cm.aptoide.accountmanager.R.layout.logged_in_second_screen;
  }

  private void bindViews() {
    mToolbar = (Toolbar) findViewById(cm.aptoide.accountmanager.R.id.toolbar);
    mContinueButton = (Button) findViewById(cm.aptoide.accountmanager.R.id.logged_in_continue);
    mPrivateProfile =
        (Button) findViewById(cm.aptoide.accountmanager.R.id.logged_in_private_button);
    mToolbar = (Toolbar) findViewById(cm.aptoide.accountmanager.R.id.toolbar);
  }

  private void setupToolbar() {
    setSupportActionBar(mToolbar);
    getSupportActionBar().setTitle(getActivityTitle());
  }

  private void setupListeners() {
    mSubscriptions.add(RxView.clicks(mContinueButton).subscribe(clicks -> {

      pleaseWaitDialog = GenericDialogs.createGenericPleaseWaitDialog(this,
          getApplicationContext().getString(cm.aptoide.accountmanager.R.string.please_wait));
      pleaseWaitDialog.show();

      SetUserRequest.of(aptoideClientUUID.getUniqueIdentifier(), UserAccessState.PUBLIC.toString(),
          accountManager.getAccessToken()).execute(answer -> {
        if (answer.isOk()) {
          Logger.v(TAG, "user is public");
          Toast.makeText(LoggedInActivity2ndStep.this,
              cm.aptoide.accountmanager.R.string.successful, Toast.LENGTH_SHORT).show();
        } else {
          Logger.v(TAG, "user is public: error: " + answer.getError().getDescription());
          Toast.makeText(LoggedInActivity2ndStep.this,
              cm.aptoide.accountmanager.R.string.unknown_error, Toast.LENGTH_SHORT).show();
        }
        goTo();
      }, throwable -> {
        goTo();
      });
    }));
    mSubscriptions.add(RxView.clicks(mPrivateProfile).subscribe(clicks -> {

      pleaseWaitDialog = GenericDialogs.createGenericPleaseWaitDialog(this,
          getApplicationContext().getString(cm.aptoide.accountmanager.R.string.please_wait));
      pleaseWaitDialog.show();

      SetUserRequest.of(aptoideClientUUID.getUniqueIdentifier(),
          UserAccessState.UNLISTED.toString(), accountManager.getAccessToken())
          .execute(answer -> {
            if (answer.isOk()) {
              Logger.v(TAG, "user is private");
              Toast.makeText(LoggedInActivity2ndStep.this, R.string.successful, Toast.LENGTH_SHORT)
                  .show();
            } else {
              Logger.v(TAG, "user is private: error: " + answer.getError().getDescription());
              Toast.makeText(LoggedInActivity2ndStep.this, R.string.unknown_error,
                  Toast.LENGTH_SHORT).show();
            }

        goTo();
      }, throwable -> {

        goTo();
      });
    }));
  }

  private void goTo() {

    if (getIntent() != null && getIntent().getBooleanExtra(AptoideAccountManager.IS_FACEBOOK_OR_GOOGLE,
        false)) {
      updateUserInfo();
    } else {
      if (pleaseWaitDialog != null && pleaseWaitDialog.isShowing()) {
        pleaseWaitDialog.dismiss();
      }
      startActivity(getIntent().setClass(this, CreateStoreActivity.class));
      finish();
    }
  }

  private void updateUserInfo() {
    accountManager.syncUser().subscribe(() -> {
      if (pleaseWaitDialog != null && pleaseWaitDialog.isShowing()) {
        pleaseWaitDialog.dismiss();
      }
      finish();
    }, throwable -> throwable.printStackTrace());
  }
}
