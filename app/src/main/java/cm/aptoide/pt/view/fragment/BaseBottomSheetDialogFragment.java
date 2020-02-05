package cm.aptoide.pt.view.fragment;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.FlavourFragmentModule;
import cm.aptoide.pt.view.BaseActivity;
import cm.aptoide.pt.view.FragmentComponent;
import cm.aptoide.pt.view.FragmentModule;
import cm.aptoide.pt.view.MainActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.trello.rxlifecycle.components.support.RxAppCompatDialogFragment;

public class BaseBottomSheetDialogFragment extends RxAppCompatDialogFragment {

  private FragmentComponent fragmentComponent;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((MainActivity) getContext()).getActivityComponent()
        .inject(this);
  }

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new BottomSheetDialog(requireContext(), this.getTheme());
  }

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

  private FragmentModule getFragmentModule(BaseBottomSheetDialogFragment baseFragment,
      Bundle savedInstanceState, Bundle arguments, boolean createStoreUserPrivacyEnabled,
      String packageName) {
    return new FragmentModule(baseFragment, savedInstanceState, arguments,
        createStoreUserPrivacyEnabled, packageName);
  }
}
