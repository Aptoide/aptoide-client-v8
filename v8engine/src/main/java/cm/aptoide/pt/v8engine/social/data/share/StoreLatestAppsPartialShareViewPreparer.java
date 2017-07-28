package cm.aptoide.pt.v8engine.social.data.share;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.Post;
import cm.aptoide.pt.v8engine.social.data.StoreLatestApps;

class StoreLatestAppsPartialShareViewPreparer implements PartialShareViewPreparer {
  @Override public View prepareViewForPostType(Post post, Context context, LayoutInflater factory) {
    View view = factory.inflate(R.layout.timeline_store_preview, null);

    TextView sharedStoreTitleName = (TextView) view.findViewById(R.id.social_shared_store_name);
    TextView sharedStoreName = (TextView) view.findViewById(R.id.store_name);
    ImageView sharedStoreAvatar = (ImageView) view.findViewById(R.id.social_shared_store_avatar);
    LinearLayout latestAppsContainer = (LinearLayout) view.findViewById(
        R.id.displayable_social_timeline_store_latest_apps_container);
    RelativeLayout followStoreBar = (RelativeLayout) view.findViewById(R.id.follow_store_bar);

    followStoreBar.setVisibility(View.GONE);
    sharedStoreTitleName.setText(((StoreLatestApps) post).getStoreName());
    sharedStoreName.setText(((StoreLatestApps) post).getStoreName());
    ImageLoader.with(context)
        .loadWithShadowCircleTransform(((StoreLatestApps) post).getStoreAvatar(),
            sharedStoreAvatar);
    View latestAppView;
    ImageView latestAppIcon;
    TextView latestAppName;
    for (App latestApp : ((StoreLatestApps) post).getApps()) {
      latestAppView =
          factory.inflate(R.layout.social_timeline_latest_app, latestAppsContainer, false);
      latestAppIcon = (ImageView) latestAppView.findViewById(R.id.social_timeline_latest_app_icon);
      latestAppName = (TextView) latestAppView.findViewById(R.id.social_timeline_latest_app_name);
      ImageLoader.with(context)
          .load(latestApp.getIcon(), latestAppIcon);
      latestAppName.setMaxLines(1);
      latestAppName.setText(latestApp.getName());
      latestAppsContainer.addView(latestAppView);
    }
    return view;
  }
}
