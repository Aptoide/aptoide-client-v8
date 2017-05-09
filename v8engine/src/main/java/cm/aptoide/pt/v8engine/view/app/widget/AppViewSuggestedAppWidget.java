package cm.aptoide.pt.v8engine.view.app.widget;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewSuggestedAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by neuro on 04-08-2016.
 */
public class AppViewSuggestedAppWidget extends Widget<AppViewSuggestedAppDisplayable> {

  private ImageView iconImageView;
  private TextView appNameTextView;
  private TextView descriptionTextView;
  private View layout;

  public AppViewSuggestedAppWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    layout = itemView;
    iconImageView = (ImageView) itemView.findViewById(R.id.icon);
    appNameTextView = (TextView) itemView.findViewById(R.id.app_name);
    descriptionTextView = (TextView) itemView.findViewById(R.id.description);
  }

  @Override public void bindView(AppViewSuggestedAppDisplayable displayable) {
    MinimalAd pojo = displayable.getPojo();

    final FragmentActivity context = getContext();
    ImageLoader.with(context).load(pojo.getIconPath(), iconImageView);
    appNameTextView.setText(pojo.getName());
    descriptionTextView.setText(AptoideUtils.HtmlU.parse(pojo.getDescription()));
    compositeSubscription.add(RxView.clicks(layout)
        .subscribe(__ -> getFragmentNavigator().navigateTo(
            V8Engine.getFragmentProvider().newAppViewFragment(pojo))));
  }
}
