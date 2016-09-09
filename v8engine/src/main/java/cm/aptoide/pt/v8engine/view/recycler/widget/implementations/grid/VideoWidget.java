/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 01/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import com.jakewharton.rxbinding.view.RxView;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.link.customtabs.CustomTabsHelper;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.VideoDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by jdandrade on 8/10/16.
 */
public class VideoWidget extends Widget<VideoDisplayable> {

	private TextView title;
	private TextView subtitle;
	private ImageView image;
	private TextView videoTitle;
	private ImageView thumbnail;
	private View url;
	private CompositeSubscription subscriptions;
	private Button getAppButton;
	private ImageView play_button;
	private FrameLayout media_layout;
	private CardView cardView;
	private VideoDisplayable displayable;
	private View videoHeader;
	private TextView relatedTo;

	public VideoWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		title = (TextView)itemView.findViewById(R.id.card_title);
		subtitle = (TextView)itemView.findViewById(R.id.card_subtitle);
		image = (ImageView) itemView.findViewById(R.id.card_image);
		play_button = (ImageView) itemView.findViewById(R.id.play_button);
		media_layout = (FrameLayout)itemView.findViewById(R.id.media_layout);
		videoTitle = (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_title);
		thumbnail = (ImageView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_image);
		url = itemView.findViewById(R.id.partial_social_timeline_thumbnail);
		getAppButton = (Button) itemView.findViewById(R.id.partial_social_timeline_thumbnail_get_app_button);
		cardView = (CardView) itemView.findViewById(R.id.card);
		videoHeader = itemView.findViewById(R.id.displayable_social_timeline_video_header);
		relatedTo = (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_related_to);

	}

	@Override
	public void bindView(VideoDisplayable displayable) {
		this.displayable = displayable;
		Typeface typeFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/DroidSerif-Regular.ttf");
		title.setText(displayable.getTitle());
		subtitle.setText(displayable.getTimeSinceLastUpdate(getContext()));
		videoTitle.setTypeface(typeFace);
		videoTitle.setText(displayable.getVideoTitle());
		setCardviewMargin(displayable);
		ImageLoader.loadWithShadowCircleTransform(displayable.getAvatarUrl(), image);
		ImageLoader.load(displayable.getThumbnailUrl(), thumbnail);
		play_button.setVisibility(View.VISIBLE);
		relatedTo.setText(displayable.getAppRelatedText(getContext()));

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			media_layout.setForeground(
					getContext().getResources().getDrawable(R.color.overlay_black, getContext()
							.getTheme())
			);
		}else {
			media_layout.setForeground(
					getContext().getResources().getDrawable(R.color.overlay_black)
			);
		}

		if (getAppButton.getVisibility() != View.GONE && displayable.isGetApp()) {
			getAppButton.setVisibility(View.VISIBLE);
			getAppButton.setText(displayable.getAppText(getContext()));
			getAppButton.setOnClickListener(view -> ((FragmentShower) getContext())
					.pushFragmentV4(AppViewFragment.newInstance(displayable.getAppId())));
		}

//		CustomTabsHelper.getInstance()
//				.setUpCustomTabsService(displayable.getLink().getUrl(), getContext());

		media_layout.setOnClickListener(v ->{
			Analytics.AppsTimeline.clickOnCard("Video", Analytics.AppsTimeline.BLANK, displayable.getVideoTitle(), displayable.getTitle(), Analytics
					.AppsTimeline.OPEN_VIDEO);
			displayable.getLink().launch(getContext());
		});
	}

	private void setCardviewMargin(VideoDisplayable displayable) {
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

			subscriptions.add(RxView.clicks(videoHeader)
					.subscribe(click -> {
						displayable.getBaseLink().launch(getContext());
						Analytics.AppsTimeline.clickOnCard("Video", Analytics.AppsTimeline.BLANK,
								displayable.getVideoTitle(), displayable.getTitle(),
								Analytics.AppsTimeline.OPEN_VIDEO_HEADER);
					}));
		}
	}

	@Override
	public void onViewDetached() {
		url.setOnClickListener(null);
		getAppButton.setOnClickListener(null);
		if (subscriptions != null) {
			subscriptions.unsubscribe();
			subscriptions=null;
		}
	}
}
