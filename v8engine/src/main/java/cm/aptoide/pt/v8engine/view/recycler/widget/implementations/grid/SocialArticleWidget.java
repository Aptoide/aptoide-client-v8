package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.ws.v7.SendEventRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.AptoideAnalytics;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SocialArticleDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import com.like.LikeButton;
import com.like.OnLikeListener;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 23/11/2016.
 */

public class SocialArticleWidget extends CardWidget<SocialArticleDisplayable> {

  private final String cardType = "Social Article";
  private TextView title;
  private TextView subtitle;
  private ImageView storeAvatar;
  private ImageView userAvatar;
  private TextView articleTitle;
  private ImageView thumbnail;
  private View url;
  private Button getAppButton;
  private CardView cardView;
  private View articleHeader;
  private SocialArticleDisplayable displayable;
  private TextView relatedTo;
  private LinearLayout like;
  private LinearLayout share;
  private LinearLayout comments;
  private LikeButton likeButton;
  private TextView numberLikes;
  private TextView numberComments;
  private String appName;
  private String packageName;

  public SocialArticleWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    title = (TextView) itemView.findViewById(R.id.card_title);
    subtitle = (TextView) itemView.findViewById(R.id.card_subtitle);
    storeAvatar = (ImageView) itemView.findViewById(R.id.card_image);
    userAvatar = (ImageView) itemView.findViewById(R.id.card_user_avatar);
    articleTitle = (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_title);
    thumbnail = (ImageView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_image);
    url = itemView.findViewById(R.id.partial_social_timeline_thumbnail);
    getAppButton =
        (Button) itemView.findViewById(R.id.partial_social_timeline_thumbnail_get_app_button);
    cardView = (CardView) itemView.findViewById(R.id.card);
    articleHeader = itemView.findViewById(R.id.displayable_social_timeline_article_header);
    relatedTo = (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_related_to);
    like = (LinearLayout) itemView.findViewById(R.id.social_like);
    share = (LinearLayout) itemView.findViewById(R.id.social_share);
    likeButton = (LikeButton) itemView.findViewById(R.id.social_like_test);
    comments = (LinearLayout) itemView.findViewById(R.id.social_comment);
    numberLikes = (TextView) itemView.findViewById(R.id.social_number_of_likes);
    numberComments = (TextView) itemView.findViewById(R.id.social_number_of_comments);
  }

  @Override public void bindView(SocialArticleDisplayable displayable) {
    this.displayable = displayable;
    if (displayable.getStore() != null) {
      title.setText(displayable.getStore().getName());
    }

    if (displayable.getUser() != null) {
      subtitle.setText(displayable.getUser().getName());
    }

    //subtitle.setText(displayable.getTimeSinceLastUpdate(getContext()));
    Typeface typeFace =
        Typeface.createFromAsset(getContext().getAssets(), "fonts/DroidSerif-Regular.ttf");
    articleTitle.setTypeface(typeFace);
    articleTitle.setText(displayable.getArticleTitle());
    setCardviewMargin(displayable, cardView);

    if (displayable.getStore() != null) {
      ImageLoader.loadWithShadowCircleTransform(displayable.getStore().getAvatar(), storeAvatar);
      if (displayable.getUser() != null && ("PUBLIC").equals(ManagerPreferences.getUserAccess())) {
        ImageLoader.loadWithShadowCircleTransform(displayable.getUser().getAvatar(), userAvatar);
      }
    } else {
      if (displayable.getUser() != null && ("PUBLIC").equals(ManagerPreferences.getUserAccess())) {
        ImageLoader.loadWithShadowCircleTransform(displayable.getUser().getAvatar(), storeAvatar);
      }
    }


    ImageLoader.load(displayable.getThumbnailUrl(), thumbnail);
    likeButton.setLiked(false);
    like.setVisibility(View.VISIBLE);
    numberLikes.setVisibility(View.VISIBLE);
    numberLikes.setText(String.valueOf(displayable.getNumberOfLikes()));
    comments.setVisibility(View.VISIBLE);
    numberComments.setVisibility(View.VISIBLE);
    numberComments.setText(String.valueOf(displayable.getNumberOfComments()));
    //relatedTo.setText(displayable.getAppRelatedToText(getContext(), appName));

    //numberLikes.setText(String.valueOf(numberOfLikes));
    if (getAppButton.getVisibility() != View.GONE && displayable.isGetApp(appName)) {
      getAppButton.setVisibility(View.VISIBLE);
      getAppButton.setText(displayable.getAppText(getContext(), appName));
      getAppButton.setOnClickListener(view -> ((FragmentShower) getContext()).pushFragmentV4(
          V8Engine.getFragmentProvider().newAppViewFragment(displayable.getAppId())));
    }

    //		CustomTabsHelper.getInstance()
    //				.setUpCustomTabsService(displayable.getLink().getUrl(), getContext());

    url.setOnClickListener(v -> {
      knockWithSixpackCredentials(displayable.getAbUrl());
      displayable.getLink().launch(getContext());
      Analytics.AppsTimeline.clickOnCard(cardType, Analytics.AppsTimeline.BLANK,
          displayable.getArticleTitle(), displayable.getTitle(),
          Analytics.AppsTimeline.OPEN_ARTICLE);
      displayable.sendOpenArticleEvent(SendEventRequest.Body.Data.builder()
          .cardType(cardType)
          .source(displayable.getTitle())
          .specific(SendEventRequest.Body.Specific.builder()
              .url(displayable.getLink().getUrl())
              .app(packageName)
              .build())
          .build(), AptoideAnalytics.OPEN_ARTICLE);
    });

    compositeSubscription.add(displayable.getRelatedToApplication()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(installeds -> {
          if (installeds != null && !installeds.isEmpty()) {
            appName = installeds.get(0).getName();
            packageName = installeds.get(0).getPackageName();
          } else {
            setAppNameToFirstLinkedApp();
          }
          if (appName != null) {
            relatedTo.setText(displayable.getAppRelatedToText(getContext(), appName));
          }
        }, throwable -> {
          setAppNameToFirstLinkedApp();
          if (appName != null) {
            relatedTo.setText(displayable.getAppRelatedToText(getContext(), appName));
          }
          throwable.printStackTrace();
        }));

    compositeSubscription.add(RxView.clicks(articleHeader).subscribe(click -> {
      knockWithSixpackCredentials(displayable.getAbUrl());
      displayable.getDeveloperLink().launch(getContext());
      Analytics.AppsTimeline.clickOnCard(cardType, Analytics.AppsTimeline.BLANK,
          displayable.getArticleTitle(), displayable.getTitle(),
          Analytics.AppsTimeline.OPEN_ARTICLE_HEADER);
      displayable.sendOpenArticleEvent(SendEventRequest.Body.Data.builder()
          .cardType(cardType)
          .source(displayable.getTitle())
          .specific(SendEventRequest.Body.Specific.builder()
              .url(displayable.getDeveloperLink().getUrl())
              .app(packageName)
              .build())
          .build(), AptoideAnalytics.OPEN_BLOG);
    }));

    compositeSubscription.add(RxView.clicks(share).subscribe(click -> {
      //shareCard(displayable);
    }, throwable -> throwable.printStackTrace()));

    compositeSubscription.add(RxView.clicks(like).subscribe(click -> {
    }, (throwable) -> throwable.printStackTrace()));

    likeButton.setOnLikeListener(new OnLikeListener() {
      @Override public void liked(LikeButton likeButton) {
        likeCard(displayable, cardType, 1);
        numberLikes.setText(String.valueOf(displayable.getNumberOfLikes() + 1));
      }

      @Override public void unLiked(LikeButton likeButton) {
        likeButton.setLiked(true);
        //likeCard(displayable, cardType, -1);
        //numberLikes.setText("0");
      }
    });
  }

  private void setAppNameToFirstLinkedApp() {
    if (!displayable.getRelatedToAppsList().isEmpty()) {
      appName = displayable.getRelatedToAppsList().get(0).getName();
    }
  }
}
