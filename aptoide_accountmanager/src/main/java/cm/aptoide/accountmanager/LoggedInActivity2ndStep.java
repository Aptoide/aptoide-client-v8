package cm.aptoide.accountmanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.SetUserRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import com.jakewharton.rxbinding.view.RxView;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by pedroribeiro on 15/12/16.
 */

public class LoggedInActivity2ndStep extends BaseActivity {

  private static final String TAG = LoggedInActivity2ndStep.class.getSimpleName();

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
      SetUserRequest.of(new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
          DataProvider.getContext()).getAptoideClientUUID(), "PUBLIC").execute(answer -> {
        if (answer.isOk()) {
          Logger.v(TAG, "user is public");
        } else {
          Logger.v(TAG, "user is public: error: " + answer.getError().getDescription());
        }
        startActivity(new Intent(this, CreateStoreActivity.class));
        finish();
      });
    }));
    mSubscriptions.add(RxView.clicks(mPrivateProfile).subscribe(clicks -> {
      SetUserRequest.of(new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
          DataProvider.getContext()).getAptoideClientUUID(), "UNLISTED").execute(answer -> {
        if (answer.isOk()) {
          Logger.v(TAG, "user is private");
        } else {
          Logger.v(TAG, "user is private: error: " + answer.getError().getDescription());
        }
        startActivity(new Intent(this, CreateStoreActivity.class));
        finish();
      });
    }));
  }
}
