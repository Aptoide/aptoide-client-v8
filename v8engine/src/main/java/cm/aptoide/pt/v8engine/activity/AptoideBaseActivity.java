/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 01/05/2016.
 */

package cm.aptoide.pt.v8engine.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import lombok.Getter;

/**
 * Created by neuro on 01-05-2016.
 */
public abstract class AptoideBaseActivity extends AppCompatActivity {

	@Getter private boolean _resumed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentViewId());
		bindViews();
		setupToolbar();
		setupViews();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * Setup previously binded views.
	 */
	protected abstract void setupViews();

	/**
	 * Setup the toolbar, if present.
	 */
	protected abstract void setupToolbar();

	/**
	 * Bind needed views.
	 */
	protected abstract void bindViews();

	/**
	 * @return the LayoutRes to be set on {@link #setContentView(int)}.
	 */
	protected abstract
	@LayoutRes
	int getContentViewId();

	@Override
	protected void onPause() {
		super.onPause();
		_resumed = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		_resumed = true;
	}

	@Override
	protected void onStart() {
		super.onStart();
	}
}
