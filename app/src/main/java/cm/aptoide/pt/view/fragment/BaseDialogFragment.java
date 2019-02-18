package cm.aptoide.pt.view.fragment;

import android.os.Bundle;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.FlavourFragmentModule;
import cm.aptoide.pt.view.BaseActivity;
import cm.aptoide.pt.view.FragmentComponent;
import cm.aptoide.pt.view.FragmentModule;
import com.trello.rxlifecycle.components.support.RxDialogFragment;

public class BaseDialogFragment extends RxDialogFragment {

  private FragmentComponent fragmentComponent;

  public FragmentComponent getFragmentComponent(Bundle savedInstanceState) {
    if (fragmentComponent == null) {
      AptoideApplication aptoideApplication =
          ((AptoideApplication) getContext().getApplicationContext());
      fragmentComponent = ((BaseActivity) getActivity()).getActivityComponent()
          .plus(getFragmentModule(this, savedInstanceState, getArguments(),
              aptoideApplication.isCreateStoreUserPrivacyEnabled(),
              (getActivity().getApplicationContext()).getPackageName()),
              new FlavourFragmentModule());
    }
    return fragmentComponent;
  }

  private FragmentModule getFragmentModule(BaseDialogFragment baseFragment,
      Bundle savedInstanceState, Bundle arguments, boolean createStoreUserPrivacyEnabled,
      String packageName) {
    return new FragmentModule(baseFragment, savedInstanceState, arguments,
        createStoreUserPrivacyEnabled, packageName);
  }
}
