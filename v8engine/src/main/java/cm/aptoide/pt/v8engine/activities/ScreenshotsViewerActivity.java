/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 11/05/2016.
 */

package cm.aptoide.pt.v8engine.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.activity.AptoideBaseActivity;

/**
 * Created by sithengineer on 11/05/16.
 */
public class ScreenshotsViewerActivity extends AptoideBaseActivity {

	public static final String POSITION = "POSITION";
	public static final String URLs = "URLs";

	private String[] images = new String[0];
	private int currentItem;

	@Override
	protected void loadExtras(Bundle extras) {
		if (extras == null) {
			currentItem = getIntent().getIntExtra(POSITION, 0);
		} else {
			currentItem = extras.getInt(POSITION, 0);
		}
	}

	@Override
	protected void setupViews() {
		V8Engine.getThemePicker().setAptoideTheme(this);
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.page_screenshots_viewer);

		final ViewPager screenshots = (ViewPager) findViewById(R.id.screenShotsPager);

		ArrayList<String> uri = getIntent().getStringArrayListExtra("url");
		if (uri != null) {
			images = uri.toArray(images);
		}
		if (images != null && images.length > 0) {
			screenshots.setAdapter(new ViewPagerAdapterScreenshots(uri));
			screenshots.setCurrentItem(currentItem);
		}

		View btnCloseViewer = findViewById(R.id.btn_close_screenshots_window);
		btnCloseViewer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void setupToolbar() { }

	@Override
	protected void bindViews() {

	}

	@Override
	protected int getContentViewId() {
		return 0;
	}

	@Override
	protected String getAnalyticsScreenName() {
		return "Screenshots Viewer";
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_MENU) {
			// do nothing
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(POSITION, currentItem);
	}
}
