/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 16/04/2016.
 */

package cm.aptoide.pt.aptoideclientv8;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import cm.aptoide.accountmanager.AccountManager;

import cm.aptoide.pt.R;
import cm.aptoide.pt.v8engine.fragments.GridRecyclerFragment;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getSupportFragmentManager().beginTransaction().replace(R.id.contentor, new GridRecyclerFragment()).commit();
		AccountManager.openAccountManager(this);
	}
}
