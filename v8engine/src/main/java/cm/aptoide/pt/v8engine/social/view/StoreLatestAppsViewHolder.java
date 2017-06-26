package cm.aptoide.pt.v8engine.social.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LongSparseArray;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.image.ImageLoader;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.StoreAppCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.StoreLatestApps;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 21/06/2017.
 */

public class StoreLatestAppsViewHolder extends CardViewHolder<StoreLatestApps> {
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final DateCalculator dateCalculator;
  private final ImageView headerIcon;
  private final TextView headerTitle;
  private final TextView headerSubtitle;
  private final LinearLayout appsContainer;
  private final LayoutInflater inflater;
  private final SpannableFactory spannableFactory;

  public StoreLatestAppsViewHolder(View view,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject, DateCalculator dateCalculator,
      SpannableFactory spannableFactory) {
    super(view);
    this.spannableFactory = spannableFactory;
    inflater = LayoutInflater.from(itemView.getContext());
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.dateCalculator = dateCalculator;

    this.headerIcon = (ImageView) view.findViewById(
        R.id.displayable_social_timeline_store_latest_apps_card_image);
    this.headerTitle =
        (TextView) view.findViewById(R.id.displayable_social_timeline_store_latest_apps_card_title);
    this.headerSubtitle = (TextView) view.findViewById(
        R.id.displayable_social_timeline_store_latest_apps_card_subtitle);
    this.appsContainer = (LinearLayout) itemView.findViewById(
        R.id.displayable_social_timeline_store_latest_apps_container);
  }

  @Override void setCard(StoreLatestApps card, int position) {
    ImageLoader.with(itemView.getContext())
        .loadWithShadowCircleTransform(card.getStoreAvatar(), headerIcon);

    headerTitle.setText(getStyledTitle(itemView.getContext(), card.getStoreName()));
    headerSubtitle.setText(getTimeSinceLastUpdate(itemView.getContext(), card.getLatestUpdate()));
    showStoreLatestApps(card);
  }

  public String getTimeSinceLastUpdate(Context context, Date latestUpdate) {
    return dateCalculator.getTimeSinceDate(context, latestUpdate);
  }

  public Spannable getStyledTitle(Context context, String storeName) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.store_has_new_apps, storeName),
        ContextCompat.getColor(context, R.color.black_87_alpha), storeName);
  }

  private void showStoreLatestApps(StoreLatestApps card) {
    Map<View, Long> apps = new HashMap<>();
    LongSparseArray<String> appsPackages = new LongSparseArray<>();

    appsContainer.removeAllViews();
    View latestAppView;
    ImageView latestAppIcon;
    TextView latestAppName;
    for (App latestApp : card.getApps()) {
      latestAppView = inflater.inflate(R.layout.social_timeline_latest_app, appsContainer, false);
      latestAppIcon = (ImageView) latestAppView.findViewById(R.id.social_timeline_latest_app_icon);
      latestAppName = (TextView) latestAppView.findViewById(R.id.social_timeline_latest_app_name);
      ImageLoader.with(itemView.getContext())
          .load(latestApp.getIcon(), latestAppIcon);
      latestAppName.setText(latestApp.getName());
      appsContainer.addView(latestAppView);
      apps.put(latestAppView, latestApp.getId());
      appsPackages.put(latestApp.getId(), latestApp.getPackageName());
    }

    setStoreLatestAppsListeners(card, apps, appsPackages);
  }

  private void setStoreLatestAppsListeners(StoreLatestApps card, Map<View, Long> apps,
      LongSparseArray<String> appsPackages) {
    for (View app : apps.keySet()) {
      app.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
          new StoreAppCardTouchEvent(card, CardTouchEvent.Type.BODY,
              appsPackages.get(apps.get(app)))));
    }
  }
}
