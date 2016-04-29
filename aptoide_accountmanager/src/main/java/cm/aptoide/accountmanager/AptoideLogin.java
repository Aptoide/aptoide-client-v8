package cm.aptoide.accountmanager;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import cm.aptoide.accountmanager.ws.LoginMode;

/**
 * Created by trinkes on 4/26/16.
 */
public class AptoideLogin {

    static void setupAptoideLogin(Button loginButton, Button registerButton) {

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = AptoideAccountManager.getInstance().getIntroducedUserName();
                String password = AptoideAccountManager.getInstance().getIntroducedPassword();

                if (username == null || password == null || (username.length() == 0 || password.length() == 0)) {
                    Toast.makeText(v.getContext(), R.string.fields_cannot_empty, Toast.LENGTH_LONG).show();
                    return;
                }

                AptoideAccountManager.loginUserCredentials(LoginMode.APTOIDE, username, password, null);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 4/22/16 trinkes use REQ_SIGNUP on startActivityForResult
                Snackbar.make(v, "Register acitivity not implemented yet", Snackbar.LENGTH_LONG).show();
//                Intent signup = new Intent(v.getContext(), signupClass);
//                ((Activity)v.getContext()).startActivityForResult(signup, REQ_SIGNUP);
            }
        });
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
