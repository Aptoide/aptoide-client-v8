/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.v8engine.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;

/**
 * Created by sithengineer on 12/05/16.
 * <p>
 * code migrated form v7
 */
public class ViewPagerAdapterScreenshots extends PagerAdapter {

	private final ArrayList<String> uris;

	public ViewPagerAdapterScreenshots(ArrayList<String> uris) {
		this.uris = uris;
	}

	private int getPlaceholder(Context ctx) {
		int id;
		if (ctx.getResources()
				.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			id = R.drawable.placeholder_144x240;
		} else {
			id = R.drawable.placeholder_256x160;
		}
		return id;
	}

	@Override
	public int getCount() {
		return uris.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		final Context context = V8Engine.getContext();

		final View rootView = LayoutInflater.from(context)
				.inflate(R.layout.row_item_screenshots_big, null);

		final ImageView imageView = (ImageView) rootView.findViewById(R.id.screenshot_image_big);

		ImageLoader.load(uris.get(position), getPlaceholder(context), imageView);

		container.addView(rootView);

		return rootView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object); // ??
	}
}
