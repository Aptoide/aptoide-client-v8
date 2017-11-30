package cm.aptoide.pt.view;

import android.os.Bundle;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.ComponentFactory;
import com.trello.rxlifecycle.components.support.RxFragment;

public abstract class BaseFragment extends RxFragment {

  private FragmentComponent fragmentComponent;

  public FragmentComponent getFragmentComponent(Bundle savedInstanceState) {
    if (fragmentComponent == null) {
      boolean dismissToNavigateToMainView = getArguments().getBoolean("dismiss_to_navigate_to_main_view");
      boolean navigateToHome = getArguments().getBoolean("clean_back_stack");
      boolean goToHome = getArguments().getBoolean("go_to_home", true);
      boolean isEditProfile = getArguments() != null && getArguments().getBoolean("is_edit", false);
      boolean isCreateStoreUserPrivacyEnabled = ((AptoideApplication) getContext().getApplicationContext()).isCreateStoreUserPrivacyEnabled();
      String packageName = (getActivity().getApplicationContext()).getPackageName();
      ActivityComponent activityComponent = ((BaseActivity) getActivity()).getActivityComponent();
      fragmentComponent = ComponentFactory.create(activityComponent, this,savedInstanceState,dismissToNavigateToMainView,navigateToHome,goToHome,isEditProfile,
          isCreateStoreUserPrivacyEnabled,packageName);
    }
    return fragmentComponent;
  }

  @Override public void onDestroyView() {
    fragmentComponent = null;
    super.onDestroyView();
  }
}
