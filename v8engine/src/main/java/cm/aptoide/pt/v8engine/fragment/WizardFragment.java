package cm.aptoide.pt.v8engine.fragment;

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
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.adapters.DumbEagerFragmentPagerAdapter;
import cm.aptoide.pt.v8engine.fragment.implementations.JoinCommunityFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.WizardPageOneFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.WizardPageThreeFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.WizardPageTwoFragment;
import com.jakewharton.rxbinding.support.v4.view.RxViewPager;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.ArrayList;

/**
 * Created by jandrade on 18-07-2016.
 * This Fragment inflates the Wizard layout and uses the ViewPagerAdapterWizard to inflate each
 * Wizard Page.
 * It also manages swapping pages and UI changes (Indicator + skip/next arrow)
 */
// TODO: 16/2/2017 sithengineer add MVP to this view
public class WizardFragment extends FragmentView {

  private DumbEagerFragmentPagerAdapter viewPagerAdapter;
  private ViewPager viewPager;
  private RadioGroup radioGroup;
  private View skipText;
  private View nextIcon;

  private ArrayList<RadioButton> wizardButtons;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_wizard, container, false);
    bind(view);
    createRadioButtons();
    setupHandlers();
    return view;
  }

  private void bind(View view) {
    viewPager = (ViewPager) view.findViewById(R.id.view_pager);
    radioGroup = (RadioGroup) view.findViewById(R.id.view_pager_radio_group);
    skipText = view.findViewById(R.id.skip_text);
    nextIcon = view.findViewById(R.id.next_icon);

    viewPagerAdapter = new DumbEagerFragmentPagerAdapter(getActivity().getSupportFragmentManager());
    viewPagerAdapter.attachFragments(WizardPageOneFragment.newInstance(),
        WizardPageTwoFragment.newInstance(), WizardPageThreeFragment.newInstance(),
        JoinCommunityFragment.newInstance());

    viewPager.setAdapter(viewPagerAdapter);
    viewPager.setCurrentItem(0);
  }

  private void createRadioButtons() {
    RadioGroup.LayoutParams layoutParams =
        new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
            RadioGroup.LayoutParams.WRAP_CONTENT);

    wizardButtons = new ArrayList<>(viewPagerAdapter.getCount());
    for (int i = 0; i < viewPagerAdapter.getCount(); i++) {
      RadioButton radioButton = new RadioButton(getContext());
      radioButton.setBackgroundResource(R.drawable.wizard_custom_indicator);
      radioButton.setButtonDrawable(android.R.color.transparent);
      radioButton.setId(radioGroup.getId() + i + 1);
      radioGroup.addView(radioButton, layoutParams);
      wizardButtons.add(radioButton);
    }
  }

  private void setupHandlers() {
    RxView.clicks(nextIcon)
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(__ -> handleNextPageClick(), err -> CrashReport.getInstance().log(err));

    RxView.clicks(skipText)
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(__ -> handleSkipClick(), err -> CrashReport.getInstance().log(err));

    RxViewPager.pageSelections(viewPager)
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(selectedPage -> handleSelectedPage(selectedPage),
            err -> CrashReport.getInstance().log(err));
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
    radioGroup.check(wizardButtons.get(selectedPage).getId());
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

  public boolean onBackPressed() {
    return ((FragmentView)viewPagerAdapter.getItem(viewPager.getCurrentItem())).onBackPressed();
  }
}
