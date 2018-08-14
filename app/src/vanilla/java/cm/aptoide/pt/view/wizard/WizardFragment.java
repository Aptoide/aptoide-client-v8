package cm.aptoide.pt.view.wizard;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.LoginBottomSheet;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.NotBottomNavigationView;
import cm.aptoide.pt.view.custom.AptoideViewPager;
import cm.aptoide.pt.view.fragment.UIComponentFragment;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
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
public class WizardFragment extends UIComponentFragment
    implements WizardView, NotBottomNavigationView {

  public static final int LAYOUT = R.layout.fragment_wizard;
  private static final String PAGE_INDEX = "page_index";
  AptoideViewPager.SimpleOnPageChangeListener pageChangeListener;
  @Inject WizardPresenter wizardPresenter;
  private WizardPagerAdapter viewPagerAdapter;
  private AptoideViewPager viewPager;
  private RadioGroup radioGroup;
  private View skipText;
  private List<RadioButton> wizardButtons;
  private View skipOrNextLayout;
  private LoginBottomSheet loginBottomSheet;
  private boolean isInPortraitMode;
  private int currentPosition;
  private Runnable registerViewpagerCurrentItem;
  private View animatedColorView;
  private Integer[] transitionColors;

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

  @Override public void onDestroy() {
    if (viewPager != null) {
      viewPager.removeOnPageChangeListener(null);
      viewPager = null;
    }
    super.onDestroy();
  }

  @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    loadExtras(savedInstanceState);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(PAGE_INDEX, viewPager.getCurrentItem());
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    transitionColors = new Integer[] {
        getContext().getResources().getColor(R.color.wizard_color_1_blue),
        getContext().getResources().getColor(R.color.wizard_color_2_green),
        getContext().getResources().getColor(R.color.wizard_color_3_orange)
    };
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    attachPresenter(wizardPresenter);
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
    pageChangeListener = new AptoideViewPager.SimpleOnPageChangeListener() {

      @Override public void onPageSelected(int position) {
        if (position == 0) {
          navigationTracker.registerScreen(
              ScreenTagHistory.Builder.build(WizardPageOneFragment.class.getSimpleName(), "0"));
        }
      }
    };
    viewPager.addOnPageChangeListener(wizardPresenter);
    viewPager.addOnPageChangeListener(pageChangeListener);
    registerViewpagerCurrentItem =
        () -> pageChangeListener.onPageSelected(viewPager.getCurrentItem());
    viewPager.post(registerViewpagerCurrentItem);
  }

  @Override public void onDestroyView() {
    viewPager.removeOnPageChangeListener(pageChangeListener);
    viewPager.removeCallbacks(registerViewpagerCurrentItem);
    skipOrNextLayout = null;
    wizardButtons = null;
    radioGroup = null;
    skipText = null;
    viewPager.setAdapter(null);
    viewPager = null;
    animatedColorView = null;
    super.onDestroyView();
  }

  @Override public Completable createWizardAdapter(Account account) {
    return Completable.fromAction(() -> {
      viewPagerAdapter = new WizardPagerAdapter(getChildFragmentManager(), account);
      createRadioButtons();
      viewPager.setAdapter(viewPagerAdapter);
      viewPager.setCurrentItem(currentPosition);
      handleSelectedPage(currentPosition);
    });
  }

  @Override public Observable<Void> skipWizardClick() {
    return RxView.clicks(skipText);
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

  @Override
  public void handleColorTransitions(int position, float positionOffset, int positionOffsetPixels) {
    if (position < (viewPagerAdapter.getCount() - 1) && position < (transitionColors.length - 1)) {
      ArgbEvaluator argbEvaluator = new ArgbEvaluator();
      int argbEvaluation =
          (Integer) argbEvaluator.evaluate(positionOffset, transitionColors[position],
              transitionColors[position + 1]);
      animatedColorView.setBackgroundColor(argbEvaluation);
    } else {
      if (viewPagerAdapter.getCount() == 2) {
        animatedColorView.setBackgroundColor(transitionColors[transitionColors.length - 2]);
      } else {
        animatedColorView.setBackgroundColor(transitionColors[transitionColors.length - 1]);
      }
    }
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
    return LAYOUT;
  }

  @Override public void bindViews(@Nullable View view) {
    viewPager = (AptoideViewPager) view.findViewById(R.id.view_pager);
    skipOrNextLayout = view.findViewById(R.id.skip_next_layout);
    radioGroup = (RadioGroup) view.findViewById(R.id.view_pager_radio_group);
    skipText = view.findViewById(R.id.skip_text);
    isInPortraitMode = getActivity().getResources()
        .getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    animatedColorView = view.findViewById(R.id.animated_color_view);
  }
}
