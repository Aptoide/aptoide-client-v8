/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 21/04/2016.
 */

package cm.aptoide.pt.aptoideclientv8;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.Executors;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.CheckUserCredentialsRequest;
import cm.aptoide.accountmanager.ws.LoginMode;
import cm.aptoide.accountmanager.ws.OAuth2AuthenticationRequest;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.v8engine.fragments.GridRecyclerFragment;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
private static final String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getSupportFragmentManager().beginTransaction().replace(R.id.contentor, new GridRecyclerFragment()).commit();

//		GetStoreRequest getStoreRequest = GetStoreRequest.of("apps");
//		getStoreRequest.execute(getStoreResponse -> System.out.println("Ss: " + getStoreResponse.getNodes().getMeta()), System.out::println);


//		Observable<String> s = AptoideAccountManager.invalidateAccessToken(this);
//		if (s != null) {
//			s.observeOn(AndroidSchedulers.mainThread()).doOnNext(new Action1<String>() {
//				@Override
//				public void call(String s1) {
//					Toast.makeText(MainActivity.this, s1, Toast.LENGTH_SHORT).show();
//					Log.d(TAG, "call() called with: " + "s = [" + s1 + "]");
//					Log.d(TAG, "call() called with: " + "s = [" + AptoideAccountManager.getAccessToken() + "]");
//				}
//			}).subscribe();
//		}

//		Executors.newSingleThreadScheduledExecutor().execute(() -> {
//			String token = AptoideAccountManager.invalidateAccessTokenSync(MainActivity.this);
//			Log.d(TAG, "onCreate: "+token);
//			Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
//		});
//
//		AptoideAccountManager.invalidateAccessToken(this).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Action1<String>() {
//			@Override
//			public void call(String s) {
//				Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
//				Log.d(TAG, "call: " + s);
//			}
//		}).subscribe();


		// Fabio Account Manager
//		OAuth2AuthenticationRequest oAuth2AuthenticationRequest = new OAuth2AuthenticationRequest();
//		oAuth2AuthenticationRequest.setMode(LoginMode.APTOIDE);
//		oAuth2AuthenticationRequest.setPassword("data2244");
//		oAuth2AuthenticationRequest.setUsername("jonas.pir1es@gmail.com");
//
//		oAuth2AuthenticationRequest.execute(oAuth -> System.out.println("Amora: " + oAuth));

		// GetUserInfo
//		CheckUserCredentialsRequest checkUserCredentialsRequest = new CheckUserCredentialsRequest(this);
//		checkUserCredentialsRequest.setToken("4325281f253df81a85f30b12061f7d1c53c7fe7b");
//		checkUserCredentialsRequest.execute(checkUserCredentialsJson -> System.out.println("Amora: 2: " + checkUserCredentialsJson));
	}
}
