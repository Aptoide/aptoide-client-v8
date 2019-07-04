package cm.aptoide.pt.home;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.Translator;
import cm.aptoide.pt.view.app.Application;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import rx.subjects.PublishSubject;

public class TopBundleViewHolder extends AppBundleViewHolder {
  private final TextView bundleTitle;
  private final Button moreButton;
  private final RecyclerView topList;
  private final TopBundleAdapter topBundleAdapter;

  private final String marketName;
  private final PublishSubject<HomeEvent> uiEventsListener;

  public TopBundleViewHolder(View view, PublishSubject<HomeEvent> uiEventsListener,
      DecimalFormat oneDecimalFormatter, String marketName) {
    super(view);
    this.marketName = marketName;
    this.uiEventsListener = uiEventsListener;
    bundleTitle = view.findViewById(R.id.bundle_title);
    moreButton = view.findViewById(R.id.bundle_more);
    topList = view.findViewById(R.id.top_list);
    topBundleAdapter =
        new TopBundleAdapter(new ArrayList<>(), oneDecimalFormatter, uiEventsListener);
    LinearLayoutManager layoutManager =
        new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
    topList.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
          RecyclerView.State state) {
        int margin = AptoideUtils.ScreenU.getPixelsForDip(5, view.getResources());
        int marginHorizontal = AptoideUtils.ScreenU.getPixelsForDip(4, view.getResources());
        outRect.set(margin, marginHorizontal, margin, marginHorizontal);
      }
    });
    topList.setLayoutManager(layoutManager);
    topList.setAdapter(topBundleAdapter);
    topList.setNestedScrollingEnabled(false);
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    if (!(homeBundle instanceof AppBundle)) {
      throw new IllegalStateException(this.getClass()
          .getName() + " is getting non AppBundle instance!");
    }
    bundleTitle.setText(
        Translator.translate(homeBundle.getTitle(), itemView.getContext(), marketName));
    topBundleAdapter.updateBundle(homeBundle, position);
    topBundleAdapter.update((List<Application>) homeBundle.getContent());
    moreButton.setOnClickListener(v -> uiEventsListener.onNext(
        new HomeEvent(homeBundle, getAdapterPosition(), HomeEvent.Type.MORE_TOP)));
  }
}
