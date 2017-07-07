/*
 * Copyright (c) 2016.
 * Modified on 01/08/2016.
 */

package cm.aptoide.pt.v8engine.timeline.view.widget;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.timeline.view.displayable.ArticleDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 6/17/16.
 */
public class ArticleWidget extends CardWidget<ArticleDisplayable> {

  private TextView title;
  private TextView subtitle;
  private ImageView image;
  private TextView articleTitle;
  private ImageView thumbnail;
  private View url;
  private CardView cardView;
  private View articleHeader;
  private TextView relatedTo;

  private String appName;
  private String packageName;
  private RatingBar ratingBar;

  public ArticleWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    super.assignViews(itemView);

    title = (TextView) itemView.findViewById(R.id.card_title);
    subtitle = (TextView) itemView.findViewById(R.id.card_subtitle);
    image = (ImageView) itemView.findViewById(R.id.card_image);
    articleTitle = (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_title);
    thumbnail = (ImageView) itemView.findViewById(R.id.featured_graphic);
    url = itemView.findViewById(R.id.partial_social_timeline_thumbnail);
    cardView = (CardView) itemView.findViewById(R.id.card);
    articleHeader = itemView.findViewById(R.id.displayable_social_timeline_article_header);
    relatedTo = (TextView) itemView.findViewById(R.id.app_name);
    ratingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);
  }

  @Override public void bindView(ArticleDisplayable displayable) {
    super.bindView(displayable);
    final FragmentActivity context = getContext();
    ratingBar.setVisibility(View.INVISIBLE);
    title.setText(displayable.getStyleText(context, displayable.getTitle()));
    subtitle.setText(displayable.getTimeSinceLastUpdate(context));
    articleTitle.setText(displayable.getArticleTitle());
    relatedTo.setTextSize(11);
    setCardViewMargin(displayable, cardView);
    ImageLoader.with(context)
        .loadWithShadowCircleTransform(displayable.getAvatarUrl(), image);
    ImageLoader.with(context)
        .load(displayable.getThumbnailUrl(), thumbnail);
    thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);

    url.setOnClickListener(v -> {
      knockWithSixpackCredentials(displayable.getAbUrl());
      displayable.getLink()
          .launch();
      Analytics.AppsTimeline.clickOnCard(ArticleDisplayable.CARD_TYPE_NAME,
          Analytics.AppsTimeline.BLANK, displayable.getArticleTitle(), displayable.getTitle(),
          Analytics.AppsTimeline.OPEN_ARTICLE);
      displayable.sendArticleWidgetCardClickEvent(Analytics.AppsTimeline.OPEN_ARTICLE,
          socialAction);
      displayable.sendOpenArticleEvent(packageName);
    });

    compositeSubscription.add(displayable.getRelatedToApplication()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(installedList -> {
          if (installedList != null && !installedList.isEmpty()) {
            appName = installedList.get(0)
                .getName();
            packageName = installedList.get(0)
                .getPackageName();
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

    compositeSubscription.add(RxView.clicks(articleHeader)
        .subscribe(click -> {
          knockWithSixpackCredentials(displayable.getAbUrl());
          displayable.getDeveloperLink()
              .launch();
          Analytics.AppsTimeline.clickOnCard(ArticleDisplayable.CARD_TYPE_NAME,
              Analytics.AppsTimeline.BLANK, displayable.getArticleTitle(), displayable.getTitle(),
              Analytics.AppsTimeline.OPEN_ARTICLE_HEADER);
          displayable.sendArticleWidgetCardClickEvent(Analytics.AppsTimeline.OPEN_ARTICLE_HEADER,
              socialAction);
          displayable.sendOpenArticleEvent(packageName);
        }));
  }

  @Override String getCardTypeName() {
    return ArticleDisplayable.CARD_TYPE_NAME;
  }

  private void setAppNameToFirstLinkedApp(ArticleDisplayable displayable) {
    if (!displayable.getRelatedToAppsList()
        .isEmpty()) {
      appName = displayable.getRelatedToAppsList()
          .get(0)
          .getName();
    }
  }
}
