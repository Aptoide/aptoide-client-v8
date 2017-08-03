package cm.aptoide.pt.v8engine.social.view.viewholder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LongSparseArray;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.LikeCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.StoreAppCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.StoreLatestApps;
import cm.aptoide.pt.v8engine.timeline.view.LikeButtonView;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 21/06/2017.
 */

public class StoreLatestAppsViewHolder extends PostViewHolder<StoreLatestApps> {
  private final DateCalculator dateCalculator;
  private final ImageView headerIcon;
  private final TextView headerTitle;
  private final TextView headerSubtitle;
  private final LinearLayout appsContainer;
  private final LayoutInflater inflater;
  private final SpannableFactory spannableFactory;
  private final LinearLayout like;
  private final LikeButtonView likeButton;
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final TextView commentButton;
  private final TextView shareButton;

  public StoreLatestAppsViewHolder(View view,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject, DateCalculator dateCalculator,
      SpannableFactory spannableFactory) {
    super(view);
    this.spannableFactory = spannableFactory;
    this.inflater = LayoutInflater.from(itemView.getContext());
    this.dateCalculator = dateCalculator;
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.headerIcon = (ImageView) view.findViewById(
        R.id.displayable_social_timeline_store_latest_apps_card_image);
    this.headerTitle =
        (TextView) view.findViewById(R.id.displayable_social_timeline_store_latest_apps_card_title);
    this.headerSubtitle = (TextView) view.findViewById(
        R.id.displayable_social_timeline_store_latest_apps_card_subtitle);
    this.appsContainer = (LinearLayout) itemView.findViewById(
        R.id.displayable_social_timeline_store_latest_apps_container);
    this.likeButton = (LikeButtonView) itemView.findViewById(R.id.social_like_button);
    this.like = (LinearLayout) itemView.findViewById(R.id.social_like);
    this.commentButton = (TextView) view.findViewById(R.id.social_comment);
    this.shareButton = (TextView) view.findViewById(R.id.social_share);
  }

  @Override public void setPost(StoreLatestApps card, int position) {
    ImageLoader.with(itemView.getContext())
        .loadWithShadowCircleTransform(card.getStoreAvatar(), headerIcon);

    headerTitle.setText(getStyledTitle(itemView.getContext(), card.getStoreName()));
    headerSubtitle.setText(getTimeSinceLastUpdate(itemView.getContext(), card.getLatestUpdate()));
    showStoreLatestApps(card);
    if (card.isLiked()) {
      if (card.isLikeFromClick()) {
        likeButton.setHeartState(true);
        card.setLikedFromClick(false);
      } else {
        likeButton.setHeartStateWithoutAnimation(true);
      }
    } else {
      likeButton.setHeartState(false);
    }
    this.like.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new LikeCardTouchEvent(card, CardTouchEvent.Type.LIKE, position)));
    this.commentButton.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.COMMENT)));
    this.shareButton.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.SHARE)));
  }

  public Spannable getStyledTitle(Context context, String storeName) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.timeline_title_card_title_has_new_apps_present_singular,
            storeName), ContextCompat.getColor(context, R.color.black_87_alpha), storeName);
  }

  public String getTimeSinceLastUpdate(Context context, Date latestUpdate) {
    return dateCalculator.getTimeSinceDate(context, latestUpdate);
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
