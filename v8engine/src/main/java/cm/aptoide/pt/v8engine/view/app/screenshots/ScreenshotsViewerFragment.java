/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 29/07/2016.
 */

package cm.aptoide.pt.v8engine.view.app.screenshots;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.fragment.UIComponentFragment;
import java.util.ArrayList;

/**
 * Created by sithengineer on 11/05/16.
 * <p>
 * code migrated from v7
 */
public class ScreenshotsViewerFragment extends UIComponentFragment {

  // vars
  private ArrayList<String> uris;
  private int currentItem;
  // views
  private ViewPager screenshots;
  private View btnCloseViewer;

  // static builder
  public static ScreenshotsViewerFragment newInstance(ArrayList<String> uris, int currentItem) {
    ScreenshotsViewerFragment fragment = new ScreenshotsViewerFragment();

    Bundle bundle = new Bundle();
    bundle.putStringArrayList(BundleArgs.URIs.name(), uris);
    bundle.putInt(BundleArgs.POSITION.name(), currentItem);

    fragment.setArguments(bundle);
    return fragment;
  }

  // methods
  @Override public void loadExtras(Bundle extras) {
    if (extras == null) {
      currentItem = getActivity().getIntent()
          .getIntExtra(BundleArgs.POSITION.name(), 0);
    } else {
      currentItem = extras.getInt(BundleArgs.POSITION.name(), 0);
    }

    if (extras == null) {
      uris = getActivity().getIntent()
          .getStringArrayListExtra(BundleArgs.URIs.name());
    } else {
      uris = extras.getStringArrayList(BundleArgs.URIs.name());
    }
  }

  @Override public void setupViews() {

    if (uris != null && uris.size() > 0) {
      screenshots.setAdapter(new ViewPagerAdapterScreenshots(uris));
      screenshots.setCurrentItem(currentItem);
    }

    btnCloseViewer.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //getActivity().finish();
        getActivity().onBackPressed();
      }
    });
  }

  @Override public void onResume() {
    super.onResume();
    hideSystemUI();
  }

  @Override public void onPause() {
    super.onPause();
    showSystemUI();
  }

  // This snippet shows the system bars. It does this by removing all the flags
  // except for the ones that make the content appear under the system bars.
  private void showSystemUI() {
    screenshots.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
  }

  // This snippet hides the system bars.
  private void hideSystemUI() {
    // Set the IMMERSIVE flag.
    // Set the content to appear under the system bars so that the content
    // doesn't resize when the system bars hide and show.
    screenshots.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        // hide nav bar
        | View.SYSTEM_UI_FLAG_FULLSCREEN
        // hide status bar
        | View.SYSTEM_UI_FLAG_IMMERSIVE);
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_screenshots_viewer;
  }

  @Override public void bindViews(View view) {
    //V8Engine.getThemePicker().setAptoideTheme(this);
    //super.onCreate(savedInstanceState);

    screenshots = (ViewPager) view.findViewById(R.id.screen_shots_pager);
    btnCloseViewer = view.findViewById(R.id.btn_close_screenshots_window);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(BundleArgs.POSITION.name(), currentItem);
    outState.putStringArrayList(BundleArgs.URIs.name(), uris);
  }

  // constants
  private enum BundleArgs {
    POSITION, URIs
  }

	/*
  @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_MENU) {
			// do nothing
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
	*/
}
