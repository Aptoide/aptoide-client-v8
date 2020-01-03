/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.app.view.screenshots;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.AttrRes;
import androidx.viewpager.widget.PagerAdapter;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
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

    final View rootView = LayoutInflater.from(context)
        .inflate(R.layout.row_item_screenshots_big, null);

    final ImageView imageView = (ImageView) rootView.findViewById(R.id.screenshot_image_big);

    ImageLoader.with(context)
        .load(uris.get(position), getPlaceholder(context), imageView);

    container.addView(rootView);

    return rootView;
  }

  @Override public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View) object);
  }

  @Override public boolean isViewFromObject(View view, Object object) {
    return view.equals(object); // ??
  }

  private @AttrRes int getPlaceholder(Context context) {
    int id;
    if (context.getResources()
        .getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
      id = R.attr.placeholder_9_16;
    } else {
      id = R.attr.placeholder_16_9;
    }
    return id;
  }
}
