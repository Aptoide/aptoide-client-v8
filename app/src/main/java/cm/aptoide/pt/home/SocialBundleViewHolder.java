package cm.aptoide.pt.home;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.app.Application;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 23/03/2018.
 */

class SocialBundleViewHolder extends AppBundleViewHolder {

  private final PublishSubject<Application> appClickedEvents;
  private final TextView appName;
  private final TextView userName;
  private final ImageView icon;
  private final TextView rating;
  private final ImageView userIcon;
  private final View appLayout;
  private final Button install;

  public SocialBundleViewHolder(View view, PublishSubject<Application> appClickedEvents) {
    super(view);
    this.appClickedEvents = appClickedEvents;
    this.appName = (TextView) view.findViewById(R.id.recommended_app_name);
    this.icon = (ImageView) view.findViewById(R.id.recommended_app_icon);
    this.rating = (TextView) view.findViewById(R.id.rating_label);
    this.userName = (TextView) view.findViewById(R.id.recommends);
    this.userIcon = (ImageView) view.findViewById(R.id.icon);
    this.appLayout = view.findViewById(R.id.recommended_app_layout);
    this.install = (Button) view.findViewById(R.id.install);
  }

  @Override public void setBundle(HomeBundle homeBundle) {
    if (!(homeBundle instanceof SocialBundle)) {
      throw new IllegalStateException(this.getClass()
          .getName() + " is getting a non SocialBundle instance!");
    }
    SocialBundle bundle = (SocialBundle) homeBundle;
    List<Application> apps = (List<Application>) homeBundle.getContent();
    Application app;

    if (apps != null && !apps.isEmpty()) {
      app = apps.get(0);
      userName.setText(AptoideUtils.StringU.getFormattedString(R.string.home_recommends,
          itemView.getContext()
              .getResources(), bundle.getUserName()));
      appName.setText(app.getName());
      ImageLoader.with(itemView.getContext())
          .load(bundle.getUserIcon(), userIcon);
      rating.setText(String.valueOf(app.getRating()));
      ImageLoader.with(itemView.getContext())
          .load(app.getIcon(), icon);

      appLayout.setOnClickListener(v -> appClickedEvents.onNext(app));
      install.setOnClickListener(v -> appClickedEvents.onNext(app));
    }
  }
}
