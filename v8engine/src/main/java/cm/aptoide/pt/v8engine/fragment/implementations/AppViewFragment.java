/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 10/05/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.CollapsingToolbarLayout;
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
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView
		.AppViewInstallDisplayable;
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
	private long appId;

	//
	// vars
	//
	private AppViewHeader header;

	public static AppViewFragment newInstance(long appId) {
		Bundle bundle = new Bundle();
		bundle.putLong(BundleKeys.APP_ID.name(), appId);

		AppViewFragment fragment = new AppViewFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	//
	// micro widget for header
	//

	@Override
	public int getRootViewId() {
		return VIEW_ID;
	}

	//
	// bundle keys used internally in this fragment
	//

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
			savedInstanceState) {
		View root = super.onCreateView(inflater, container, savedInstanceState);
		header = new AppViewHeader(root);
		return root;
	}

	//
	// static fragment default new instance method
	//

	@Override
	public void load(boolean refresh) {
		if (refresh) {
			loadAppInfo((int) appId).compose(ObservableUtils.applySchedulers())
					.subscribe(this::showAppInfo);
		}
	}

	//
	// methods
	//

	@Override
	protected void loadBundle(Bundle args) {
		super.loadBundle(args);
		appId = args.getLong(BundleKeys.APP_ID.name());
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

	private enum BundleKeys {
		APP_ID
	}

	private static final class AppViewHeader {

		// views
		private CollapsingToolbarLayout collapsingToolbar;
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
			collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
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

			if (pojo.getGraphic() != null) {
				Glide.with(Aptoide.getContext()).load(pojo.getGraphic()).into(featuredGraphic);
			}
			/*
			else if (screenshots != null && screenshots.size() > 0 && !TextUtils.isEmpty
			(screenshots.get(0).url)) {
				Glide.with(Aptoide.getContext()).load(screenshots.get(0).url).into
				(mFeaturedGraphic);
			}
			*/

			if (pojo.getIcon() != null) {
				Glide.with(Aptoide.getContext()).load(pojo.getIcon()).into(appIcon);
			}

			// TODO add placeholders in image loading

			collapsingToolbar.setTitle(pojo.getName());
			ratingBar.setRating(pojo.getStats().getRating().getAvg());
			fileSize.setText(String.format(Locale.ROOT, "%d", pojo.getFile().getFilesize()));
			versionName.setText(pojo.getFile().getVername());
			downloadsCount.setText(String.format(Locale.ROOT, "%d", pojo.getStats()
					.getDownloads()));

			@DrawableRes int badgeResId = 0;
			@StringRes int badgeMessageId = 0;
			switch (pojo.getFile().getMalware().getRank()) {
				case GetAppMeta.GetAppMetaFile.Malware.TRUSTED:
					badgeResId = R.drawable.ic_badge_trusted;
					badgeMessageId = R.string.appview_header_trusted_text;
					break;

				case GetAppMeta.GetAppMetaFile.Malware.WARNING:
					badgeResId = R.drawable.ic_badge_warning;
					badgeMessageId = R.string.warning;
					break;

				default:
				case GetAppMeta.GetAppMetaFile.Malware.UNKNOWN:
					badgeResId = R.drawable.ic_badge_unknown;
					badgeMessageId = R.string.unknown;
					break;
			}

			Glide.with(Aptoide.getContext()).load(badgeResId).into(badge);
			badgeText.setText(badgeMessageId);
		}

	}
}

