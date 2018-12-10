package cm.aptoide.pt.promotions;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.util.AppBarStateChangeListener;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import java.util.ArrayList;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

import static cm.aptoide.pt.utils.GenericDialogs.EResponse.YES;

public class PromotionsFragment extends NavigationTrackFragment implements PromotionsView {

  @Inject PromotionsPresenter promotionsPresenter;
  private RecyclerView promotionsList;
  private PromotionsAdapter promotionsAdapter;
  private PublishSubject<PromotionAppClick> promotionAppClick;
  private TextView promotionFirstMessage;
  private Window window;
  private Toolbar toolbar;
  private Drawable backArrow;
  private AppBarLayout appBarLayout;
  private CollapsingToolbarLayout collapsingToolbarLayout;
  private TextView toolbarTitle;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    window = getActivity().getWindow();
    setHasOptionsMenu(true);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    promotionsList = view.findViewById(R.id.fragment_promotions_promotions_list);
    promotionAppClick = PublishSubject.create();
    promotionsAdapter = new PromotionsAdapter(new ArrayList<>(),
        new PromotionsViewHolderFactory(promotionAppClick));

    promotionFirstMessage = view.findViewById(R.id.promotions_message_1);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.setStatusBarColor(getResources().getColor(R.color.black_87_alpha));
    }
    toolbarTitle = view.findViewById(R.id.toolbar_title);
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    toolbar.setTitle("");
    AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
    appCompatActivity.setSupportActionBar(toolbar);
    ActionBar actionBar = appCompatActivity.getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
    backArrow = toolbar.getNavigationIcon();
    appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar_layout);

    collapsingToolbarLayout =
        ((CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar_layout));
    collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.tw__transparent));
    collapsingToolbarLayout.setCollapsedTitleTextColor(
        getResources().getColor(R.color.tw__transparent));

    appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {

      private void configureAppBarLayout(Drawable drawable, int toolbarColor, boolean isCollapsed) {
        toolbar.setBackgroundDrawable(drawable);

        toolbarTitle.setTextColor(toolbarColor);
        toolbarTitle.setText(getString(R.string.holidayspromotion_title_holidays));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          handleStatusBar(isCollapsed);
        }
        if (backArrow != null) {
          backArrow.setColorFilter(toolbarColor, PorterDuff.Mode.SRC_IN);
        }
      }

      @Override public void onStateChanged(AppBarLayout appBarLayout, State state) {
        Resources resources = getResources();
        switch (state) {
          case EXPANDED:
            break;
          default:
          case IDLE:
          case MOVING:
            toolbarTitle.setVisibility(View.GONE);
            configureAppBarLayout(
                resources.getDrawable(R.drawable.editorial_up_bottom_black_gradient),
                resources.getColor(R.color.tw__solid_white), false);
            break;
          case COLLAPSED:
            toolbarTitle.setVisibility(View.VISIBLE);

            configureAppBarLayout(resources.getDrawable(R.drawable.tw__transparent),
                resources.getColor(R.color.black), true);
            break;
        }
      }
    });

    setupRecyclerView();
    attachPresenter(promotionsPresenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName(), "");
  }

  private void handleStatusBar(boolean collapseState) {
    if (collapseState) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
          && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        window.setStatusBarColor(getResources().getColor(R.color.grey_medium));
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(getResources().getColor(R.color.white));
      }
    } else {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
          && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        window.setStatusBarColor(getResources().getColor(R.color.black_87_alpha));
        window.getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        window.setStatusBarColor(getResources().getColor(R.color.black_87_alpha));
      }
    }
  }

  private void setupRecyclerView() {
    promotionsList.setAdapter(promotionsAdapter);
    promotionsList.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    RecyclerView.ItemAnimator animator = promotionsList.getItemAnimator();
    if (animator instanceof SimpleItemAnimator) {
      ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_promotions, container, false);
  }

  @Override public void showPromotionApp(PromotionViewApp promotionViewApp) {
    promotionsAdapter.setPromotionApp(promotionViewApp);
  }

  @Override public Observable<PromotionViewApp> installButtonClick() {
    return promotionAppClick.filter(
        promotionAppClick -> promotionAppClick.getClickType() == PromotionAppClick.ClickType.UPDATE
            || promotionAppClick.getClickType() == PromotionAppClick.ClickType.INSTALL_APP
            || promotionAppClick.getClickType() == PromotionAppClick.ClickType.DOWNLOAD)
        .map(promotionAppClick -> promotionAppClick.getApp());
  }

  @Override public Observable<Boolean> showRootInstallWarningPopup() {
    return GenericDialogs.createGenericYesNoCancelMessage(this.getContext(), null,
        getResources().getString(R.string.root_access_dialog))
        .map(response -> (response.equals(YES)));
  }

  @Override public Observable<PromotionViewApp> pauseDownload() {
    return promotionAppClick.filter(promotionAppClick -> promotionAppClick.getClickType()
        == PromotionAppClick.ClickType.PAUSE_DOWNLOAD)
        .map(promotionAppClick -> promotionAppClick.getApp());
  }

  @Override public Observable<PromotionViewApp> cancelDownload() {
    return promotionAppClick.filter(promotionAppClick -> promotionAppClick.getClickType()
        == PromotionAppClick.ClickType.CANCEL_DOWNLOAD)
        .map(promotionAppClick -> promotionAppClick.getApp());
  }

  @Override public Observable<PromotionViewApp> resumeDownload() {
    return promotionAppClick.filter(promotionAppClick -> promotionAppClick.getClickType()
        == PromotionAppClick.ClickType.RESUME_DOWNLOAD)
        .map(promotionAppClick -> promotionAppClick.getApp());
  }

  @Override public void showAppCoinsAmmount(int totalAppcValue) {
    promotionFirstMessage.setText(
        getString(R.string.holidayspromotion_message_1, String.valueOf(totalAppcValue)));
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      window.getDecorView()
          .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
    toolbarTitle = null;
    window = null;
    toolbar = null;
    promotionsList = null;
    promotionsAdapter = null;
    collapsingToolbarLayout = null;
    appBarLayout = null;
    backArrow = null;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    promotionAppClick = null;
  }
}
