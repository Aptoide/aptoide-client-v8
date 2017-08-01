package cm.aptoide.pt.view.app.widget;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.app.displayable.AppViewSuggestedAppDisplayable;
import cm.aptoide.pt.view.app.displayable.AppViewSuggestedAppsDisplayable;
import cm.aptoide.pt.view.recycler.BaseAdapter;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import cm.aptoide.pt.view.recycler.widget.Displayables;
import cm.aptoide.pt.view.recycler.widget.Widget;
import java.util.LinkedList;
import java.util.List;

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
    final List<MinimalAd> ads = displayable.getPojo();

    List<Displayable> displayables = new LinkedList<>();
    for (MinimalAd minimalAd : ads) {
      displayables.add(
          new AppViewSuggestedAppDisplayable(minimalAd, displayable.getAppViewAnalytics()));
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
}
