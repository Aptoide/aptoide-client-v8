package cm.aptoide.pt.v8engine.social.view.viewholder;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LongSparseArray;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.FollowStoreCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.SocialHeaderCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.SocialStore;
import cm.aptoide.pt.v8engine.social.data.StoreAppCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.StoreCardTouchEvent;
import cm.aptoide.pt.v8engine.timeline.view.LikeButtonView;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.HashMap;
import java.util.Map;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 28/06/2017.
 */

public class SocialStoreViewHolder extends CardViewHolder<SocialStore> {
  private final DateCalculator dateCalculator;
  private final SpannableFactory spannableFactory;
  private final LayoutInflater inflater;
  private final ImageView headerPrimaryAvatar;
  private final ImageView headerSecondaryAvatar;
  private final TextView headerPrimaryName;
  private final TextView headerSecondaryName;
  private final TextView timestamp;
  private final TextView storeNameBodyHeader;
  private final LinearLayout appsContainer;
  private final TextView storeNameFollow;
  private final ImageView storeAvatarFollow;
  private final TextView storeNumberFollowers;
  private final TextView storeNumberApps;
  private final Button followStoreButton;
  private final RelativeLayout cardHeader;
  private final LinearLayout like;
  private final LikeButtonView likeButton;
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final TextView commentButton;
  private final TextView shareButton;

  public SocialStoreViewHolder(View view,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject, DateCalculator dateCalculator,
      SpannableFactory spannableFactory) {
    super(view);
    this.inflater = LayoutInflater.from(itemView.getContext());
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.headerPrimaryAvatar = (ImageView) view.findViewById(R.id.card_image);
    this.headerSecondaryAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
    this.headerPrimaryName = (TextView) view.findViewById(R.id.card_title);
    this.headerSecondaryName = (TextView) view.findViewById(R.id.card_subtitle);
    this.timestamp = (TextView) view.findViewById(R.id.card_date);
    this.storeNameBodyHeader = (TextView) view.findViewById(R.id.social_shared_store_name);
    this.appsContainer = (LinearLayout) view.findViewById(
        R.id.displayable_social_timeline_store_latest_apps_container);
    this.storeAvatarFollow = (ImageView) view.findViewById(R.id.social_shared_store_avatar);
    this.storeNameFollow = (TextView) view.findViewById(R.id.store_name);
    this.storeNumberFollowers = (TextView) view.findViewById(R.id.social_number_of_followers_text);
    this.storeNumberApps = (TextView) view.findViewById(R.id.social_number_of_apps_text);
    this.followStoreButton = (Button) view.findViewById(R.id.follow_btn);
    this.cardHeader = (RelativeLayout) view.findViewById(R.id.social_header);
    this.likeButton = (LikeButtonView) itemView.findViewById(R.id.social_like_button);
    this.like = (LinearLayout) itemView.findViewById(R.id.social_like);
    this.commentButton = (TextView) itemView.findViewById(R.id.social_comment);
    this.shareButton = (TextView) itemView.findViewById(R.id.social_share);
  }

  @Override public void setCard(SocialStore card, int position) {
    ImageLoader.with(itemView.getContext())
        .loadWithShadowCircleTransform(card.getPoster()
            .getPrimaryAvatar(), this.headerPrimaryAvatar);
    ImageLoader.with(itemView.getContext())
        .loadWithShadowCircleTransform(card.getPoster()
            .getSecondaryAvatar(), this.headerSecondaryAvatar);
    this.headerPrimaryName.setText(getStyledStoreName(card));
    this.headerSecondaryName.setText(card.getPoster()
        .getSecondaryName());
    this.timestamp.setText(
        dateCalculator.getTimeSinceDate(itemView.getContext(), card.getLatestUpdate()));
    this.storeNameBodyHeader.setText(card.getStoreName());
    ImageLoader.with(itemView.getContext())
        .load(card.getStoreAvatar(), storeAvatarFollow);
    this.storeNameFollow.setText(card.getStoreName());
    this.storeNumberFollowers.setText(String.valueOf(card.getSubscribers()));
    this.storeNumberApps.setText(String.valueOf(card.getAppsNumber()));
    this.cardHeader.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new SocialHeaderCardTouchEvent(card, card.getPoster()
            .getStore()
            .getName(), card.getPoster()
            .getStore()
            .getStoreTheme(), card.getPoster()
            .getUser()
            .getId(), CardTouchEvent.Type.HEADER)));
    showStoreLatestApps(card);
    this.followStoreButton.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new FollowStoreCardTouchEvent(card, card.getStoreId(), card.getStoreName(),
            CardTouchEvent.Type.BODY)));
    this.storeAvatarFollow.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new StoreCardTouchEvent(card, card.getStoreName(), card.getStoreTheme(),
            CardTouchEvent.Type.BODY)));
    if (card.isLiked()) {
      likeButton.setHeartState(true);
    } else {
      likeButton.setHeartState(false);
    }
    this.like.setOnClickListener(click -> this.likeButton.performClick());

    this.likeButton.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.LIKE)));
    this.commentButton.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.COMMENT)));
    this.shareButton.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.SHARE)));
  }

  @NonNull private Spannable getStyledStoreName(SocialStore card) {
    return spannableFactory.createColorSpan(itemView.getContext()
            .getString(R.string.store_has_new_apps, card.getPoster()
                .getPrimaryName()),
        ContextCompat.getColor(itemView.getContext(), R.color.black_87_alpha), card.getPoster()
            .getPrimaryName());
  }

  private void showStoreLatestApps(SocialStore card) {
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

  private void setStoreLatestAppsListeners(SocialStore card, Map<View, Long> apps,
      LongSparseArray<String> appsPackages) {
    for (View app : apps.keySet()) {
      app.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
          new StoreAppCardTouchEvent(card, CardTouchEvent.Type.BODY,
              appsPackages.get(apps.get(app)))));
    }
  }
}
