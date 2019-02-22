package cm.aptoide.pt.home;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;

public class HomeContainerFragment extends NavigationTrackFragment {

  private static final BottomNavigationItem BOTTOM_NAVIGATION_ITEM = BottomNavigationItem.HOME;

  private CheckBox gamesChip;
  private CheckBox appsChip;
  private BottomNavigationActivity bottomNavigationActivity;

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof BottomNavigationActivity) {
      bottomNavigationActivity = ((BottomNavigationActivity) activity);
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (bottomNavigationActivity != null) {
      bottomNavigationActivity.requestFocus(BOTTOM_NAVIGATION_ITEM);
    }
    gamesChip = view.findViewById(R.id.games_chip);
    appsChip = view.findViewById(R.id.apps_chip);

    gamesChip.setOnClickListener(click -> {
      if (appsChip.isChecked()) appsChip.setChecked(false);
    });
    appsChip.setOnClickListener(click -> {
      if (gamesChip.isChecked()) gamesChip.setChecked(false);
    });

    gamesChip.setOnCheckedChangeListener((__, isChecked) -> {
      if (isChecked) {
        gamesChip.setTextColor(getResources().getColor(R.color.white));
      } else {
        gamesChip.setTextColor(getResources().getColor(R.color.default_orange_gradient_end));
      }
    });

    appsChip.setOnCheckedChangeListener((__, isChecked) -> {
      if (isChecked) {
        appsChip.setTextColor(getResources().getColor(R.color.white));
      } else {
        appsChip.setTextColor(getResources().getColor(R.color.default_orange_gradient_end));
      }
    });
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName(), "", StoreContext.home.name());
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_home_container, container, false);
  }

  @Override public void onDetach() {
    bottomNavigationActivity = null;
    super.onDetach();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
  }
}
