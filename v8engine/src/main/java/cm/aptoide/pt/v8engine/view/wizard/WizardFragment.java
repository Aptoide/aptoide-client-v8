package cm.aptoide.pt.v8engine.view.wizard;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.account.LoginBottomSheet;
import cm.aptoide.pt.v8engine.view.fragment.UIComponentFragment;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.ArrayList;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jandrade on 18-07-2016.
 * <p/>
 * This Fragment inflates the Wizard layout and uses the ViewPagerAdapterWizard to inflate each
 * Wizard Page.
 * It also manages swapping pages and UI changes (Indicator + skip/next arrow)
 */
public class WizardFragment extends UIComponentFragment implements WizardView {

  private static final String PAGE_INDEX = "page_index";

  private WizardPagerAdapter viewPagerAdapter;
  private ViewPager viewPager;
  private RadioGroup radioGroup;
  private View skipText;
  private View nextIcon;
  private List<RadioButton> wizardButtons;
  private View skipOrNextLayout;
  private LoginBottomSheet loginBottomSheet;

  private boolean isInPortraitMode;
  private int currentPosition;

  public static WizardFragment newInstance() {
    return new WizardFragment();
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

    currentPosition = 0;
    // restore state
    if (args != null) {
      currentPosition = args.getInt(PAGE_INDEX, 0);
    }
  }

  @Override public void setupViews() {
    loginBottomSheet.state()
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(state -> {
          if (isInPortraitMode && LoginBottomSheet.State.EXPANDED.equals(state)) {
            skipOrNextLayout.setVisibility(View.GONE);
          } else if (LoginBottomSheet.State.COLLAPSED.equals(state)) {
            skipOrNextLayout.setVisibility(View.VISIBLE);
          }
        });

    final AptoideAccountManager accountManager =
        ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    WizardPresenter presenter =
        new WizardPresenter(this, accountManager, CrashReport.getInstance());
    attachPresenter(presenter, null);
    viewPager.addOnPageChangeListener(presenter);
  }

  @Override public void onDestroy() {
    if (viewPager != null) {
      viewPager.removeOnPageChangeListener(null);
      viewPager = null;
    }
    super.onDestroy();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(PAGE_INDEX, viewPager.getCurrentItem());
  }

  @Override public void onDestroyView() {
    skipOrNextLayout = null;
    wizardButtons = null;
    radioGroup = null;
    skipText = null;
    nextIcon = null;
    viewPager.setAdapter(null);
    viewPager = null;
    super.onDestroyView();
  }

  @Override public Completable createWizardAdapter(Account account) {
    return Completable.fromAction(() -> {
      viewPagerAdapter = new WizardPagerAdapter(getChildFragmentManager(), account);
      viewPager.setAdapter(viewPagerAdapter);

      createRadioButtons();
      viewPager.setCurrentItem(currentPosition);
      handleSelectedPage(currentPosition);
    });
  }

  @Override public Observable<Void> goToNextPageClick() {
    return RxView.clicks(nextIcon);
  }

  @Override public Observable<Void> skipWizardClick() {
    return RxView.clicks(skipText);
  }

  @Override public void goToNextPage() {
    if (viewPager.getCurrentItem() < viewPagerAdapter.getCount() - 1) {
      viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }
  }

  @Override public void skipWizard() {
    final FragmentActivity activity = getActivity();
    activity.onBackPressed();
  }

  /**
   * This method will be invoked when a new page becomes selected. Animation is not
   * necessarily complete.
   *
   * @param selectedPage Position index of the new selected page.
   */
  @Override public void handleSelectedPage(int selectedPage) {
    if (wizardButtons == null || selectedPage >= wizardButtons.size()) {
      return;
    }

    RadioButton radioButton = wizardButtons.get(selectedPage);
    if (radioButton != null) {
      radioButton.setChecked(true);
    }
  }

  @Override public int getWizardButtonsCount() {
    return wizardButtons.size();
  }

  /**
   * Show the arrow in all pages except the last.
   */
  @Override public void showArrow() {
    skipText.setVisibility(View.GONE);
    nextIcon.setVisibility(View.VISIBLE);
  }

  /**
   * On the last page we show skip button instead of the arrow.
   */
  @Override public void showSkipButton() {
    skipText.setVisibility(View.VISIBLE);
    nextIcon.setVisibility(View.GONE);
  }

  private void createRadioButtons() {
    // set button dimension
    int buttonSize = AptoideUtils.ScreenU.getPixelsForDip(10, getResources());
    ViewGroup.LayoutParams buttonLayoutParams = new RadioGroup.LayoutParams(buttonSize, buttonSize);

    // set button margin
    int buttonMargin = AptoideUtils.ScreenU.getPixelsForDip(2, getResources());
    ViewGroup.MarginLayoutParams marginLayoutParams =
        (ViewGroup.MarginLayoutParams) buttonLayoutParams;
    marginLayoutParams.setMargins(buttonMargin, buttonMargin, buttonMargin, buttonMargin);

    final int pages = viewPagerAdapter.getCount();
    wizardButtons = new ArrayList<>(pages);
    Context context = getContext();
    for (int i = 0; i < pages; i++) {
      RadioButton radioButton = new RadioButton(context);
      radioButton.setLayoutParams(buttonLayoutParams);
      radioButton.setButtonDrawable(android.R.color.transparent);
      radioButton.setBackgroundResource(R.drawable.wizard_custom_indicator);
      radioButton.setClickable(false);
      radioGroup.addView(radioButton);
      wizardButtons.add(radioButton);
    }
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_wizard;
  }

  @Override public void bindViews(@Nullable View view) {
    viewPager = (ViewPager) view.findViewById(R.id.view_pager);
    skipOrNextLayout = view.findViewById(R.id.skip_next_layout);
    radioGroup = (RadioGroup) view.findViewById(R.id.view_pager_radio_group);
    skipText = view.findViewById(R.id.skip_text);
    nextIcon = view.findViewById(R.id.next_icon);
    isInPortraitMode = getActivity().getResources()
        .getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
  }
}
