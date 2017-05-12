package cm.aptoide.pt.v8engine.view.search;

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
import cm.aptoide.pt.v8engine.store.StoreThemeEnum;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxMenuItem;
import com.jakewharton.rxbinding.view.RxView;
import java.util.Date;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by neuro on 01-06-2016.
 */
@Displayables({ SearchDisplayable.class }) public class SearchWidget
    extends Widget<SearchDisplayable> {

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
    ListSearchApps.SearchAppsApp searchAppsApp = displayable.getPojo();

    final Action0 clickCallback = displayable.getClickCallback();
    final Action1<Void> clickToOpenStore =
        __ -> handleClickToOpenPopupMenu(clickCallback, overflowImageView, searchAppsApp);
    compositeSubscription.add(RxView.clicks(overflowImageView)
        .subscribe(clickToOpenStore));

    nameTextView.setText(searchAppsApp.getName());
    String downloadNumber = AptoideUtils.StringU.withSuffix(searchAppsApp.getStats()
        .getPdownloads()) + " " + bottomView.getContext()
        .getString(R.string.downloads);
    downloadsTextView.setText(downloadNumber);

    float avg = searchAppsApp.getStats()
        .getRating()
        .getAvg();
    if (avg <= 0) {
      ratingBar.setVisibility(View.GONE);
    } else {
      ratingBar.setVisibility(View.VISIBLE);
      ratingBar.setRating(avg);
    }

    Date modified = searchAppsApp.getModified();
    if (modified != null) {
      String timeSinceUpdate = AptoideUtils.DateTimeU.getInstance(itemView.getContext())
          .getTimeDiffAll(itemView.getContext(), modified.getTime());
      if (timeSinceUpdate != null && !timeSinceUpdate.equals("")) {
        timeTextView.setText(timeSinceUpdate);
      }
    }

    final StoreThemeEnum theme = StoreThemeEnum.get(searchAppsApp.getStore()
        .getAppearance()
        .getTheme());

    Drawable background = bottomView.getBackground();
    if (background instanceof ShapeDrawable) {
      ((ShapeDrawable) background).getPaint()
          .setColor(itemView.getContext()
              .getResources()
              .getColor(theme.getStoreHeader()));
    } else if (background instanceof GradientDrawable) {
      ((GradientDrawable) background).setColor(itemView.getContext()
          .getResources()
          .getColor(theme.getStoreHeader()));
    }

    background = storeTextView.getBackground();
    if (background instanceof ShapeDrawable) {
      ((ShapeDrawable) background).getPaint()
          .setColor(itemView.getContext()
              .getResources()
              .getColor(theme.getStoreHeader()));
    } else if (background instanceof GradientDrawable) {
      ((GradientDrawable) background).setColor(itemView.getContext()
          .getResources()
          .getColor(theme.getStoreHeader()));
    }

    storeTextView.setText(searchAppsApp.getStore()
        .getName());
    ImageLoader.with(getContext())
        .load(searchAppsApp.getIcon(), iconImageView);

    if (Malware.Rank.TRUSTED.equals(searchAppsApp.getFile()
        .getMalware()
        .getRank())) {
      icTrustedImageView.setVisibility(View.VISIBLE);
    } else {
      icTrustedImageView.setVisibility(View.GONE);
    }

    final Action1<Void> clickToOpenAppView =
        v -> handleClickToOpenAppView(clickCallback, searchAppsApp);
    compositeSubscription.add(RxView.clicks(itemView)
        .subscribe(clickToOpenAppView));
  }

  private void handleClickToOpenPopupMenu(Action0 clickCallback, View view,
      ListSearchApps.SearchAppsApp searchAppsApp) {

    final PopupMenu popup = new PopupMenu(view.getContext(), view);
    MenuInflater inflater = popup.getMenuInflater();
    inflater.inflate(R.menu.menu_search_item, popup.getMenu());

    MenuItem menuItemVersions = popup.getMenu()
        .findItem(R.id.versions);
    if (searchAppsApp.isHasVersions()) {
      menuItemVersions.setVisible(true);
      compositeSubscription.add(RxMenuItem.clicks(menuItemVersions)
          .subscribe(aVoid -> {
            if (clickCallback != null) {
              clickCallback.call();
            }

            String name = searchAppsApp.getName();
            String icon = searchAppsApp.getIcon();
            String packageName = searchAppsApp.getPackageName();

            getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
                .newOtherVersionsFragment(name, icon, packageName));
          }));
    }

    MenuItem menuItemGoToStore = popup.getMenu()
        .findItem(R.id.go_to_store);
    compositeSubscription.add(RxMenuItem.clicks(menuItemGoToStore)
        .subscribe(__ -> {
          if (clickCallback != null) {
            clickCallback.call();
          }
          getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
              .newStoreFragment(searchAppsApp.getStore()
                  .getName(), searchAppsApp.getStore()
                  .getAppearance()
                  .getTheme()));
        }));

    popup.show();
  }

  private void handleClickToOpenAppView(Action0 clickCallback,
      ListSearchApps.SearchAppsApp searchAppsApp) {
    if (clickCallback != null) {
      clickCallback.call();
    }
    getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
        .newAppViewFragment(searchAppsApp.getId(), searchAppsApp.getPackageName(),
            searchAppsApp.getStore()
                .getAppearance()
                .getTheme(), searchAppsApp.getStore()
                .getName()));
  }
}
