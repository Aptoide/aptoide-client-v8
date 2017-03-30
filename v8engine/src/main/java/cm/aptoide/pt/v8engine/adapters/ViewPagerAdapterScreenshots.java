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
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import java.util.ArrayList;

/**
 * Code migrated form v7
 */
public class ViewPagerAdapterScreenshots extends PagerAdapter {

  private final ArrayList<String> uris;

  public ViewPagerAdapterScreenshots(ArrayList<String> uris) {
    this.uris = uris;
  }

  @Override public int getCount() {
    return uris.size();
  }

  @Override public Object instantiateItem(ViewGroup container, final int position) {
    final Context context = container.getContext();

    final View rootView =
        LayoutInflater.from(context).inflate(R.layout.row_item_screenshots_big, null);

    final ImageView imageView = (ImageView) rootView.findViewById(R.id.screenshot_image_big);

    ImageLoader.with(context).load(uris.get(position), getPlaceholder(context), imageView);

    container.addView(rootView);

    return rootView;
  }

  @Override public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View) object);
  }

  @Override public boolean isViewFromObject(View view, Object object) {
    return view.equals(object); // ??
  }

  private int getPlaceholder(Context context) {
    int id;
    if (context.getResources().getConfiguration().orientation
        == Configuration.ORIENTATION_PORTRAIT) {
      id = R.drawable.placeholder_9_16;
    } else {
      id = R.drawable.placeholder_16_9;
    }
    return id;
  }
}
