/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 09/05/2016.
 */

package cm.aptoide.pt.v8engine.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;

import cm.aptoide.pt.v8engine.R;

/**
 * Created by neuro on 05-05-2016.
 */
public abstract class AptoideFragmentActivity extends AptoideBaseActivity {

	/*
	private WeakReference<Fragment> weakFragment;

	protected abstract Fragment createFragment();

	@Override
	protected void onStop() {
		super.onStop();
		if(weakFragment!=null) {
			Fragment fragment = weakFragment.get();
			if(fragment!=null) {
				getSupportFragmentManager().beginTransaction().remove(fragment)
						.commitAllowingStateLoss();
			}
			//weakFragment = null;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		Fragment fragment = null;
		if (weakFragment != null) {
			fragment = weakFragment.get();
		}

		if (fragment == null) {
			fragment = createFragment();
			weakFragment = new WeakReference<>(fragment);
		}

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_placeholder, fragment)
				.commit();
	}
	*/

	protected abstract Fragment createFragment();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.fragment_placeholder, createFragment())
					.commit();
		}

	}
}
