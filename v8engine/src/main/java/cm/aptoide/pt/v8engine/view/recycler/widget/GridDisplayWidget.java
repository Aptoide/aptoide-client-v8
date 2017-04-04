/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.model.v7.store.GetStoreDisplays;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.store.home.HomeFragment;
import cm.aptoide.pt.v8engine.view.store.StoreTabFragmentChooser;
import cm.aptoide.pt.v8engine.view.store.GridDisplayDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import rx.functions.Action1;

/**
 * Created by sithengineer on 02/05/16.
 */
@Displayables({ GridDisplayDisplayable.class }) public class GridDisplayWidget
    extends Widget<GridDisplayDisplayable> {

  private static final String TAG = GridDisplayWidget.class.getName();

  private ImageView imageView;

  public GridDisplayWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    imageView = (ImageView) itemView.findViewById(R.id.image_category);
  }

  @Override public void bindView(GridDisplayDisplayable displayable) {
    GetStoreDisplays.EventImage pojo = displayable.getPojo();
    final FragmentActivity context = getContext();
    ImageLoader.with(context).load(pojo.getGraphic(), imageView);

    final Action1<Void> imageClickHandler = v -> {
      Event event = pojo.getEvent();
      Event.Name name = event.getName();
      if (StoreTabFragmentChooser.validateAcceptedName(name)) {
        getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
            .newStoreTabGridRecyclerFragment(event, pojo.getLabel(), displayable.getStoreTheme(),
                displayable.getTag(), displayable.getStoreContext()));
      } else {
        switch (name) {
          case facebook:
            InstalledAccessor installedAccessor = AccessorFactory.getAccessorFor(Installed.class);
            compositeSubscription.add(installedAccessor.get(HomeFragment.FACEBOOK_PACKAGE_NAME)
                .subscribe(installedFacebook -> {
                  sendActionEvent(AptoideUtils.SocialLinksU.getFacebookPageURL(
                      installedFacebook == null ? 0 : installedFacebook.getVersionCode(),
                      event.getAction()));
                }, err -> {
                  CrashReport.getInstance().log(err);
                }));
            break;
          case twitch:
          case youtube:
          default:
            sendActionEvent(event.getAction());
            break;
        }
      }
    };
    compositeSubscription.add(RxView.clicks(imageView)
        .subscribe(imageClickHandler, throwable -> CrashReport.getInstance().log(throwable)));
  }

  private void sendActionEvent(String eventActionUrl) {
    Intent i;
    if (eventActionUrl != null) {
      i = new Intent(Intent.ACTION_VIEW);
      i.setData(Uri.parse(eventActionUrl));
      itemView.getContext().startActivity(i);
    }
  }
}
