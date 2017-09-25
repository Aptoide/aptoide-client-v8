package cm.aptoide.pt.view.search.result;

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
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.dataprovider.model.v7.Malware;
import cm.aptoide.pt.dataprovider.model.v7.search.SearchApp;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.search.SearchAnalytics;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.recycler.widget.Widget;
import com.facebook.appevents.AppEventsLogger;
import com.jakewharton.rxbinding.view.RxMenuItem;
import com.jakewharton.rxbinding.view.RxView;
import java.util.Date;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by neuro on 01-06-2016.
 */
@Deprecated
public class SearchWidget extends Widget<SearchDisplayable> {

  private TextView nameTextView;
  private ImageView iconImageView;
  private TextView downloadsTextView;
  private RatingBar ratingBar;
  private ImageView overflowImageView;
  private TextView timeTextView;
  private TextView storeTextView;
  private ImageView icTrustedImageView;
  private View bottomView;

  private SearchAnalytics searchAnalytics;

  public SearchWidget(View itemView) {
    super(itemView);
    searchAnalytics = new SearchAnalytics(Analytics.getInstance(),
        AppEventsLogger.newLogger(getContext().getApplicationContext()));
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
    SearchApp searchApp = displayable.getPojo();

    final Action0 clickCallback = displayable.getClickCallback();
    final Action1<Void> clickToOpenStore =
        __ -> handleClickToOpenPopupMenu(clickCallback, overflowImageView, searchApp);
    compositeSubscription.add(RxView.clicks(overflowImageView)
        .subscribe(clickToOpenStore));

    nameTextView.setText(searchApp.getName());
    String downloadNumber = AptoideUtils.StringU.withSuffix(searchApp.getStats()
        .getPdownloads()) + " " + bottomView.getContext()
        .getString(R.string.downloads);
    downloadsTextView.setText(downloadNumber);

    float avg = searchApp.getStats()
        .getRating()
        .getAvg();
    if (avg <= 0) {
      ratingBar.setVisibility(View.GONE);
    } else {
      ratingBar.setVisibility(View.VISIBLE);
      ratingBar.setRating(avg);
    }

    Date modified = searchApp.getModified();
    if (modified != null) {
      String timeSinceUpdate = AptoideUtils.DateTimeU.getInstance(itemView.getContext())
          .getTimeDiffAll(itemView.getContext(), modified.getTime(), getContext().getResources());
      if (timeSinceUpdate != null && !timeSinceUpdate.equals("")) {
        timeTextView.setText(timeSinceUpdate);
      }
    }

    final StoreTheme theme = StoreTheme.get(searchApp.getStore()
        .getAppearance()
        .getTheme());

    Drawable background = bottomView.getBackground();
    if (background instanceof ShapeDrawable) {
      ((ShapeDrawable) background).getPaint()
          .setColor(itemView.getContext()
              .getResources()
              .getColor(theme.getPrimaryColor()));
    } else if (background instanceof GradientDrawable) {
      ((GradientDrawable) background).setColor(itemView.getContext()
          .getResources()
          .getColor(theme.getPrimaryColor()));
    }

    background = storeTextView.getBackground();
    if (background instanceof ShapeDrawable) {
      ((ShapeDrawable) background).getPaint()
          .setColor(itemView.getContext()
              .getResources()
              .getColor(theme.getPrimaryColor()));
    } else if (background instanceof GradientDrawable) {
      ((GradientDrawable) background).setColor(itemView.getContext()
          .getResources()
          .getColor(theme.getPrimaryColor()));
    }

    storeTextView.setText(searchApp.getStore()
        .getName());
    ImageLoader.with(getContext())
        .load(searchApp.getIcon(), iconImageView);

    if (Malware.Rank.TRUSTED.equals(searchApp.getFile()
        .getMalware()
        .getRank())) {
      icTrustedImageView.setVisibility(View.VISIBLE);
    } else {
      icTrustedImageView.setVisibility(View.GONE);
    }

    final Action1<Void> clickToOpenAppView =
        v -> handleClickToOpenAppView(clickCallback, searchApp, displayable.getQuery());
    compositeSubscription.add(RxView.clicks(itemView)
        .subscribe(clickToOpenAppView));
  }

  private void handleClickToOpenPopupMenu(Action0 clickCallback, View view,
      SearchApp searchApp) {

    final PopupMenu popup = new PopupMenu(view.getContext(), view);
    MenuInflater inflater = popup.getMenuInflater();
    inflater.inflate(R.menu.menu_search_item, popup.getMenu());

    MenuItem menuItemVersions = popup.getMenu()
        .findItem(R.id.versions);
    if (searchApp.hasVersions()) {
      menuItemVersions.setVisible(true);
      compositeSubscription.add(RxMenuItem.clicks(menuItemVersions)
          .subscribe(aVoid -> {
            if (clickCallback != null) {
              clickCallback.call();
            }

            String name = searchApp.getName();
            String icon = searchApp.getIcon();
            String packageName = searchApp.getPackageName();

            getFragmentNavigator().navigateTo(AptoideApplication.getFragmentProvider()
                .newOtherVersionsFragment(name, icon, packageName), true);
          }));
    }

    MenuItem menuItemGoToStore = popup.getMenu()
        .findItem(R.id.go_to_store);
    compositeSubscription.add(RxMenuItem.clicks(menuItemGoToStore)
        .subscribe(__ -> {
          if (clickCallback != null) {
            clickCallback.call();
          }
          getFragmentNavigator().navigateTo(AptoideApplication.getFragmentProvider()
              .newStoreFragment(searchApp.getStore()
                  .getName(), searchApp.getStore()
                  .getAppearance()
                  .getTheme()), true);
        }));

    popup.show();
  }

  private void handleClickToOpenAppView(Action0 clickCallback,
      SearchApp searchApp, String query) {
    if (clickCallback != null) {
      clickCallback.call();
    }

    searchAnalytics.searchAppClick(query, searchApp.getPackageName());
    getFragmentNavigator().navigateTo(AptoideApplication.getFragmentProvider()
        .newAppViewFragment(searchApp.getId(), searchApp.getPackageName(),
            searchApp.getStore()
                .getAppearance()
                .getTheme(), searchApp.getStore()
                .getName()), true);
  }
}
