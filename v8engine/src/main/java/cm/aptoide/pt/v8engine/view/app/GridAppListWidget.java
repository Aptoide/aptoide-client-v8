/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 30/06/2016.
 */

package cm.aptoide.pt.v8engine.view.app;

import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import java.util.Date;

/**
 * Created by neuro on 29-06-2016.
 */
public class GridAppListWidget extends Widget<GridAppListDisplayable> {

  public TextView name;
  public ImageView icon;
  private TextView tvTimeSinceModified;
  private TextView tvStoreName;

  public GridAppListWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    name = (TextView) itemView.findViewById(R.id.name);
    icon = (ImageView) itemView.findViewById(R.id.icon);
    tvTimeSinceModified = (TextView) itemView.findViewById(R.id.timeSinceModified);
    tvStoreName = (TextView) itemView.findViewById(R.id.storeName);
  }

  @Override public void bindView(GridAppListDisplayable displayable) {
    App app = displayable.getPojo();
    name.setText(app.getName());

    Date modified = app.getUpdated();
    if (modified != null) {
      tvTimeSinceModified.setText(AptoideUtils.DateTimeU.getInstance(itemView.getContext())
          .getTimeDiffString(itemView.getContext(), modified.getTime()));
    }

    name.setText(app.getName());
    name.setTypeface(null, Typeface.BOLD);

    tvStoreName.setText(app.getStore()
        .getName());
    tvStoreName.setTypeface(null, Typeface.BOLD);
    final FragmentActivity context = getContext();
    compositeSubscription.add(RxView.clicks(itemView)
        .subscribe(v -> {
          // FIXME
          getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
              .newAppViewFragment(app.getId(), app.getPackageName()));
        }, throwable -> CrashReport.getInstance()
            .log(throwable)));

    ImageLoader.with(context)
        .load(app.getIcon(), icon);
  }
}
