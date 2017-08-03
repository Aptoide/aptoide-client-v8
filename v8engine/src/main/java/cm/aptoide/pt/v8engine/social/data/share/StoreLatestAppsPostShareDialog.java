package cm.aptoide.pt.v8engine.social.data.share;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.StoreLatestApps;
import cm.aptoide.pt.v8engine.view.rx.RxAlertDialog;

class StoreLatestAppsPostShareDialog extends BaseShareDialog<StoreLatestApps> {

  @LayoutRes static final int LAYOUT_ID = R.layout.timeline_store_preview;

  StoreLatestAppsPostShareDialog(RxAlertDialog dialog) {
    super(dialog);
  }

  @Override void setupView(View view, StoreLatestApps post) {
    final Context context = view.getContext();
    final LayoutInflater layoutInflater = LayoutInflater.from(context);

    TextView sharedStoreTitleName = (TextView) view.findViewById(R.id.social_shared_store_name);
    TextView sharedStoreName = (TextView) view.findViewById(R.id.store_name);
    ImageView sharedStoreAvatar = (ImageView) view.findViewById(R.id.social_shared_store_avatar);
    LinearLayout latestAppsContainer = (LinearLayout) view.findViewById(
        R.id.displayable_social_timeline_store_latest_apps_container);
    RelativeLayout followStoreBar = (RelativeLayout) view.findViewById(R.id.follow_store_bar);

    followStoreBar.setVisibility(View.GONE);
    sharedStoreTitleName.setText((post).getStoreName());
    sharedStoreName.setText((post).getStoreName());
    ImageLoader.with(context)
        .loadWithShadowCircleTransform((post).getStoreAvatar(), sharedStoreAvatar);
    View latestAppView;
    ImageView latestAppIcon;
    TextView latestAppName;
    for (App latestApp : (post).getApps()) {
      latestAppView =
          layoutInflater.inflate(R.layout.social_timeline_latest_app, latestAppsContainer, false);
      latestAppIcon = (ImageView) latestAppView.findViewById(R.id.social_timeline_latest_app_icon);
      latestAppName = (TextView) latestAppView.findViewById(R.id.social_timeline_latest_app_name);
      ImageLoader.with(context)
          .load(latestApp.getIcon(), latestAppIcon);
      latestAppName.setMaxLines(1);
      latestAppName.setText(latestApp.getName());
      latestAppsContainer.addView(latestAppView);
    }
  }

  public static class Builder extends BaseShareDialog.Builder {

    public Builder(Context context, SharePostViewSetup sharePostViewSetup, Account account) {
      super(context, sharePostViewSetup, account, LAYOUT_ID);
    }

    public StoreLatestAppsPostShareDialog build() {
      return new StoreLatestAppsPostShareDialog(buildRxAlertDialog());
    }
  }
}
