/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.ListSearchApps;
import cm.aptoide.pt.model.v7.Malware;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.util.FragmentUtils;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SearchDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by neuro on 01-06-2016.
 */
@Displayables({ SearchDisplayable.class }) public class SearchWidget
    extends Widget<SearchDisplayable> {

  private final SimpleDateFormat dateFormatter =
      new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
  private TextView nameTextView;
  private ImageView iconImageView;
  private TextView downloadsTextView;
  private RatingBar ratingBar;
  private ImageView overflowImageView;
  private TextView timeTextView;
  private TextView storeTextView;
  private ImageView icTrustedImageView;
  private View bottomView;

  public SearchWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    nameTextView = (TextView) itemView.findViewById(R.id.name);
    iconImageView = (ImageView) itemView.findViewById(R.id.icon);
    downloadsTextView = (TextView) itemView.findViewById(R.id.downloads);
    ratingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);
    overflowImageView = (ImageView) itemView.findViewById(R.id.overflow);
    timeTextView = (TextView) itemView.findViewById(R.id.search_time);
    storeTextView = (TextView) itemView.findViewById(R.id.search_store);
    icTrustedImageView = (ImageView) itemView.findViewById(R.id.ic_trusted_search);
    bottomView = itemView.findViewById(R.id.bottom_view);
  }

  @Override public void bindView(SearchDisplayable displayable) {

    ListSearchApps.SearchAppsApp pojo = displayable.getPojo();

    overflowImageView.setOnClickListener(view -> {
      final PopupMenu popup = new PopupMenu(view.getContext(), view);
      MenuInflater inflater = popup.getMenuInflater();
      inflater.inflate(R.menu.menu_search_item, popup.getMenu());
      MenuItem menuItem = popup.getMenu().findItem(R.id.versions);
      menuItem.setVisible(pojo.isHasVersions());
      menuItem.setOnMenuItemClickListener(menuItem1 -> {

        ListSearchApps.SearchAppsApp searchAppsApp = displayable.getPojo();
        String name = searchAppsApp.getName();
        String icon = searchAppsApp.getIcon();
        String packageName = searchAppsApp.getPackageName();

        FragmentUtils.replaceFragmentV4(getContext(),
            V8Engine.getFragmentProvider().newOtherVersionsFragment(name, icon, packageName));
        return true;
      });
      menuItem = popup.getMenu().findItem(R.id.go_to_store);
      menuItem.setOnMenuItemClickListener(menuItem12 -> {
        FragmentUtils.replaceFragmentV4(getContext(), V8Engine.getFragmentProvider()
            .newStoreFragment(pojo.getStore().getName(),
                pojo.getStore().getAppearance().getTheme()));
        return true;
      });

      popup.show();
    });

    nameTextView.setText(pojo.getName());
    String downloadNumber = AptoideUtils.StringU.withSuffix(pojo.getStats().getDownloads())
        + " "
        + bottomView.getContext().getString(R.string.downloads);
    downloadsTextView.setText(downloadNumber);

    float avg = pojo.getStats().getRating().getAvg();
    if (avg <= 0) {
      ratingBar.setVisibility(View.GONE);
    } else {
      ratingBar.setVisibility(View.VISIBLE);
      ratingBar.setRating(avg);
    }

    Date modified = pojo.getModified();
    if (modified != null) {
      String timeSinceUpdate = AptoideUtils.DateTimeU.getInstance(itemView.getContext())
          .getTimeDiffAll(itemView.getContext(), modified.getTime());
      if (timeSinceUpdate != null && !timeSinceUpdate.equals("")) {
        timeTextView.setText(timeSinceUpdate);
      }
    }

    final StoreThemeEnum theme = StoreThemeEnum.get(pojo.getStore().getAppearance().getTheme());

    Drawable background = bottomView.getBackground();
    if (background instanceof ShapeDrawable) {
      ((ShapeDrawable) background).getPaint()
          .setColor(itemView.getContext().getResources().getColor(theme.getStoreHeader()));
    } else if (background instanceof GradientDrawable) {
      ((GradientDrawable) background).setColor(
          itemView.getContext().getResources().getColor(theme.getStoreHeader()));
    }

    background = storeTextView.getBackground();
    if (background instanceof ShapeDrawable) {
      ((ShapeDrawable) background).getPaint()
          .setColor(itemView.getContext().getResources().getColor(theme.getStoreHeader()));
    } else if (background instanceof GradientDrawable) {
      ((GradientDrawable) background).setColor(
          itemView.getContext().getResources().getColor(theme.getStoreHeader()));
    }

    storeTextView.setText(pojo.getStore().getName());
    ImageLoader.load(pojo.getIcon(), iconImageView);

    if (Malware.Rank.TRUSTED.equals(pojo.getFile().getMalware().getRank())) {
      icTrustedImageView.setVisibility(View.VISIBLE);
    } else {
      icTrustedImageView.setVisibility(View.GONE);
    }

    itemView.setOnClickListener(v -> FragmentUtils.replaceFragmentV4(getContext(),
        V8Engine.getFragmentProvider()
            .newAppViewFragment(pojo.getId(), pojo.getStore().getAppearance().getTheme(),
                pojo.getStore().getName())));
  }

  @Override public void onViewDetached() {

  }
}
