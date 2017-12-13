package cm.aptoide.pt.view;

import android.os.Bundle;
import cm.aptoide.pt.AptoideApplication;
import com.trello.rxlifecycle.components.support.RxFragment;

public abstract class BaseFragment extends RxFragment {

  private FragmentComponent fragmentComponent;

  public FragmentComponent getFragmentComponent(Bundle savedInstanceState) {
    if (fragmentComponent == null) {
      fragmentComponent = ((BaseActivity) getActivity()).getActivityComponent()
          .plus(new FragmentModule(this, savedInstanceState, getArguments(),
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
