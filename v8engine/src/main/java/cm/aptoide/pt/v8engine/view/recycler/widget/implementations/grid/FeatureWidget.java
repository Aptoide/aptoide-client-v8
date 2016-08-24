package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.timeline.Feature;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FeatureDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.VideoDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by marcelobenites on 6/21/16.
 */
public class FeatureWidget extends Widget<FeatureDisplayable> {

	private TextView title;
	private TextView subtitle;
	private ImageView image;
	private TextView articleTitle;
	private ImageView thumbnail;
	private View url;
	private Button getAppButton;
	private CardView cardView;

	public FeatureWidget(View itemView) {
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
	}

	@Override
	public void bindView(FeatureDisplayable displayable) {
		title.setText(displayable.getTitle(getContext()));
		subtitle.setText(displayable.getTimeSinceLastUpdate(getContext()));
		articleTitle.setText(displayable.getTitleResource());
		setCardviewMargin(displayable);
		ImageLoader.loadWithShadowCircleTransform(displayable.getAvatarResource(), image);
		ImageLoader.load(displayable.getThumbnailUrl(), thumbnail);

		if (displayable.isGetApp()) {
			getAppButton.setVisibility(View.VISIBLE);
			getAppButton.setText(displayable.getAppText(getContext()));
			getAppButton.setOnClickListener(view -> ((FragmentShower) getContext())
					.pushFragmentV4(AppViewFragment.newInstance(displayable.getAppId())));
		} else {
			getAppButton.setVisibility(View.GONE);
		}

		url.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(displayable.getUrl())));
			}
		});
	}

	private void setCardviewMargin(FeatureDisplayable displayable) {
		CardView.LayoutParams layoutParams = new CardView.LayoutParams(
				CardView.LayoutParams.WRAP_CONTENT, CardView.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(displayable.getMarginWidth(getContext(), getContext().getResources().getConfiguration().orientation),0,displayable
				.getMarginWidth
						(getContext(), getContext().getResources().getConfiguration().orientation),30);
		cardView.setLayoutParams(layoutParams);
	}

	@Override
	public void onViewAttached() {

	}

	@Override
	public void onViewDetached() {
		url.setOnClickListener(null);
		getAppButton.setOnClickListener(null);
	}
}
