package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.FragmentView;
import cm.aptoide.pt.v8engine.presenter.LoginSignUpPresenter;
import cm.aptoide.pt.v8engine.view.JoinCommunityView;

/**
 * This fragment has too much code equal to {@link LoginSignUpCredentialsFragment} due to Google /
 * Facebook
 * login functionality. Further code refactoring is needed to migrate external source login into
 * their own fragment and include the fragment inside the necessary login / sign up views.
 */
public class LoginSignUpFragment extends FragmentView implements JoinCommunityView {

  private static final String DISMISS_TO_NAVIGATE_TO_MAIN_VIEW = "dismiss_to_navigate_to_main_view";
  private static final String BOTTOM_SHEET_WITH_BOTTOM_BAR = "bottom_sheet_expanded";

  private BottomSheetStateListener bottomSheetStateListener;
  private LoginSignUpCredentialsFragment loginFragment;
  private BottomSheetBehavior<View> bottomSheetBehavior;
  private boolean withBottomBar;
  private boolean dismissToNavigateToMainView;

  public static LoginSignUpFragment newInstance(boolean withBottomBar,
      boolean dimissToNavigateToMainView) {
    Bundle args = new Bundle();
    args.putBoolean(BOTTOM_SHEET_WITH_BOTTOM_BAR, withBottomBar);
    args.putBoolean(DISMISS_TO_NAVIGATE_TO_MAIN_VIEW, dimissToNavigateToMainView);

    LoginSignUpFragment fragment = new LoginSignUpFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    withBottomBar = getArguments().getBoolean(BOTTOM_SHEET_WITH_BOTTOM_BAR);
    dismissToNavigateToMainView = getArguments().getBoolean(DISMISS_TO_NAVIGATE_TO_MAIN_VIEW);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(getLayoutId(), container, false);

    loginFragment = LoginSignUpCredentialsFragment.newInstance(dismissToNavigateToMainView);
    // nested fragments only work using dynamic fragment addition.
    getActivity().getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.login_signup_layout, loginFragment)
        .commit();

    return view;
  }

  @LayoutRes public int getLayoutId() {
    return R.layout.fragment_login_sign_up;
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
    attachPresenter(new LoginSignUpPresenter(this), savedInstanceState);
  }

  private void bindViews(View view) {
    bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.login_signup_layout));

    // pass some of this logic to the presenter
    final View mainContent = view.findViewById(R.id.main_content);
    final int originalBottomPadding = withBottomBar ? mainContent.getPaddingBottom() : 0;
    if (withBottomBar) {
      view.findViewById(R.id.appbar).setVisibility(View.GONE);
    } else {
      view.findViewById(R.id.appbar).setVisibility(View.VISIBLE);
      setupToolbar(view);
    }
    mainContent.setPadding(0, 0, 0, originalBottomPadding);
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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

  private void setupToolbar(View view) {
    Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    toolbar.setLogo(R.drawable.logo_toolbar);
    
    toolbar.setTitle(getString(R.string.my_account));
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
  }

  public LoginSignUpFragment registerBottomSheetStateListener(
      BottomSheetStateListener bottomSheetStateListener) {
    this.bottomSheetStateListener = bottomSheetStateListener;
    return this;
  }

  public interface BottomSheetStateListener {
    void expanded();

    void hidden();
  }
}
