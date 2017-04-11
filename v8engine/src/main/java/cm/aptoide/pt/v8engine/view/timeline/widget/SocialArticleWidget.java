package cm.aptoide.pt.v8engine.view.timeline.widget;

import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.view.timeline.displayable.SocialArticleDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 23/11/2016.
 */

public class SocialArticleWidget extends SocialCardWidget<SocialArticleDisplayable> {

  private TextView title;
  private TextView subtitle;
  //private TextView time;
  private TextView articleTitle;
  private ImageView thumbnail;
  private View url;
  private Button getAppButton;
  private CardView cardView;
  private View articleHeader;
  private TextView relatedTo;

  private String appName;
  private String packageName;

  public SocialArticleWidget(View itemView) {
    super(itemView);
  }

  @Override String getCardTypeName() {
    return SocialArticleDisplayable.CARD_TYPE_NAME;
  }

  @Override protected void assignViews(View itemView) {
    super.assignViews(itemView);
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
    articleHeader = itemView.findViewById(R.id.social_header);
    relatedTo = (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_related_to);
  }

  @Override public void bindView(SocialArticleDisplayable displayable) {
    super.bindView(displayable);

    final FragmentActivity context = getContext();
    if (displayable.getStore() != null) {
      title.setVisibility(View.VISIBLE);
      title.setText(displayable.getStore().getName());
      storeAvatar.setVisibility(View.VISIBLE);
      ImageLoader.with(context)
          .loadWithShadowCircleTransform(displayable.getStore().getAvatar(), storeAvatar);
      if (displayable.getUser() != null) {
        subtitle.setVisibility(View.VISIBLE);
        subtitle.setText(displayable.getUser().getName());
        userAvatar.setVisibility(View.VISIBLE);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(displayable.getUser().getAvatar(), userAvatar);
      } else {
        subtitle.setVisibility(View.GONE);
        userAvatar.setVisibility(View.GONE);
      }
    } else {
      subtitle.setVisibility(View.GONE);
      userAvatar.setVisibility(View.GONE);
      if (displayable.getUser() != null) {
        title.setVisibility(View.VISIBLE);
        title.setText(displayable.getUser().getName());
        storeAvatar.setVisibility(View.VISIBLE);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(displayable.getUser().getAvatar(), storeAvatar);
      }
    }

    Typeface typeFace =
        Typeface.createFromAsset(context.getAssets(), "fonts/DroidSerif-Regular.ttf");
    articleTitle.setTypeface(typeFace);
    articleTitle.setText(displayable.getArticleTitle());
    setCardViewMargin(displayable, cardView);

    //time.setText(displayable.getTimeSinceLastUpdate(getContext()));

    ImageLoader.with(context).load(displayable.getThumbnailUrl(), thumbnail);

    url.setOnClickListener(v -> {
      knockWithSixpackCredentials(displayable.getAbUrl());
      displayable.getLink().launch(context);
      Analytics.AppsTimeline.clickOnCard(SocialArticleDisplayable.CARD_TYPE_NAME,
          Analytics.AppsTimeline.BLANK, displayable.getArticleTitle(), displayable.getTitle(),
          Analytics.AppsTimeline.OPEN_ARTICLE);
      displayable.sendOpenArticleEvent();
    });

    compositeSubscription.add(displayable.getRelatedToApplication()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(installeds -> {
          if (installeds != null && !installeds.isEmpty()) {
            appName = installeds.get(0).getName();
            packageName = installeds.get(0).getPackageName();
          } else {
            setAppNameToFirstLinkedApp(displayable);
          }
          if (appName != null) {
            relatedTo.setText(displayable.getAppRelatedToText(context, appName));
          }
        }, throwable -> {
          setAppNameToFirstLinkedApp(displayable);
          if (appName != null) {
            relatedTo.setText(displayable.getAppRelatedToText(context, appName));
          }
          throwable.printStackTrace();
        }));

    compositeSubscription.add(RxView.clicks(articleHeader).subscribe(click -> {
      knockWithSixpackCredentials(displayable.getAbUrl());
      displayable.getDeveloperLink().launch(context);
      Analytics.AppsTimeline.clickOnCard(SocialArticleDisplayable.CARD_TYPE_NAME,
          Analytics.AppsTimeline.BLANK, displayable.getArticleTitle(), displayable.getTitle(),
          Analytics.AppsTimeline.OPEN_ARTICLE_HEADER);
      displayable.sendOpenBlogEvent();
    }));
  }

  private void setAppNameToFirstLinkedApp(SocialArticleDisplayable displayable) {
    if (!displayable.getRelatedToAppsList().isEmpty()) {
      appName = displayable.getRelatedToAppsList().get(0).getName();
    }
  }
}
