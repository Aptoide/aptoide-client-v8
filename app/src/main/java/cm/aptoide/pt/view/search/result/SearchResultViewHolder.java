package cm.aptoide.pt.view.search.result;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.Malware;
import cm.aptoide.pt.dataprovider.model.v7.search.SearchApp;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.utils.AptoideUtils;
import com.jakewharton.rxbinding.support.v7.widget.RxPopupMenu;
import com.jakewharton.rxbinding.view.RxView;
import java.util.Date;
import rx.Observable;
import rx.subjects.PublishSubject;

public class SearchResultViewHolder extends RecyclerView.ViewHolder {

  public static final int LAYOUT = R.layout.search_app_row;
  private final PublishSubject<OtherVersionsData> onOtherVersionsClickSubject;
  private final PublishSubject<StoreData> onOpenStoreClickSubject;

  private TextView nameTextView;
  private ImageView iconImageView;
  private TextView downloadsTextView;
  private RatingBar ratingBar;
  private TextView timeTextView;
  private TextView storeTextView;
  private ImageView icTrustedImageView;
  private ImageView overflowImageView;
  private View bottomView;
  private SearchApp searchApp;
  private String query;

  public SearchResultViewHolder(View itemView) {
    super(itemView);
    this.onOtherVersionsClickSubject = PublishSubject.create();
    this.onOpenStoreClickSubject = PublishSubject.create();
    bindViews(itemView);
  }

  public void setupWith(SearchApp searchApp, String query) {
    this.searchApp = searchApp;
    this.query = query;
    setAppName();
    setDownloadCount();
    setAverageValue();
    setDateModified();
    setBackground();
    setStoreName();
    setIconView();
    setTrustedBadge();
  }

  private void setTrustedBadge() {
    if (Malware.Rank.TRUSTED.equals(searchApp.getFile()
        .getMalware()
        .getRank())) {
      icTrustedImageView.setVisibility(View.VISIBLE);
    } else {
      icTrustedImageView.setVisibility(View.GONE);
    }
  }

  private void setIconView() {
    ImageLoader.with(iconImageView.getContext())
        .load(searchApp.getIcon(), iconImageView);
  }

  private void setStoreName() {
    storeTextView.setText(searchApp.getStore()
        .getName());
  }

  private void setBackground() {
    final Resources resources = itemView.getResources();
    final StoreTheme theme = StoreTheme.get(searchApp.getStore()
        .getAppearance()
        .getTheme());
    Drawable background = bottomView.getBackground();

    if (background instanceof ShapeDrawable) {
      ((ShapeDrawable) background).getPaint()
          .setColor(resources.getColor(theme.getPrimaryColor()));
    } else if (background instanceof GradientDrawable) {
      ((GradientDrawable) background).setColor(resources.getColor(theme.getPrimaryColor()));
    }

    background = storeTextView.getBackground();
    if (background instanceof ShapeDrawable) {
      ((ShapeDrawable) background).getPaint()
          .setColor(resources.getColor(theme.getPrimaryColor()));
    } else if (background instanceof GradientDrawable) {
      ((GradientDrawable) background).setColor(resources.getColor(theme.getPrimaryColor()));
    }
  }

  private void setDateModified() {
    Date modified = searchApp.getModified();
    if (modified != null) {
      final Resources resources = itemView.getResources();
      final Context context = itemView.getContext();
      String timeSinceUpdate = AptoideUtils.DateTimeU.getInstance(context)
          .getTimeDiffAll(context, modified.getTime(), resources);
      if (timeSinceUpdate != null && !timeSinceUpdate.equals("")) {
        timeTextView.setText(timeSinceUpdate);
      }
    }
  }

  private void setAverageValue() {
    float avg = searchApp.getStats()
        .getRating()
        .getAvg();
    if (avg <= 0) {
      ratingBar.setVisibility(View.GONE);
    } else {
      ratingBar.setVisibility(View.VISIBLE);
      ratingBar.setRating(avg);
    }
  }

  private void setDownloadCount() {
    String downloadNumber = AptoideUtils.StringU.withSuffix(searchApp.getStats()
        .getPdownloads()) + " " + bottomView.getContext()
        .getString(R.string.downloads);
    downloadsTextView.setText(downloadNumber);
  }

  private void setAppName() {
    nameTextView.setText(searchApp.getName());
  }

  private void bindViews(View itemView) {
    nameTextView = (TextView) itemView.findViewById(R.id.name);
    iconImageView = (ImageView) itemView.findViewById(R.id.icon);
    downloadsTextView = (TextView) itemView.findViewById(R.id.downloads);
    ratingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);
    timeTextView = (TextView) itemView.findViewById(R.id.search_time);
    storeTextView = (TextView) itemView.findViewById(R.id.search_store);
    icTrustedImageView = (ImageView) itemView.findViewById(R.id.ic_trusted_search);
    bottomView = itemView.findViewById(R.id.bottom_view);
    overflowImageView = (ImageView) itemView.findViewById(R.id.overflow);
  }

  public Observable<Pair<SearchApp, String>> onOpenAppViewClick() {
    return RxView.clicks(itemView)
        .map(__ -> new Pair<>(searchApp, query));
  }

  public Observable<SearchApp> onOpenPopupMenuClick() {
    return RxView.clicks(overflowImageView)
        .map(__ -> searchApp);
  }

  public Observable<OtherVersionsData> onOtherVersionsClick() {
    return onOtherVersionsClickSubject.asObservable();
  }

  public Observable<StoreData> onOpenStoreClick() {
    return onOpenStoreClickSubject.asObservable();
  }

  public void showPopup(boolean hasVersions, String appName, String appIcon, String packageName,
      String storeName, String theme) {
    final Context context = itemView.getContext();
    final PopupMenu popupMenu = new PopupMenu(context, itemView);

    MenuInflater inflater = popupMenu.getMenuInflater();
    inflater.inflate(R.menu.menu_search_item, popupMenu.getMenu());

    if (hasVersions) {
      MenuItem menuItemVersions = popupMenu.getMenu()
          .findItem(R.id.versions);
      menuItemVersions.setVisible(true);

    }

    RxPopupMenu.itemClicks(popupMenu)
        .filter(menuItem -> menuItem.getItemId() == R.id.versions)
        .subscribe(__ -> onOtherVersionsClickSubject.onNext(
            new OtherVersionsData(appName, appIcon, packageName)));

    RxPopupMenu.itemClicks(popupMenu)
        .filter(menuItem -> menuItem.getItemId() == R.id.go_to_store)
        .subscribe(__ -> onOpenStoreClickSubject.onNext(new StoreData(storeName, theme)));

    popupMenu.show();
  }

  static final class StoreData {
    private final String storeName;
    private final String theme;

    StoreData(String storeName, String theme) {
      this.storeName = storeName;
      this.theme = theme;
    }

    public String getStoreName() {
      return storeName;
    }

    public String getTheme() {
      return theme;
    }
  }

  static final class OtherVersionsData {
    private final String appName;
    private final String appIcon;
    private final String packageName;

    OtherVersionsData(String appName, String appIcon, String packageName) {
      this.appName = appName;
      this.appIcon = appIcon;
      this.packageName = packageName;
    }

    public String getAppName() {
      return appName;
    }

    public String getAppIcon() {
      return appIcon;
    }

    public String getPackageName() {
      return packageName;
    }
  }
}
