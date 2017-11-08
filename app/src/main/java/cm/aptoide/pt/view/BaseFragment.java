package cm.aptoide.pt.view;

import com.trello.rxlifecycle.components.support.RxFragment;

public abstract class BaseFragment extends RxFragment {

  private FragmentComponent fragmentComponent;

  public FragmentComponent getFragmentComponent() {
    if (fragmentComponent == null) {
      fragmentComponent = ((BaseActivity) getActivity()).getActivityComponent()
          .plus(new FragmentModule(this, getArguments().getBoolean("dismiss_to_navigate_to_main_view"), getArguments().getBoolean("clean_back_stack")));
    }
    return fragmentComponent;
  }

  @Override public void onDestroyView() {
    fragmentComponent = null;
    super.onDestroyView();
  }
}
