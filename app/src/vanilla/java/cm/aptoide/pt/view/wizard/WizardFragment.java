package cm.aptoide.pt.view.wizard;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.LoginBottomSheet;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.NotBottomNavigationView;
import cm.aptoide.pt.view.custom.AptoideViewPager;
import cm.aptoide.pt.view.fragment.UIComponentFragment;
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
public class WizardFragment extends UIComponentFragment
    implements WizardView, NotBottomNavigationView {

  public static final int LAYOUT = R.layout.fragment_wizard;
  private static final String PAGE_INDEX = "page_index";
  AptoideViewPager.SimpleOnPageChangeListener pageChangeListener;
  ValueAnimator colorAnimation;
  private WizardPagerAdapter viewPagerAdapter;
  private AptoideViewPager viewPager;
  private RadioGroup radioGroup;
  private View skipText;
  private List<RadioButton> wizardButtons;
  private View skipOrNextLayout;
  private LoginBottomSheet loginBottomSheet;
  private View animatedColorView;
  private boolean isInPortraitMode;
  private int currentPosition;
  private Runnable registerViewpagerCurrentItem;
  private boolean startTransition;

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

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    animatedColorView = view.findViewById(R.id.animated_color_view);
    startTransition = false;
    pageChangeListener = new AptoideViewPager.SimpleOnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override public void onPageSelected(int position) {
        if (position == 0) {
          navigationTracker.registerScreen(
              ScreenTagHistory.Builder.build(WizardPageOneFragment.class.getSimpleName(), "0"));
        }
      }

      @Override public void onPageScrollStateChanged(int state) {
      }
    };

    super.onViewCreated(view, savedInstanceState);
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
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    final AccountAnalytics accountAnalytics =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountAnalytics();
    WizardPresenter presenter =
        new WizardPresenter(this, accountManager, CrashReport.getInstance(), accountAnalytics);
    attachPresenter(presenter);
    viewPager.addOnPageChangeListener(presenter);
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

  public void handleColorTransitions(int position, float positionOffset, int positionOffsetPixels) {
    Integer[] endColors = {
        getContext().getResources().getColor(R.color.wizard_page_1_end_color),
        getContext().getResources().getColor(R.color.wizard_page_2_end_color),
        getContext().getResources().getColor(R.color.default_orange_gradient_end)
    };
    Integer[] startColors = {
        getContext().getResources().getColor(R.color.wizard_page_1_start_color),
        getContext().getResources().getColor(R.color.wizard_page_2_start_color),
        getContext().getResources().getColor(R.color.default_orange_gradient_start)
    };

    Drawable[] gradients = {
        getContext().getResources().getDrawable(R.drawable.wizard_page_1_gradient),
        getContext().getResources().getDrawable(R.drawable.light_green_gradient),
        getContext().getResources().getDrawable(R.drawable.aptoide_gradient)
    };

    ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    if (position < (viewPagerAdapter.getCount() - 1)
        && position < (endColors.length) - 1
        && positionOffset != 0) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && startTransition) {
        Drawable[] list = { gradients[position], new ColorDrawable(startColors[position]) };
        TransitionDrawable d = new TransitionDrawable(list);
        animatedColorView.setBackground(d);
        d.startTransition(300);
        startTransition = false;
      }
      animatedColorView.setBackgroundColor(
          (Integer) argbEvaluator.evaluate(positionOffset, startColors[position],
              startColors[position + 1]));
    } else {
      if (position < endColors.length) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
          Drawable[] list = {
              new ColorDrawable(endColors[position]), gradients[position]
          };

          TransitionDrawable d = new TransitionDrawable(list);
          animatedColorView.setBackground(d);
          d.startTransition(300);
          startTransition = true;
        }
      }
    }
  }

  //@Override public void handleColorTransitions(int position, float positionOffset, int positionOffsetPixels) {
  //  Integer[] endColors = {
  //      getContext().getResources().getColor(R.color.wizard_page_1_end_color),
  //      getContext().getResources().getColor(R.color.wizard_page_2_end_color),
  //      getContext().getResources().getColor(R.color.default_orange_gradient_end)
  //  };
  //  Integer[] startColors = {
  //      getContext().getResources().getColor(R.color.wizard_page_1_start_color),
  //      getContext().getResources().getColor(R.color.wizard_page_2_start_color),
  //      getContext().getResources().getColor(R.color.default_orange_gradient_start)
  //  };
  //
  //  Logger.i("OOOOOOFFFFFFSSEEEEEET!!!!!: ", Float.toString(positionOffset));
  //
  //  if (position < (viewPagerAdapter.getCount() - 1)
  //      && position < (endColors.length) - 1
  //      && positionOffset != 0) {
  //    int[] colors = {
  //        gradientMap(startColors[position], endColors[position], positionOffset),
  //        gradientMap(startColors[position + 1], endColors[position + 1], 1 - positionOffset)
  //    };
  //    GradientDrawable gradient =
  //        new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
  //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
  //      animatedColorView.setBackground(gradient);
  //    }
  //  } else if (position < startColors.length) {
  //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
  //      switch (position) {
  //        case 0:
  //          animatedColorView.setBackground(getContext().getResources()
  //              .getDrawable(R.drawable.wizard_page_1_gradient));
  //          break;
  //        case 1:
  //          animatedColorView.setBackground(getContext().getResources()
  //              .getDrawable(R.drawable.light_green_gradient));
  //          break;
  //        case 2:
  //          animatedColorView.setBackground(getContext().getResources()
  //              .getDrawable(R.drawable.aptoide_gradient));
  //          break;
  //        default:
  //          break;
  //      }
  //    }
  //  }
  //}

  @Override public int getWizardButtonsCount() {
    return wizardButtons.size();
  }

  private int gradientMap(int startColor, int endColor, float offset) {

    int startRed = Color.red(startColor);
    int startGreen = Color.green(startColor);
    int startBlue = Color.blue(startColor);
    int endRed = Color.red(endColor);
    int endGreen = Color.green(endColor);
    int endBlue = Color.blue(endColor);

    if (offset < 0.1 || offset > 0.95) {
      int finalRed = Math.round((1 - offset) * startRed + offset * endRed);
      int finalGreen = Math.round((1 - offset) * startGreen + offset * endGreen);
      int finalBlue = Math.round((1 - offset) * startBlue + offset * endBlue);
      return Color.rgb(finalRed, finalGreen, finalBlue);
    } else if (offset > 0 && offset <= 0.099999999) {
      return startColor;
    } else {
      return endColor;
    }
  }

  //TODO: DELETE THIS!!!!

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
  }
}
