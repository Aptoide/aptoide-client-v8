package cm.aptoide.pt.social.view.viewholder;

import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.repository.StoreRepository;
import cm.aptoide.pt.social.data.AggregatedStore;
import cm.aptoide.pt.social.data.CardTouchEvent;
import cm.aptoide.pt.social.data.FollowStoreCardTouchEvent;
import cm.aptoide.pt.social.data.MinimalCardViewFactory;
import cm.aptoide.pt.social.data.Post;
import cm.aptoide.pt.social.data.PostPopupMenuBuilder;
import cm.aptoide.pt.social.data.StoreAppCardTouchEvent;
import cm.aptoide.pt.social.data.StoreCardTouchEvent;
import cm.aptoide.pt.social.data.publisher.Poster;
import cm.aptoide.pt.util.DateCalculator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 30/06/2017.
 */

public class AggregatedStoreViewHolder extends PostViewHolder<AggregatedStore> {
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final DateCalculator dateCalculator;
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
  private final View overflowMenu;
  private final StoreRepository storeRepository;

  public AggregatedStoreViewHolder(View view,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject, DateCalculator dateCalculator,
      MinimalCardViewFactory minimalCardViewFactory, StoreRepository storeRepository) {
    super(view, cardTouchEventPublishSubject);
    this.storeRepository = storeRepository;
    this.inflater = LayoutInflater.from(itemView.getContext());
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.dateCalculator = dateCalculator;
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
    this.overflowMenu = itemView.findViewById(R.id.overflow_menu);
  }

  @Override public void setPost(AggregatedStore post, int position) {
    if (post.getPosters() != null) {
      if (post.getPosters()
          .size() > 0) {
        ImageLoader.with(itemView.getContext())
            .loadWithShadowCircleTransform(post.getPosters()
                .get(0)
                .getPrimaryAvatar(), this.headerAvatar1);
      }
      if (post.getPosters()
          .size() > 1) {
        ImageLoader.with(itemView.getContext())
            .loadWithShadowCircleTransform(post.getPosters()
                .get(1)
                .getPrimaryAvatar(), this.headerAvatar2);
      }
    }
    this.headerNames.setText(getCardHeaderNames(post));
    this.headerTimestamp.setText(
        dateCalculator.getTimeSinceDate(itemView.getContext(), post.getLatestUpdate()));
    this.storeNameBodyHeader.setText(post.getStoreName());
    ImageLoader.with(itemView.getContext())
        .load(post.getStoreAvatar(), storeAvatarFollow);
    this.storeNameFollow.setText(post.getStoreName());
    this.storeNumberFollowers.setText(String.valueOf(post.getSubscribers()));
    this.storeNumberApps.setText(String.valueOf(post.getAppsNumber()));
    setupOverflowMenu(post, position);
    showStoreLatestApps(post);
    showMorePostersLabel(post);
    minimalCardContainer.removeAllViews();
    minimalCardContainer.addView(minimalCardViewFactory.getView(post, post.getMinimalPosts(),
        MinimalCardViewFactory.MINIMUM_NUMBER_OF_VISILIBE_MINIMAL_CARDS, inflater,
        itemView.getContext(), position));
    showFollowButton(post);
    this.followStoreButton.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new FollowStoreCardTouchEvent(post, post.getStoreId(), post.getStoreName(),
            CardTouchEvent.Type.BODY, getPosition())));
    this.storeAvatarFollow.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new StoreCardTouchEvent(post, post.getStoreName(), post.getStoreTheme(),
            CardTouchEvent.Type.BODY, getPosition())));
  }

  private void showFollowButton(AggregatedStore post) {
    storeRepository.isSubscribed(post.getStoreId())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(isSubscribed -> {
          if (isSubscribed) {
            followStoreButton.setText(R.string.followed);
          } else {
            followStoreButton.setText(R.string.follow);
          }
        }, throwable -> CrashReport.getInstance()
            .log(throwable));
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
              appsPackages.get(apps.get(app)), getPosition())));
    }
  }

  private void setupOverflowMenu(Post post, int position) {
    overflowMenu.setOnClickListener(view -> {
      PopupMenu popupMenu = new PostPopupMenuBuilder().prepMenu(itemView.getContext(), overflowMenu)
          .addReportAbuse(menuItem -> {
            cardTouchEventPublishSubject.onNext(
                new CardTouchEvent(post, position, CardTouchEvent.Type.REPORT_ABUSE));
            return false;
          })
          .addUnfollow(menuItem -> {
            cardTouchEventPublishSubject.onNext(
                new CardTouchEvent(post, position, CardTouchEvent.Type.UNFOLLOW_STORE));
            return false;
          })
          .getPopupMenu();
      popupMenu.show();
    });
  }
}
