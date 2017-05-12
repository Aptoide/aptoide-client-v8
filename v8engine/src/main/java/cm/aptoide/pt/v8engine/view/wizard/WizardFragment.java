package cm.aptoide.pt.v8engine.view.wizard;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.BackButtonFragment;
import cm.aptoide.pt.v8engine.view.account.LoginBottomSheet;
import com.jakewharton.rxbinding.support.v4.view.RxViewPager;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.ArrayList;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jandrade on 18-07-2016.
 * This Fragment inflates the Wizard layout and uses the ViewPagerAdapterWizard to inflate each
 * Wizard Page.
 * It also manages swapping pages and UI changes (Indicator + skip/next arrow)
 */
// TODO: 16/2/2017 sithengineer add MVP to this view
public class WizardFragment extends BackButtonFragment {

  private CrashReport crashReport;
  private WizardPagerAdapter viewPagerAdapter;
  private ViewPager viewPager;
  private RadioGroup radioGroup;
  private View skipText;
  private View nextIcon;
  private List<RadioButton> wizardButtons;
  private View skipOrNextLayout;

  private AptoideAccountManager accountManager;
  private LoginBottomSheet loginBottomSheet;

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof LoginBottomSheet) {
      loginBottomSheet = (LoginBottomSheet) context;
    } else {
      throw new IllegalStateException(
          "Context should implement " + LoginBottomSheet.class.getSimpleName());
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_wizard, container, false);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    accountManager = ((V8Engine) getActivity().getApplicationContext()).getAccountManager();
    crashReport = CrashReport.getInstance();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewPager = (ViewPager) view.findViewById(R.id.view_pager);
    skipOrNextLayout = view.findViewById(R.id.skip_next_layout);
    radioGroup = (RadioGroup) view.findViewById(R.id.view_pager_radio_group);
    skipText = view.findViewById(R.id.skip_text);
    nextIcon = view.findViewById(R.id.next_icon);
    createViewsAndButtons(getContext());

    loginBottomSheet.state()
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(state -> {
          if (LoginBottomSheet.State.EXPANDED.equals(state)) {
            skipOrNextLayout.setVisibility(View.GONE);
          } else if (LoginBottomSheet.State.COLLAPSED.equals(state)) {
            skipOrNextLayout.setVisibility(View.VISIBLE);
          }
        }, err -> crashReport.log(err));

    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      }

      @Override public void onPageSelected(int position) {
        if (position == 2) {
          //Inside the wizards third page
          Analytics.Account.enterAccountScreen(Analytics.Account.AccountOrigins.WIZARD);
        }
      }

      @Override public void onPageScrollStateChanged(int state) {
      }
    });
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

  @Override public void onDestroy() {
    super.onDestroy();
    if (viewPager != null) {
      viewPager.removeOnPageChangeListener(null);
    }
  }

  private void createViewsAndButtons(Context context) {
    accountManager.accountStatus()
        .first()
        .toSingle()
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW).forSingle())
        .subscribe(account -> {
          viewPagerAdapter = new WizardPagerAdapter(getChildFragmentManager(), account);
          viewPager.setAdapter(viewPagerAdapter);
          viewPager.setCurrentItem(0);

          createRadioButtons(context);
          setupHandlers();
        }, err -> crashReport.log(err));
  }

  private void createRadioButtons(Context context) {
    RadioGroup.LayoutParams layoutParams =
        new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
            RadioGroup.LayoutParams.WRAP_CONTENT);

    wizardButtons = new ArrayList<>(viewPagerAdapter.getCount());
    for (int i = 0; i < viewPagerAdapter.getCount(); i++) {
      RadioButton radioButton = new RadioButton(context);
      radioButton.setBackgroundResource(R.drawable.wizard_custom_indicator);
      radioButton.setButtonDrawable(android.R.color.transparent);
      radioButton.setClickable(false);
      radioGroup.addView(radioButton, layoutParams);
      wizardButtons.add(radioButton);
    }
  }

  private void setupHandlers() {
    RxView.clicks(nextIcon)
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(__ -> handleNextPageClick(), err -> crashReport.log(err));

    RxView.clicks(skipText)
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(__ -> handleSkipClick(), err -> crashReport.log(err));

    RxViewPager.pageSelections(viewPager)
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(selectedPage -> handleSelectedPage(selectedPage), err -> crashReport.log(err));
  }

  private void handleNextPageClick() {
    // safety check. should not be needed
    if (viewPager.getCurrentItem() < viewPagerAdapter.getCount() - 1) {
      viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }
  }

  private void handleSkipClick() {
    final FragmentActivity activity = getActivity();
    activity.onBackPressed();
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
  }

  /**
   * This method will be invoked when a new page becomes selected. Animation is not
   * necessarily complete.
   *
   * @param selectedPage Position index of the new selected page.
   */
  private void handleSelectedPage(int selectedPage) {
    // mark the current page as selected in the radio group
    wizardButtons.get(selectedPage)
        .setChecked(true);
    if (selectedPage > 0 && selectedPage < wizardButtons.size() - 1) {
      // show the arrow in all pages except the last
      skipText.setVisibility(View.GONE);
      nextIcon.setVisibility(View.VISIBLE);
    } else if (selectedPage == wizardButtons.size() - 1) {
      // on last page we show skip button instead of the arrow
      skipText.setVisibility(View.VISIBLE);
      nextIcon.setVisibility(View.GONE);
    }
  }
}
