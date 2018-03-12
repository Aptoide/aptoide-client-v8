package cm.aptoide.pt.home;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.utils.AptoideUtils;
import java.text.DecimalFormat;
import java.util.ArrayList;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 07/03/2018.
 */

class AppsBundleViewHolder extends AppBundleViewHolder {
  private final TextView bundleTitle;
  private final Button moreButton;
  private final RecyclerView appsList;
  private final AppsInBundleAdapter appsInBundleAdapter;
  private final PublishSubject<HomeClick> uiEventsListener;
  private final LinearLayoutManager layoutManager;

  public AppsBundleViewHolder(View view, PublishSubject<HomeClick> uiEventsListener,
      DecimalFormat oneDecimalFormatter) {
    super(view);
    this.uiEventsListener = uiEventsListener;
    bundleTitle = (TextView) view.findViewById(R.id.bundle_title);
    moreButton = (Button) view.findViewById(R.id.bundle_more);
    appsList = (RecyclerView) view.findViewById(R.id.apps_list);
    appsInBundleAdapter = new AppsInBundleAdapter(new ArrayList<>(), oneDecimalFormatter);
    layoutManager =
        new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
    appsList.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
          RecyclerView.State state) {
        int margin = AptoideUtils.ScreenU.getPixelsForDip(5, view.getResources());
        outRect.set(margin, margin, 0, margin);
      }
    });
    appsList.setLayoutManager(layoutManager);
    appsList.setAdapter(appsInBundleAdapter);
  }

  @Override public void setBundle(AppBundle appBundle, int position) {
    bundleTitle.setText(appBundle.getTitle());
    appsInBundleAdapter.update(appBundle.getApps());

    moreButton.setOnClickListener(
        v -> uiEventsListener.onNext(new HomeClick(appBundle, HomeClick.Type.MORE)));
  }
}
