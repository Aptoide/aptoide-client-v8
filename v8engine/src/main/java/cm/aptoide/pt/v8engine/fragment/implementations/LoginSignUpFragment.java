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
import cm.aptoide.pt.v8engine.view.LoginSignUpView;

/**
 * This fragment has too much code equal to {@link LoginSignUpCredentialsFragment} due to Google /
 * Facebook
 * login functionality. Further code refactoring is needed to migrate external source login into
 * their own fragment and include the fragment inside the necessary login / sign up views.
 */
public class LoginSignUpFragment extends FragmentView implements LoginSignUpView {

  private static final String BOTTOM_SHEET_WITH_BOTTOM_BAR = "bottom_sheet_expanded";
  private static final String DISMISS_TO_NAVIGATE_TO_MAIN_VIEW = "dismiss_to_navigate_to_main_view";
  private static final String NAVIGATE_TO_HOME = "clean_back_stack";
  private static final String ACCOUNT_TYPE = "account_type";
  private static final String AUTH_TYPE = "auth_type";
  private static final String IS_NEW_ACCOUNT = "is_new_account";

  private BottomSheetStateListener bottomSheetStateListener;
  private LoginSignUpCredentialsFragment loginFragment;
  private BottomSheetBehavior<View> bottomSheetBehavior;
  private boolean withBottomBar;
  private boolean dismissToNavigateToMainView;
  private boolean navigateToHome;
  private String accountType;
  private String authType;
  private boolean isNewAccount;

  public static LoginSignUpFragment newInstance(boolean withBottomBar,
      boolean dismissToNavigateToMainView, boolean navigateToHome) {
    return newInstance(withBottomBar, dismissToNavigateToMainView, navigateToHome, "", "", true);
  }

  public static LoginSignUpFragment newInstance(boolean withBottomBar,
      boolean dismissToNavigateToMainView, boolean navigateToHome, String accountType,
      String authType, boolean isNewAccount) {
    Bundle args = new Bundle();
    args.putBoolean(BOTTOM_SHEET_WITH_BOTTOM_BAR, withBottomBar);
    args.putBoolean(DISMISS_TO_NAVIGATE_TO_MAIN_VIEW, dismissToNavigateToMainView);
    args.putBoolean(NAVIGATE_TO_HOME, navigateToHome);
    args.putString(ACCOUNT_TYPE, accountType);
    args.putString(AUTH_TYPE, authType);
    args.putBoolean(IS_NEW_ACCOUNT, isNewAccount);

    LoginSignUpFragment fragment = new LoginSignUpFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final Bundle args = getArguments();
    withBottomBar = args.getBoolean(BOTTOM_SHEET_WITH_BOTTOM_BAR);
    dismissToNavigateToMainView = args.getBoolean(DISMISS_TO_NAVIGATE_TO_MAIN_VIEW);
    navigateToHome = args.getBoolean(NAVIGATE_TO_HOME);
    accountType = args.getString(ACCOUNT_TYPE, "");
    authType = args.getString(AUTH_TYPE, "");
    isNewAccount = args.getBoolean(IS_NEW_ACCOUNT);
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
      setupToolbar(view, getString(R.string.my_account));
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

  protected Toolbar setupToolbar(View view, String title) {
    setHasOptionsMenu(true);
    Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    toolbar.setLogo(R.drawable.logo_toolbar);

    toolbar.setTitle(title);
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);

    return toolbar;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(getLayoutId(), container, false);

    loginFragment =
        LoginSignUpCredentialsFragment.newInstance(dismissToNavigateToMainView, navigateToHome);
    // nested fragments only work using dynamic fragment addition.
    getChildFragmentManager().beginTransaction()
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
