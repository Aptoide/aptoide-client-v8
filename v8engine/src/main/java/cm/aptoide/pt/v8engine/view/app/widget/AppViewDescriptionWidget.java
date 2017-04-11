/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 17/08/2016.
 */

package cm.aptoide.pt.v8engine.view.app.widget;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewDescriptionDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by sithengineer on 10/05/16.
 */
@Displayables({ AppViewDescriptionDisplayable.class }) public class AppViewDescriptionWidget
    extends Widget<AppViewDescriptionDisplayable> {

  private TextView descriptionTextView;
  private Button readMoreBtn;
  private String storeName;
  private String storeTheme;
  private GetAppMeta.Media media;
  private GetAppMeta.App app;

  public AppViewDescriptionWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    descriptionTextView = (TextView) itemView.findViewById(R.id.description);
    readMoreBtn = (Button) itemView.findViewById(R.id.read_more_button);
  }

  @Override public void bindView(AppViewDescriptionDisplayable displayable) {
    this.app = displayable.getPojo().getNodes().getMeta().getData();
    this.media = app.getMedia();
    this.storeName = app.getStore().getName();
    this.storeTheme = app.getStore().getAppearance().getTheme();

    if (!TextUtils.isEmpty(media.getDescription())) {
      descriptionTextView.setText(AptoideUtils.HtmlU.parse(media.getDescription()));
      compositeSubscription.add(RxView.clicks(readMoreBtn).subscribe(click -> {
        Fragment fragment = V8Engine.getFragmentProvider()
            .newDescriptionFragment(app.getName(), media.getDescription(), storeTheme);
        getFragmentNavigator().navigateTo(fragment);
      }));
    } else {
      // only show "default" description if the app doesn't have one
      descriptionTextView.setText(R.string.description_not_available);
      readMoreBtn.setVisibility(View.GONE);
    }

    if (!TextUtils.isEmpty(media.getDescription())) {
      descriptionTextView.setText(AptoideUtils.HtmlU.parse(media.getDescription()));
      compositeSubscription.add(RxView.clicks(readMoreBtn).subscribe(click -> {
        Fragment fragment = V8Engine.getFragmentProvider()
            .newDescriptionFragment(app.getName(), media.getDescription(), storeTheme);
        getFragmentNavigator().navigateTo(fragment);
      }));
    } else {
      // only show "default" description if the app doesn't have one
      descriptionTextView.setText(R.string.description_not_available);
      readMoreBtn.setVisibility(View.GONE);
    }
  }
}
