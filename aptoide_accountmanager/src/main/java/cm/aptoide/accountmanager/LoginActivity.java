package cm.aptoide.accountmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.PasswordTransformationMethod;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;

import cm.aptoide.pt.logger.Logger;

/**
 * Created by trinkes on 4/18/16.
 */
public class LoginActivity extends BaseActivity implements AptoideAccountManager.ILoginInterface {

	public static final String OPEN_MY_ACCOUNT_ON_LOGIN_SUCCESS =
			"OPEN_MY_ACCOUNT_ON_LOGIN_SUCCESS";
	private static final String TAG = LoginActivity.class.getSimpleName();
	View content;
	private Button mLoginButton;
	private Button mRegisterButton;
	private LoginButton mFacebookLoginButton;
	private EditText password_box;
	private EditText emailBox;
	private Button hidePassButton;
	private CheckBox registerDevice;
	private Toolbar mToolbar;
	private TextView forgotPassword;
	private boolean openMyAccountOnLoginSuccess;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.d(TAG, "onCreate() called with: " + "savedInstanceState = [" + savedInstanceState +
				"]");
		FacebookSdk.sdkInitialize(getApplicationContext());
		setContentView(getLayoutId());
		bindViews();
		openMyAccountOnLoginSuccess = getIntent().getBooleanExtra
				(OPEN_MY_ACCOUNT_ON_LOGIN_SUCCESS, true);
		AptoideAccountManager.getInstance()
				.setupLogins(this, this, mFacebookLoginButton, mLoginButton, mRegisterButton);
		setupShowHidePassButton();
		setupToolbar();
		setupViewListeners();
	}

	@Override
	protected String getActivityTitle() {
		return getString(R.string.login_activity_title);
	}

	@Override
	int getLayoutId() {
		return R.layout.login_activity_layout;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void setupViewListeners() {
		SpannableString forgetString = new SpannableString(getString(R.string.forgot_passwd));
		forgetString.setSpan(new UnderlineSpan(), 0, forgetString.length(), 0);
		forgotPassword.setText(forgetString);
		forgotPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent passwordRecovery = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m" +
						".aptoide.com/account/password-recovery"));
				startActivity(passwordRecovery);
			}
		});
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

	private void setupShowHidePassButton() {
		hidePassButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final int cursorPosition = password_box.getSelectionStart();
				final boolean passwordShown = password_box.getTransformationMethod() == null;
				v.setBackgroundResource(passwordShown ? R.drawable.icon_closed_eye : R.drawable
						.icon_open_eye);
				password_box.setTransformationMethod(passwordShown ? new
						PasswordTransformationMethod() : null);
				password_box.setSelection(cursorPosition);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		AptoideAccountManager.onActivityResult(this, requestCode, resultCode, data);
	}

	private void bindViews() {
		content = findViewById(android.R.id.content);
		mLoginButton = (Button) findViewById(R.id.button_login);
		mRegisterButton = (Button) findViewById(R.id.button_register);
		mFacebookLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
		password_box = (EditText) findViewById(R.id.password);
		emailBox = (EditText) findViewById(R.id.username);
		hidePassButton = (Button) findViewById(R.id.btn_show_hide_pass);
		registerDevice = (CheckBox) findViewById(R.id.link_my_device);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		forgotPassword = (TextView) findViewById(R.id.forgot_password);
	}

	@Override
	public void onLoginSuccess() {
		Toast.makeText(LoginActivity.this, R.string.login_successful, Toast.LENGTH_SHORT).show();
		finish();
		if (openMyAccountOnLoginSuccess) {
			AptoideAccountManager.openAccountManager(this);
		} else {
			finish();
		}
	}

	@Override
	public void onLoginFail(String reason) {
		Toast.makeText(LoginActivity.this, reason, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public String getIntroducedUserName() {
		return emailBox.getText().toString();
	}

	@Override
	public String getIntroducedPassword() {
		return password_box.getText().toString();
	}
}
