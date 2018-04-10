/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.view.recycler.widget;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStoreDisplays;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.store.view.GridDisplayDisplayable;
import cm.aptoide.pt.store.view.StoreTabFragmentChooser;
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment;
import com.jakewharton.rxbinding.view.RxView;
import rx.functions.Action1;

/**
 * Created on 02/05/16.
 */
public class GridDisplayWidget extends Widget<GridDisplayDisplayable> {

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
    ImageLoader.with(context)
        .load(pojo.getGraphic(), imageView);

    final Action1<Void> imageClickHandler = v -> {
      Event event = pojo.getEvent();
      Event.Name name = event.getName();
      if (StoreTabFragmentChooser.validateAcceptedName(name)) {
        getFragmentNavigator().navigateTo(
            StoreTabGridRecyclerFragment.newInstance(event, pojo.getLabel(),
                displayable.getStoreTheme(), displayable.getTag(), displayable.getStoreContext(),
                false), true);
      } else {
        switch (name) {
          case facebook:
          case twitch:
          case youtube:
          default:
            sendActionEvent(event.getAction());
            break;
        }
      }
    };
    compositeSubscription.add(RxView.clicks(imageView)
        .subscribe(imageClickHandler, throwable -> CrashReport.getInstance()
            .log(throwable)));
  }

  private void sendActionEvent(String eventActionUrl) {
    Intent i;
    if (eventActionUrl != null) {
      i = new Intent(Intent.ACTION_VIEW);
      i.setData(Uri.parse(eventActionUrl));
      itemView.getContext()
          .startActivity(i);
    }
  }
}
