/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 01/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ArticleDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by marcelobenites on 6/17/16.
 */
public class ArticleWidget extends Widget<ArticleDisplayable> {

	private TextView title;
	private TextView subtitle;
	private ImageView image;
	private TextView articleTitle;
	private ImageView thumbnail;
	private View url;
	private Button getAppButton;
	private CardView cardView;
	private CompositeSubscription subscriptions;
	private View articleHeader;
	private ArticleDisplayable displayable;

	public ArticleWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		title = (TextView)itemView.findViewById(R.id.card_title);
		subtitle = (TextView)itemView.findViewById(R.id.card_subtitle);
		image = (ImageView) itemView.findViewById(R.id.card_image);
		articleTitle = (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_title);
		thumbnail = (ImageView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_image);
		url = itemView.findViewById(R.id.partial_social_timeline_thumbnail);
		getAppButton = (Button) itemView.findViewById(R.id.partial_social_timeline_thumbnail_get_app_button);
		cardView = (CardView) itemView.findViewById(R.id.card);
		articleHeader = itemView.findViewById(R.id.displayable_social_timeline_article_header);
	}

	@Override
	public void bindView(ArticleDisplayable displayable) {
		this.displayable = displayable;
		title.setText(displayable.getTitle());
		subtitle.setText(displayable.getTimeSinceLastUpdate(getContext()));
		articleTitle.setText(displayable.getArticleTitle());
		setCardviewMargin(displayable);
		ImageLoader.loadWithShadowCircleTransform(displayable.getAvatarUrl(), image);
		ImageLoader.load(displayable.getThumbnailUrl(), thumbnail);

		if (getAppButton.getVisibility() != View.GONE && displayable.isGetApp()) {
			getAppButton.setVisibility(View.VISIBLE);
			getAppButton.setText(displayable.getAppText(getContext()));
			getAppButton.setOnClickListener(view -> ((FragmentShower) getContext())
					.pushFragmentV4(AppViewFragment.newInstance(displayable.getAppId())));
		}
		//		else {
		//			getAppButton.setVisibility(View.GONE);
		//		}

		url.setOnClickListener(v -> {
			openInBrowser(displayable.getUrl());
			Analytics.AppsTimeline.clickOnCard("Article", Analytics.AppsTimeline.BLANK, displayable.getArticleTitle(), displayable.getTitle(), Analytics
					.AppsTimeline
					.OPEN_ARTICLE);
		});
	}

	private void setCardviewMargin(ArticleDisplayable displayable) {
		CardView.LayoutParams layoutParams = new CardView.LayoutParams(
				CardView.LayoutParams.WRAP_CONTENT, CardView.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(displayable.getMarginWidth(getContext(), getContext().getResources().getConfiguration().orientation),0,displayable
				.getMarginWidth
						(getContext(), getContext().getResources().getConfiguration().orientation),30);
		cardView.setLayoutParams(layoutParams);
	}

	private void openInBrowser(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		Bundle bundle = new Bundle();
		bundle.putString("Referer", "http://m.aptoide.com");
		intent.putExtra(Browser.EXTRA_HEADERS, bundle);
		getContext().startActivity(intent);
	}

	@Override
	public void onViewAttached() {
		if (subscriptions == null) {
			subscriptions = new CompositeSubscription();

			subscriptions.add(RxView.clicks(articleHeader)
					.subscribe(click -> {
						openInBrowser(displayable.getBaseUrl());
						Analytics.AppsTimeline.clickOnCard("Article", Analytics.AppsTimeline.BLANK, displayable.getArticleTitle(), displayable.getTitle(), Analytics
								.AppsTimeline
								.OPEN_ARTICLE_HEADER);
					}));
		}
	}

	@Override
	public void onViewDetached() {
		url.setOnClickListener(null);
		getAppButton.setOnClickListener(null);
		if (subscriptions != null) {
			subscriptions.unsubscribe();
			subscriptions = null;
		}
	}
}
