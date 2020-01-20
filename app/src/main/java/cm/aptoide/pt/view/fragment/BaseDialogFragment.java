package cm.aptoide.pt.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.AttrRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.FlavourFragmentModule;
import cm.aptoide.pt.R;
import cm.aptoide.pt.themes.ThemeManager;
import cm.aptoide.pt.view.BaseActivity;
import cm.aptoide.pt.view.FragmentComponent;
import cm.aptoide.pt.view.FragmentModule;
import cm.aptoide.pt.view.MainActivity;
import com.trello.rxlifecycle.components.support.RxDialogFragment;
import javax.inject.Inject;

public class BaseDialogFragment extends RxDialogFragment {

  @Inject ThemeManager themeAttributeProvider;
  private FragmentComponent fragmentComponent;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((MainActivity) getContext()).getActivityComponent()
        .inject(this);

    if (this.getActivity() != null) {
      setStyle(DialogFragment.STYLE_NO_TITLE,
          themeAttributeProvider.getAttributeForTheme(getDialogStyle()).resourceId);
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    WindowManager.LayoutParams layoutParams = getDialog().getWindow()
        .getAttributes();
    layoutParams.dimAmount = 0.6f;
    getDialog().getWindow()
        .setAttributes(layoutParams);
    getDialog().getWindow()
        .addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
  }

  public @AttrRes int getDialogStyle() {
    return R.attr.dialogsTheme;
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

  private FragmentModule getFragmentModule(BaseDialogFragment baseFragment,
      Bundle savedInstanceState, Bundle arguments, boolean createStoreUserPrivacyEnabled,
      String packageName) {
    return new FragmentModule(baseFragment, savedInstanceState, arguments,
        createStoreUserPrivacyEnabled, packageName);
  }
}
