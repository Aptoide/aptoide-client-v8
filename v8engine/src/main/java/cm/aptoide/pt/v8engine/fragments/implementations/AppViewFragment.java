/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/05/2016.
 */

package cm.aptoide.pt.v8engine.fragments.implementations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragments.GridRecyclerFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sithengineer on 04/05/16.
 */
public class AppViewFragment extends GridRecyclerFragment {

	//
	// constants
	//

	public static final int VIEW_ID = R.layout.fragment_app_view;
	private static final String TAG = AppViewFragment.class.getName();

	//
	// vars
	//

	private String appId;
	private AppViewHeader header;

	//
	// micro widget for header
	//

	private static final class AppViewHeader {
		// views
		private ImageView featuredGraphic;
		private RelativeLayout badgeLayout;
		private ImageView badge;
		private TextView badgeText;
		private ImageView appIcon;
		private RatingBar ratingBar;
		private TextView fileSize;
		private TextView versionName;

		// ctor
		public AppViewHeader(@NonNull View view) {
			featuredGraphic = (ImageView) view.findViewById(R.id.featured_graphic);
			badgeLayout = (RelativeLayout) view.findViewById(R.id.badge_layout);
			badge = (ImageView) view.findViewById(R.id.badge_img);
			badgeText = (TextView) view.findViewById(R.id.badge_text);
			appIcon = (ImageView) view.findViewById(R.id.app_icon);
			ratingBar = (RatingBar) view.findViewById(R.id.rating_bar_top);
			fileSize = (TextView) view.findViewById(R.id.file_size);
			versionName = (TextView) view.findViewById(R.id.version_name);
		}

		// setup methods
		public void setup(@NonNull Object pojo) {
			// TODO
		}

	}

	//
	// bundle keys used internally in this fragment
	//

	private enum BundleKeys {
		APP_ID
	}

	//
	// static fragment default new instance method
	//

	public static AppViewFragment newInstance(String appId) {
		Bundle bundle = new Bundle();
		bundle.putString(BundleKeys.APP_ID.name(), appId);

		AppViewFragment fragment = new AppViewFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	//
	// methods
	//

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = super.onCreateView(inflater, container, savedInstanceState);
		header = new AppViewHeader(root);
		return root;
	}

	@Override
	protected void loadBundle(Bundle args) {
		super.loadBundle(args);
		appId = args.getString(BundleKeys.APP_ID.name());
	}

	@Override
	public void load() {
		loadAppInfo(appId).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(this::showAppInfo);
	}

	private Observable<Object> loadAppInfo(String appId) {
		return Observable.just(appId);
	}

	private void showAppInfo(Object obj) {
		// TODO generate displayables + widgets for body
		ArrayList<Displayable> displayables = new ArrayList<>();
		// ...
		addDisplayables(displayables);

		// setup header view
		header.setup(obj);
	}

}
