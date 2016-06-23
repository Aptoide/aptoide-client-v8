package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ArticleDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

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
	private Toolbar toolbar;

	public ArticleWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		toolbar = (Toolbar) itemView.findViewById(R.id.card_toolbar);
		title = (TextView)itemView.findViewById(R.id.card_title);
		subtitle = (TextView)itemView.findViewById(R.id.card_subtitle);
		image = (ImageView) itemView.findViewById(R.id.card_image);
		articleTitle = (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_title);
		thumbnail = (ImageView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_image);
		url = itemView.findViewById(R.id.partial_social_timeline_thumbnail);
	}

	@Override
	public void bindView(ArticleDisplayable displayable) {
		title.setText(displayable.getPublisher());
		subtitle.setText(getContext().getString(R.string.fragment_social_timeline_hours_since_last_update, displayable.getHoursSinceLastUpdate()));
		articleTitle.setText(displayable.getTitle());
		ImageLoader.load(displayable.getAvatarUrl(), image);
		ImageLoader.load(displayable.getThumbnailUrl(), thumbnail);

		toolbar.inflateMenu(R.menu.menu_card_timeline);
		url.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(displayable.getUrl())));
			}
		});
	}
}
