package cm.aptoide.pt.v8engine.social.view.viewholder;

import android.support.v4.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.AggregatedStore;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.FollowStoreCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.MinimalCardViewFactory;
import cm.aptoide.pt.v8engine.social.data.StoreAppCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.StoreCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.publisher.Poster;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 30/06/2017.
 */

public class AggregatedStoreViewHolder extends PostViewHolder<AggregatedStore> {
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final DateCalculator dateCalculator;
  private final SpannableFactory spannableFactory;
  private final MinimalCardViewFactory minimalCardViewFactory;
  private final LayoutInflater inflater;
  private final ImageView headerAvatar1;
  private final ImageView headerAvatar2;
  private final TextView headerNames;
  private final TextView headerTimestamp;
  private final TextView storeNameBodyHeader;
  private final LinearLayout appsContainer;
  private final ImageView storeAvatarFollow;
  private final TextView storeNameFollow;
  private final TextView storeNumberFollowers;
  private final TextView storeNumberApps;
  private final Button followStoreButton;
  private final TextView morePostersLabel;
  private final FrameLayout minimalCardContainer;

  public AggregatedStoreViewHolder(View view,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject, DateCalculator dateCalculator,
      SpannableFactory spannableFactory, MinimalCardViewFactory minimalCardViewFactory) {
    super(view);
    this.inflater = LayoutInflater.from(itemView.getContext());
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;
    this.minimalCardViewFactory = minimalCardViewFactory;
    this.headerAvatar1 = (ImageView) view.findViewById(R.id.card_header_avatar_1);
    this.headerAvatar2 = (ImageView) view.findViewById(R.id.card_header_avatar_2);
    this.headerNames = (TextView) view.findViewById(R.id.card_title);
    this.headerTimestamp = (TextView) view.findViewById(R.id.card_date);
    this.storeNameBodyHeader = (TextView) view.findViewById(R.id.social_shared_store_name);
    this.appsContainer = (LinearLayout) view.findViewById(
        R.id.displayable_social_timeline_store_latest_apps_container);
    this.storeAvatarFollow = (ImageView) view.findViewById(R.id.social_shared_store_avatar);
    this.storeNameFollow = (TextView) view.findViewById(R.id.store_name);
    this.storeNumberFollowers = (TextView) view.findViewById(R.id.social_number_of_followers_text);
    this.storeNumberApps = (TextView) view.findViewById(R.id.social_number_of_apps_text);
    this.followStoreButton = (Button) view.findViewById(R.id.follow_btn);
    this.morePostersLabel =
        (TextView) itemView.findViewById(R.id.timeline_header_aditional_number_of_shares_circular);
    this.minimalCardContainer =
        (FrameLayout) itemView.findViewById(R.id.timeline_sub_minimal_card_container);
  }

  @Override public void setPost(AggregatedStore card, int position) {
    if (card.getPosters() != null) {
      if (card.getPosters()
          .size() > 0) {
        ImageLoader.with(itemView.getContext())
            .loadWithShadowCircleTransform(card.getPosters()
                .get(0)
                .getPrimaryAvatar(), this.headerAvatar1);
      }
      if (card.getPosters()
          .size() > 1) {
        ImageLoader.with(itemView.getContext())
            .loadWithShadowCircleTransform(card.getPosters()
                .get(1)
                .getPrimaryAvatar(), this.headerAvatar2);
      }
    }
    this.headerNames.setText(getCardHeaderNames(card));
    this.headerTimestamp.setText(
        dateCalculator.getTimeSinceDate(itemView.getContext(), card.getLatestUpdate()));
    this.storeNameBodyHeader.setText(card.getStoreName());
    ImageLoader.with(itemView.getContext())
        .load(card.getStoreAvatar(), storeAvatarFollow);
    this.storeNameFollow.setText(card.getStoreName());
    this.storeNumberFollowers.setText(String.valueOf(card.getSubscribers()));
    this.storeNumberApps.setText(String.valueOf(card.getAppsNumber()));
    showStoreLatestApps(card);
    showMorePostersLabel(card);
    minimalCardContainer.removeAllViews();
    minimalCardContainer.addView(minimalCardViewFactory.getView(card, card.getMinimalPosts(),
        MinimalCardViewFactory.MINIMUM_NUMBER_OF_VISILIBE_MINIMAL_CARDS, inflater,
        itemView.getContext(), position));

    this.followStoreButton.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new FollowStoreCardTouchEvent(card, card.getStoreId(), card.getStoreName(),
            CardTouchEvent.Type.BODY)));
    this.storeAvatarFollow.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new StoreCardTouchEvent(card, card.getStoreName(), card.getStoreTheme(),
            CardTouchEvent.Type.BODY)));
  }

  public String getCardHeaderNames(AggregatedStore card) {
    StringBuilder headerNamesStringBuilder = new StringBuilder();
    if (card.getPosters()
        .size() >= 2) {
      List<Poster> posters = card.getPosters()
          .subList(0, 2);
      for (Poster poster : posters) {
        headerNamesStringBuilder.append(poster.getPrimaryName())
            .append(", ");
      }
      headerNamesStringBuilder.setLength(headerNamesStringBuilder.length() - 2);
    }
    return headerNamesStringBuilder.toString();
  }

  private void showStoreLatestApps(AggregatedStore card) {
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

  private void showMorePostersLabel(AggregatedStore card) {
    if (card.getPosters()
        .size() > 2) {
      morePostersLabel.setText(String.format(itemView.getContext()
          .getString(R.string.timeline_short_plus), String.valueOf(card.getPosters()
          .size() - 2)));
      morePostersLabel.setVisibility(View.VISIBLE);
    } else {
      morePostersLabel.setVisibility(View.INVISIBLE);
    }
  }

  private void setStoreLatestAppsListeners(AggregatedStore card, Map<View, Long> apps,
      LongSparseArray<String> appsPackages) {
    for (View app : apps.keySet()) {
      app.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
          new StoreAppCardTouchEvent(card, CardTouchEvent.Type.BODY,
              appsPackages.get(apps.get(app)))));
    }
  }
}
