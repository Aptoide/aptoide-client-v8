/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewSuggestedAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewSuggestedAppsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sithengineer on 04/05/16.
 */
@Displayables({ AppViewSuggestedAppsDisplayable.class }) public class AppViewSuggestedAppsWidget
    extends Widget<AppViewSuggestedAppsDisplayable> {

  private RecyclerView recyclerView;

  public AppViewSuggestedAppsWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    recyclerView = (RecyclerView) itemView.findViewById(R.id.appview_suggested_recycler_view);
  }

  @Override public void bindView(AppViewSuggestedAppsDisplayable displayable) {
    final List<GetAdsResponse.Ad> ads = displayable.getPojo();

    List<Displayable> displayables = new LinkedList<>();
    for (GetAdsResponse.Ad ad : ads) {
      displayables.add(new AppViewSuggestedAppDisplayable(MinimalAd.from(ad)));
    }

    BaseAdapter adapter = new BaseAdapter(displayables) {
      @Override public int getItemCount() {
        return super.getItemCount();
      }
    };
    recyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), OrientationHelper.HORIZONTAL, false));
    recyclerView.setAdapter(adapter);
  }

  @Override public void onViewAttached() {
  }

  @Override public void onViewDetached() {
  }
}
