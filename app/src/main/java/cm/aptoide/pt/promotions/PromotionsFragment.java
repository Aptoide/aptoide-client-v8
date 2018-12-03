package cm.aptoide.pt.promotions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PromotionsFragment extends NavigationTrackFragment implements PromotionsView {

  @Inject PromotionsPresenter promotionsPresenter;
  private TextView firstAppName;
  private TextView secondAppName;
  private ImageView firstAppIcon;
  private ImageView secondAppIcon;
  private RecyclerView promotionsList;
  private PromotionsAdapter promotionsAdapter;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    firstAppName = view.findViewById(R.id.promotions_app_1_app_name);
    secondAppName = view.findViewById(R.id.promotions_app_2_app_name);

    firstAppIcon = view.findViewById(R.id.promotions_app_1_icon);
    secondAppIcon = view.findViewById(R.id.promotions_app_2_icon);

    promotionsList = view.findViewById(R.id.fragment_promotions_promotions_list);
    promotionsAdapter = new PromotionsAdapter(new ArrayList<>(), new PromotionsViewHolderFactory());

    setupRecyclerView();
    attachPresenter(promotionsPresenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName(), "");
  }

  private void setupRecyclerView() {
    promotionsList.setAdapter(promotionsAdapter);
    promotionsList.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    RecyclerView.ItemAnimator animator = promotionsList.getItemAnimator();
    if (animator instanceof SimpleItemAnimator) {
      ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_promotions, container, false);
  }

  @Override public void showPromotionApps(List<PromotionApp> appsList) {

    if (!appsList.isEmpty()) {
      promotionsAdapter.setPromotionApps(appsList);
    }

    /*
    firstAppName.setText(appsList.get(0)
        .getName());

    secondAppName.setText(appsList.get(1)
        .getName());

    ImageLoader.with(getContext())
        .load(appsList.get(0)
            .getAppIcon(), firstAppIcon);

    ImageLoader.with(getContext())
        .load(appsList.get(1)
            .getAppIcon(), secondAppIcon);*/
  }
}
