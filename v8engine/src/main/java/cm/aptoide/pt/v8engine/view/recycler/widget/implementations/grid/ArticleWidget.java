/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 01/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.link.customtabs.CustomTabsHelper;
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
	private TextView relatedTo;

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
		relatedTo = (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_related_to);
	}

	@Override
	public void bindView(ArticleDisplayable displayable) {
		this.displayable = displayable;
		title.setText(displayable.getTitle());
		subtitle.setText(displayable.getTimeSinceLastUpdate(getContext()));
		Typeface typeFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/DroidSerif-Regular.ttf");
		articleTitle.setTypeface(typeFace);
		articleTitle.setText(displayable.getArticleTitle());
		setCardviewMargin(displayable);
		ImageLoader.loadWithShadowCircleTransform(displayable.getAvatarUrl(), image);
		ImageLoader.load(displayable.getThumbnailUrl(), thumbnail);

		relatedTo.setText(displayable.getAppRelatedToText(getContext()));

		if (getAppButton.getVisibility() != View.GONE && displayable.isGetApp()) {
			getAppButton.setVisibility(View.VISIBLE);
			getAppButton.setText(displayable.getAppText(getContext()));
			getAppButton.setOnClickListener(view -> ((FragmentShower) getContext())
					.pushFragmentV4(AppViewFragment.newInstance(displayable.getAppId())));
		}

//		CustomTabsHelper.getInstance()
//				.setUpCustomTabsService(displayable.getLink().getUrl(), getContext());

		url.setOnClickListener(v -> {
			displayable.getLink().launch(getContext());
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

	@Override
	public void onViewAttached() {
		if (subscriptions == null) {
			subscriptions = new CompositeSubscription();

			subscriptions.add(RxView.clicks(articleHeader)
					.subscribe(click -> {
						displayable.getDeveloperLink().launch(getContext());
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
