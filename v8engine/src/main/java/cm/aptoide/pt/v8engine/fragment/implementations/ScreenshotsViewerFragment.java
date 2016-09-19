/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 29/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.adapters.ViewPagerAdapterScreenshots;
import cm.aptoide.pt.v8engine.fragment.SupportV4BaseFragment;
import java.util.ArrayList;

/**
 * Created by sithengineer on 11/05/16.
 * <p>
 * code migrated from v7
 */
public class ScreenshotsViewerFragment extends SupportV4BaseFragment {

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
      currentItem = getActivity().getIntent().getIntExtra(BundleArgs.POSITION.name(), 0);
    } else {
      currentItem = extras.getInt(BundleArgs.POSITION.name(), 0);
    }

    if (extras == null) {
      uris = getActivity().getIntent().getStringArrayListExtra(BundleArgs.URIs.name());
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

  @Override public int getContentViewId() {
    return R.layout.fragment_screenshots_viewer;
  }

  @Override public void bindViews(View view) {
    //V8Engine.getThemePicker().setAptoideTheme(this);
    //super.onCreate(savedInstanceState);

    getActivity().getWindow()
        .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

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
