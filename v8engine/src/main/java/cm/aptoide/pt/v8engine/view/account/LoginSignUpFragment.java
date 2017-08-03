package cm.aptoide.pt.v8engine.view.account;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.Toolbar;
import android.view.View;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.presenter.LoginSignUpView;
import cm.aptoide.pt.v8engine.view.fragment.BaseToolbarFragment;

public class LoginSignUpFragment extends BaseToolbarFragment implements LoginSignUpView {

  private static final String BOTTOM_SHEET_WITH_BOTTOM_BAR = "bottom_sheet_expanded";
  private static final String DISMISS_TO_NAVIGATE_TO_MAIN_VIEW = "dismiss_to_navigate_to_main_view";
  private static final String NAVIGATE_TO_HOME = "clean_back_stack";
  private static final String ACCOUNT_TYPE = "account_type";
  private static final String AUTH_TYPE = "auth_type";
  private static final String IS_NEW_ACCOUNT = "is_new_account";

  private BottomSheetBehavior<View> bottomSheetBehavior;
  private boolean withBottomBar;
  private LoginBottomSheet loginBottomSheet;
  private View mainContent;
  private int originalBottomPadding;
  private LoginSignUpPresenter presenter;
  private String toolbarTitle;
  private boolean dismissToNavigateToMainView;
  private boolean navigateToHome;

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

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof LoginBottomSheet) {
      loginBottomSheet = (LoginBottomSheet) context;
    } else {
      throw new IllegalStateException(
          "Context should implement " + LoginBottomSheet.class.getSimpleName());
    }
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    withBottomBar = args.getBoolean(BOTTOM_SHEET_WITH_BOTTOM_BAR);
    dismissToNavigateToMainView = args.getBoolean(DISMISS_TO_NAVIGATE_TO_MAIN_VIEW);
    navigateToHome = args.getBoolean(NAVIGATE_TO_HOME);
  }

  @Override public void onDestroyView() {
    if (bottomSheetBehavior != null) {
      bottomSheetBehavior.setBottomSheetCallback(null);
      bottomSheetBehavior = null;
    }
    if (presenter != null) {
      unregisterClickHandler(presenter);
    }
    super.onDestroyView();
  }

  @Override public void setupViews() {
    super.setupViews();
    presenter = new LoginSignUpPresenter(this, getFragmentChildNavigator(R.id.login_signup_layout),
        dismissToNavigateToMainView, navigateToHome);
    attachPresenter(presenter, null);
    registerClickHandler(presenter);
    bottomSheetBehavior.setBottomSheetCallback(presenter);
  }

  @Override protected boolean hasToolbar() {
    return toolbarTitle != null;
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return hasToolbar();
  }

  public void setupToolbarDetails(Toolbar toolbar) {
    setHasOptionsMenu(true);
    toolbar.setLogo(R.drawable.logo_toolbar);
    toolbar.setTitle(toolbarTitle);
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);

    try {
      bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.login_signup_layout));
    } catch (IllegalArgumentException ex) {
      // this happens because in landscape the R.id.login_signup_layout is not
      // a child of CoordinatorLayout
    }

    if (bottomSheetBehavior != null) {
      mainContent = view.findViewById(R.id.main_content);
      originalBottomPadding = withBottomBar ? mainContent.getPaddingBottom() : 0;
      if (withBottomBar) {
        view.findViewById(R.id.appbar)
            .setVisibility(View.GONE);
      } else {
        view.findViewById(R.id.appbar)
            .setVisibility(View.VISIBLE);
        toolbarTitle = getString(R.string.my_account_title_my_account);
      }
      mainContent.setPadding(0, 0, 0, originalBottomPadding);
      bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
  }

  @Override public void collapseBottomSheet() {
    loginBottomSheet.collapse();
    mainContent.setPadding(0, 0, 0, originalBottomPadding);
  }

  @Override public void expandBottomSheet() {
    loginBottomSheet.expand();
    mainContent.setPadding(0, 0, 0, 0);
  }

  @Override public boolean bottomSheetIsExpanded() {
    return bottomSheetBehavior != null
        && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED;
  }

  @Override public void setBottomSheetState(int stateCollapsed) {
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_login_sign_up;
  }
}
