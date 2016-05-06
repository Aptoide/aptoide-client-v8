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

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Locale;

import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.utils.ObservableUtils;
import cm.aptoide.pt.v8engine.Aptoide;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragments.GridRecyclerFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewInstallDisplayable;
import rx.Observable;

/**
 * Created by sithengineer on 04/05/16.
 */
public class AppViewFragment extends GridRecyclerFragment {

	//
	// constants
	//

	public static final int VIEW_ID = R.layout.fragment_app_view;
	//private static final String TAG = AppViewFragment.class.getName();

	@Override
	public int getRootViewId() {
		return VIEW_ID;
	}

	//
	// vars
	//

	private int appId;
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
		private TextView downloadsCount;

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
			downloadsCount = (TextView) view.findViewById(R.id.downloads_count);
		}

		// setup methods
		public void setup(@NonNull GetAppMeta.App pojo) {
			Glide.with(Aptoide.getContext()).load(pojo.getIcon()).into(appIcon);
			Glide.with(Aptoide.getContext()).load(pojo.getGraphic()).into(featuredGraphic); //
			// TODO add placeholders

			ratingBar.setRating(pojo.getStats().getRating().getAvg());
			fileSize.setText(String.format(Locale.ROOT, "%d", pojo.getFile().getFilesize()));
			versionName.setText(pojo.getFile().getVername());
			downloadsCount.setText(String.format(Locale.ROOT, "%d", pojo.getStats().getDownloads()));

			// TODO add badge
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

	public static AppViewFragment newInstance(int appId) {
		Bundle bundle = new Bundle();
		bundle.putInt(BundleKeys.APP_ID.name(), appId);

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
		appId = args.getInt(BundleKeys.APP_ID.name());
	}

	@Override
	public void load() {
		loadAppInfo(appId)
				.compose(ObservableUtils.applySchedulers())
				.subscribe(this::showAppInfo);
	}

	private Observable<GetApp> loadAppInfo(int appId) {
		return GetAppRequest.of(appId).observe();
	}

	private void showAppInfo(GetApp pojo) {

		GetAppMeta.App app = pojo.getNodes().getMeta().getData();

		// TODO generate displayables for app body

		ArrayList<Displayable> displayables = new ArrayList<>();
		displayables.add(new AppViewInstallDisplayable(app));

		// ...

		// setup displayables in view
		addDisplayables(displayables);

		// setup header in view
		header.setup(app);
	}

}
