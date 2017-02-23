package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.FragmentView;
import cm.aptoide.pt.v8engine.presenter.JoinCommunityPresenter;
import cm.aptoide.pt.v8engine.view.JoinCommunityView;

/**
 * This fragment has too much code equal to {@link LoginSignUpFragment} due to Google / Facebook
 * login functionality. Further code refactoring is needed to migrate external source login into
 * their own fragment and include the fragment inside the necessary login / sign up views.
 */
public class JoinCommunityFragment extends FragmentView implements JoinCommunityView {

  private static final String BOTTOM_SHEET_WITH_BOTTOM_BAR = "bottom_sheet_expanded";

  private BottomSheetStateListener bottomSheetStateListener;
  private LoginSignUpFragment loginFragment;
  private BottomSheetBehavior<View> bottomSheetBehavior;

  public static JoinCommunityFragment newInstance() {
    return new JoinCommunityFragment();
  }

  public static JoinCommunityFragment newInstance(boolean withBottomBar) {
    Bundle args = new Bundle();
    args.putBoolean(BOTTOM_SHEET_WITH_BOTTOM_BAR, withBottomBar);

    JoinCommunityFragment fragment = new JoinCommunityFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(getLayoutId(), container, false);

    loginFragment = LoginSignUpFragment.newInstance();
    // nested fragments only work using dynamic fragment addition.
    getActivity().getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.login_signup_layout, loginFragment)
        .commit();

    return view;
  }

  @LayoutRes public int getLayoutId() {
    return R.layout.fragment_join_the_community;
  }

  @Override public boolean onBackPressed() {
    if (loginFragment.onBackPressed()) {
      bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
      return true;
    }
    return super.onBackPressed();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    bindViews(view);
    attachPresenter(new JoinCommunityPresenter(this), savedInstanceState);
  }

  private void bindViews(View view) {
    bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.login_signup_layout));

    Bundle args = getArguments();
    boolean withBottomBar = args != null && args.getBoolean(BOTTOM_SHEET_WITH_BOTTOM_BAR, false);
    bottomSheetBehavior.setState(
        withBottomBar ? BottomSheetBehavior.STATE_COLLAPSED : BottomSheetBehavior.STATE_EXPANDED);

    final View mainContent = view.findViewById(R.id.main_content);
    final int originalBottomPadding = withBottomBar ? mainContent.getPaddingBottom() : 0;
    if (!withBottomBar) {
      mainContent.setPadding(0, 0, 0, 0);
    }
    bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
      @Override public void onStateChanged(@NonNull View bottomSheet, int newState) {
        switch (newState) {
          case BottomSheetBehavior.STATE_COLLAPSED:
            if (bottomSheetStateListener != null) {
              bottomSheetStateListener.hidden();
              mainContent.setPadding(0, 0, 0, originalBottomPadding);
            }
            break;
          case BottomSheetBehavior.STATE_EXPANDED:
            if (bottomSheetStateListener != null) {
              bottomSheetStateListener.expanded();
              mainContent.setPadding(0, 0, 0, 0);
            }
            break;
        }
      }

      @Override public void onSlide(@NonNull View bottomSheet, float slideOffset) {
      }
    });
  }

  public JoinCommunityFragment registerBottomSheetStateListener(
      BottomSheetStateListener bottomSheetStateListener) {
    this.bottomSheetStateListener = bottomSheetStateListener;
    return this;
  }

  public interface BottomSheetStateListener {
    void expanded();

    void hidden();
  }
}
