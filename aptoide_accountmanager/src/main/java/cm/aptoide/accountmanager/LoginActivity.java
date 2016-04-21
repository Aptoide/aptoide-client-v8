package cm.aptoide.accountmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;

/**
 * Created by trinkes on 4/18/16.
 */
public class LoginActivity extends BaseActivity implements AccountManager.LoginInterface {

    private Button mLoginButton;
    View content;
    private Button mRegisterButton;
    private LoginButton mFacebookLoginButton;
    private EditText password_box;
    private EditText emailBox;
    private Button hidePassButton;
    private CheckBox registerDevice;
    private Toolbar mToolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(getLayoutId());
        bindViews();
        AccountManager.getInstance().setupLogins(this, this, mFacebookLoginButton, mLoginButton, mRegisterButton);
        if (AccountManager.isLoggedIn(this)) {
            finish();
            Snackbar.make(content, R.string.one_account_allowed, Snackbar.LENGTH_SHORT).show();
            return;
        }

        setupShowHidePassButton();
        setupToolbar();

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

    @Override
    protected String getActivityTitle() {
        // TODO: 4/21/16 trinkes resource
        return "Login Activity";
    }

    private void setupShowHidePassButton() {
        hidePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int cursorPosition = password_box.getSelectionStart();
                final boolean passwordShown = password_box.getTransformationMethod() == null;
                v.setBackgroundResource(passwordShown ? R.drawable.icon_closed_eye : R.drawable.icon_open_eye);
                password_box.setTransformationMethod(passwordShown ? new PasswordTransformationMethod() : null);
                password_box.setSelection(cursorPosition);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AccountManager.handleSignInResult(requestCode, resultCode, data);
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
    }

    @Override
    int getLayoutId() {
        return R.layout.login_activity_layout;
    }

    @Override
    public void onLoginSuccess() {
        Toast.makeText(LoginActivity.this, "login successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoginFail() {
        Toast.makeText(LoginActivity.this, "login failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getUser() {
        return emailBox.getText().toString();
    }

    @Override
    public String getPassword() {
        return password_box.getText().toString();
    }
}
