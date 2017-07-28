package cm.aptoide.pt.spotandshareapp.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import cm.aptoide.pt.spotandshareapp.AppModel;
import cm.aptoide.pt.spotandshareapp.R;
import java.util.List;
import rx.Observable;

/**
 * Created by filipe on 27-07-2017.
 */

public class SpotAndShareBottomSheetPickAppDialog extends BottomSheetDialog {

  private final SpotAndSharePickAppsAdapter adapter;
  private RecyclerView installedAppsRecyclerView;
  private View progressBarContainer;

  public SpotAndShareBottomSheetPickAppDialog(@NonNull Context context,
      SpotAndSharePickAppsAdapter adapter) {
    super(context);
    this.adapter = adapter;
    supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setCancelable(false);

    View contentView = View.inflate(getContext(), R.layout.fragment_spotandshare_pick_apps, null);

    setContentView(contentView);
    configureBottomSheet(contentView);

    progressBarContainer = findViewById(R.id.app_selection_progress_bar);
    installedAppsRecyclerView = (RecyclerView) findViewById(R.id.app_selection_recycler_view);
    setupRecyclerView();
  }

  private void setupRecyclerView() {
    installedAppsRecyclerView.setAdapter(adapter);
    setupLayoutManager();
    installedAppsRecyclerView.setHasFixedSize(true);
  }

  private void setupLayoutManager() {
    GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 3);
    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override public int getSpanSize(int position) {
        if (adapter.isPositionHeader(position)) {
          return gridLayoutManager.getSpanCount();
        }
        return 1;
      }
    });
    installedAppsRecyclerView.setLayoutManager(gridLayoutManager);
  }

  public Observable<AppModel> onSelectedApp() {
    return adapter.onSelectedApp();
  }

  public void setInstalledAppsList(List<AppModel> installedApps) {
    adapter.setInstalledAppsList(installedApps);
  }

  public void hideLoading() {
    progressBarContainer.setVisibility(View.GONE);
  }

  public void showLoading() {
    progressBarContainer.setVisibility(View.VISIBLE);
  }

  private void configureBottomSheet(View contentView) {
    BottomSheetBehavior mBottomSheetBehavior =
        BottomSheetBehavior.from((View) contentView.getParent());

    mBottomSheetBehavior.setPeekHeight(200);
    if (mBottomSheetBehavior != null) {
      mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

        @Override public void onStateChanged(@NonNull View bottomSheet, int newState) {
          switch (newState) {
            case BottomSheetBehavior.STATE_HIDDEN:
              mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
              break;
            case BottomSheetBehavior.STATE_EXPANDED:
              break;
            case BottomSheetBehavior.STATE_COLLAPSED:
              break;
            case BottomSheetBehavior.STATE_DRAGGING:
              break;
            case BottomSheetBehavior.STATE_SETTLING:
              break;
          }
        }

        @Override public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
      });
    }
  }
}
