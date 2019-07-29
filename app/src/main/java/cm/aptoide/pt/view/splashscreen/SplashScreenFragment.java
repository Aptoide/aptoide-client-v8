package cm.aptoide.pt.view.splashscreen;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.LoginBottomSheet;
import cm.aptoide.pt.home.AptoideBottomNavigator;
import cm.aptoide.pt.view.fragment.UIComponentFragment;
import javax.inject.Inject;

public class SplashScreenFragment extends UIComponentFragment implements SplashScreenView {

  public static final int LAYOUT = R.layout.fragment_splashscreen;

  @Inject SplashScreenPresenter presenter;
  private AptoideBottomNavigator bottomNavigator;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    attachPresenter(presenter);
  }

  @Override public void setupViews() {
    bottomNavigator.hideBottomNavigation();
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof AptoideBottomNavigator) {
      bottomNavigator = (AptoideBottomNavigator) context;
    } else {
      throw new IllegalStateException(
          "Context should implement " + LoginBottomSheet.class.getSimpleName());
    }
    getActivity().getWindow()
        .addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    getActivity().getWindow()
        .clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    bottomNavigator.toggleBottomNavigation();
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public int getContentViewId() {
    return LAYOUT;
  }

  @Override public void bindViews(@Nullable View view) {
  }
}
