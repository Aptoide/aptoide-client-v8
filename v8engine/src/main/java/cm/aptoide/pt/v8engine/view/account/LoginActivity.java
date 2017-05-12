package cm.aptoide.pt.v8engine.view.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import cm.aptoide.pt.v8engine.R;

public class LoginActivity extends LoginBottomSheetActivity {

  public static final String ACCOUNT_TYPE = "account_type";
  public static final String AUTH_TYPE = "auth_type";
  public static final String IS_ADDING_NEW_ACCOUNT = "is_adding_new_account";

  private String accountType;
  private String authType;
  private boolean isNewAccount;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());

    loadExtras(getIntent());

    if (savedInstanceState == null) {
      getFragmentNavigator().navigateTo(
          LoginSignUpFragment.newInstance(false, true, false, accountType, authType, isNewAccount));
    }
  }

  @LayoutRes private int getLayoutId() {
    return R.layout.empty_frame;
  }

  private void loadExtras(Intent intent) {
    accountType = intent.getStringExtra(ACCOUNT_TYPE);
    authType = intent.getStringExtra(AUTH_TYPE);
    isNewAccount = intent.getBooleanExtra(IS_ADDING_NEW_ACCOUNT, false);
  }
}
