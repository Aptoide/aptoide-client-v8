package cm.aptoide.pt.v8engine.social.data.share;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.CardType;
import cm.aptoide.pt.v8engine.social.data.Media;
import cm.aptoide.pt.v8engine.social.data.Post;

class MediaPartialShareViewPreparer implements PartialShareViewPreparer {
  @Override public View prepareViewForPostType(Post post, Context context, LayoutInflater factory) {
    View view = factory.inflate(R.layout.timeline_media_preview, null);
    TextView mediaTitle =
        (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_title);
    ImageView thumbnail = (ImageView) view.findViewById(R.id.featured_graphic);
    TextView relatedTo = (TextView) view.findViewById(R.id.app_name);
    ImageView playIcon = (ImageView) view.findViewById(R.id.play_button);
    if (post.getType()
        .equals(CardType.ARTICLE) || post.getType()
        .equals(CardType.SOCIAL_ARTICLE) || post.getType()
        .equals(CardType.AGGREGATED_SOCIAL_ARTICLE)) {
      playIcon.setVisibility(View.GONE);
    } else if (post.getType()
        .equals(CardType.VIDEO) || post.getType()
        .equals(CardType.SOCIAL_VIDEO) || post.getType()
        .equals(CardType.AGGREGATED_SOCIAL_VIDEO)) {
      playIcon.setVisibility(View.VISIBLE);
    }
    mediaTitle.setMaxLines(1);
    mediaTitle.setText(((Media) post).getMediaTitle());
    relatedTo.setVisibility(View.GONE);
    ImageLoader.with(context)
        .load(((Media) post).getMediaThumbnailUrl(), thumbnail);
    return view;
  }
}
