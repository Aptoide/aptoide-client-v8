package cm.aptoide.pt.view;

import android.os.Bundle;
import cm.aptoide.pt.AptoideApplication;
import com.trello.rxlifecycle.components.support.RxFragment;

public abstract class BaseFragment extends RxFragment {

  private FragmentComponent fragmentComponent;

  public FragmentComponent getFragmentComponent(Bundle savedInstanceState) {
    if (fragmentComponent == null) {
      fragmentComponent = ((BaseActivity) getActivity()).getActivityComponent()
          .plus(new FragmentModule(this, savedInstanceState,
              getArguments() != null && getArguments().getBoolean("dismiss_to_navigate_to_main_view"),
              getArguments() != null && getArguments().getBoolean("clean_back_stack"),
              getArguments() != null && getArguments().getBoolean("go_to_home", true),
              getArguments() != null && getArguments().getBoolean("is_edit", false),
              ((AptoideApplication) getContext().getApplicationContext()).isCreateStoreUserPrivacyEnabled(),
              (getActivity().getApplicationContext()).getPackageName()));
    }
    return fragmentComponent;
  }

  @Override public void onDestroyView() {
    fragmentComponent = null;
    super.onDestroyView();
  }
}
