package cm.aptoide.pt.home;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.app.Application;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 07/03/2018.
 */

class EditorsBundleViewHolder extends AppBundleViewHolder {
  private final TextView bundleTitle;
  private final Button moreButton;
  private final RecyclerView graphicsList;
  private final EditorsAppsAdapter appsAdapter;
  private final PublishSubject<HomeClick> uiEventsListener;
  private final LinearLayoutManager layoutManager;

  public EditorsBundleViewHolder(View view, PublishSubject<HomeClick> uiEventsListener,
      DecimalFormat oneDecimalFormatter) {
    super(view);
    this.uiEventsListener = uiEventsListener;
    bundleTitle = (TextView) view.findViewById(R.id.bundle_title);
    moreButton = (Button) view.findViewById(R.id.bundle_more);
    graphicsList = (RecyclerView) view.findViewById(R.id.featured_graphic_list);
    appsAdapter = new EditorsAppsAdapter(new ArrayList<>(), oneDecimalFormatter);
    layoutManager =
        new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
    graphicsList.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
          RecyclerView.State state) {
        int margin = AptoideUtils.ScreenU.getPixelsForDip(5, view.getResources());
        outRect.set(margin, margin, 0, margin);
      }
    });
    graphicsList.setLayoutManager(layoutManager);
    graphicsList.setAdapter(appsAdapter);
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    if (!(homeBundle instanceof AppBundle)) {
      throw new IllegalStateException(this.getClass()
          .getName() + " is getting non AppBundle instance!");
    }
    bundleTitle.setText(homeBundle.getTitle());
    appsAdapter.update((List<Application>) homeBundle.getContent());

    moreButton.setOnClickListener(
        v -> uiEventsListener.onNext(new HomeClick(homeBundle, HomeClick.Type.MORE)));
  }
}
