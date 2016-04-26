/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.pt.aptoideclientv8;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cm.aptoide.accountmanager.AccountManager;
import cm.aptoide.accountmanager.ws.CheckUserCredentialsRequest;
import cm.aptoide.accountmanager.ws.Mode;
import cm.aptoide.accountmanager.ws.OAuth2AuthenticationRequest;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.v8engine.fragments.GridRecyclerFragment;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getSupportFragmentManager().beginTransaction().replace(R.id.contentor, new GridRecyclerFragment()).commit();
		AccountManager.openAccountManager(this);

		GetStoreRequest getStoreRequest = GetStoreRequest.of("apps");
		getStoreRequest.execute(getStoreResponse -> System.out.println("Ss: " + getStoreResponse.getNodes().getMeta()), System.out::println);

		// Fabio Account Manager
		OAuth2AuthenticationRequest oAuth2AuthenticationRequest = new OAuth2AuthenticationRequest();
		oAuth2AuthenticationRequest.setMode(Mode.APTOIDE);
		oAuth2AuthenticationRequest.setPassword("data2244");
		oAuth2AuthenticationRequest.setUsername("jonas.pir1es@gmail.com");

		oAuth2AuthenticationRequest.execute(oAuth -> System.out.println("Amora: " + oAuth));

		// GetUserInfo
		CheckUserCredentialsRequest checkUserCredentialsRequest = new CheckUserCredentialsRequest(this);
		checkUserCredentialsRequest.setToken("4325281f253df81a85f30b12061f7d1c53c7fe7b");
		checkUserCredentialsRequest.execute(checkUserCredentialsJson -> System.out.println("Amora: 2: " + checkUserCredentialsJson));
	}
}
