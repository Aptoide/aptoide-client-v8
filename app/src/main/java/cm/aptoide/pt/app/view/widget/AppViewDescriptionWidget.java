/*
 * Copyright (c) 2016.
 * Modified on 17/08/2016.
 */

package cm.aptoide.pt.app.view.widget;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.view.displayable.AppViewDescriptionDisplayable;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created on 10/05/16.
 */
public class AppViewDescriptionWidget extends Widget<AppViewDescriptionDisplayable> {

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
    this.app = displayable.getPojo()
        .getNodes()
        .getMeta()
        .getData();
    this.media = app.getMedia();
    this.storeName = app.getStore()
        .getName();
    this.storeTheme = app.getStore()
        .getAppearance()
        .getTheme();

    if (!TextUtils.isEmpty(media.getDescription())) {
      descriptionTextView.setText(AptoideUtils.HtmlU.parse(media.getDescription()));
      compositeSubscription.add(RxView.clicks(readMoreBtn)
          .subscribe(click -> {
            displayable.getAppViewAnalytics()
                .sendReadMoreEvent();
            Fragment fragment = AptoideApplication.getFragmentProvider()
                .newDescriptionFragment(app.getName(), media.getDescription(), storeTheme);
            getFragmentNavigator().navigateTo(fragment, true);
          }));
    } else {
      // only show "default" description if the app doesn't have one
      descriptionTextView.setText(R.string.description_not_available);
      readMoreBtn.setVisibility(View.GONE);
    }
  }
}
