package com.mopub.nativeads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mopub.common.Preconditions;
import java.util.WeakHashMap;

import static android.view.View.VISIBLE;

/**
 * Created by vineet.srivastava on 5/16/17.
 */

public class InMobiNativeAdRenderer
    implements MoPubAdRenderer<InMobiNativeCustomEvent.InMobiNativeAd> {

  /**
   * Key to set and get star primary ad view as an extra in the view binder.
   */
  public static final String VIEW_BINDER_KEY_PRIMARY_AD_VIEW_LAYOUT = "primary_ad_view_layout";
  /**
   * A view binder containing the layout resource and views to be rendered by the renderer.
   */
  private final ViewBinder mViewBinder;
  /**
   * A weak hash map used to keep track of view holder so that the views can be properly recycled.
   */
  private final WeakHashMap<View, InMobiNativeViewHolder> mViewHolderMap;
  private View mAdView;

  public InMobiNativeAdRenderer(ViewBinder mViewBinder) {
    this.mViewBinder = mViewBinder;
    this.mViewHolderMap = new WeakHashMap<>();
  }

  private static void setViewVisibility(final InMobiNativeViewHolder inMobiNativeViewHolder,
      final int visibility) {
    if (inMobiNativeViewHolder.getMainView() != null) {
      inMobiNativeViewHolder.getMainView()
          .setVisibility(visibility);
    }
  }

  /**
   * Creates a new view to be used as an ad.
   * <p/>
   * This method is called when you call {@link MoPubStreamAdPlacer#getAdView}
   * and the convertView is null. You must return a valid view.
   *
   * @param context The context. Useful for creating a view. This is recommended to be an
   * Activity. If you have custom themes defined in your Activity, not passing
   * in that Activity will result in the default Application theme being used
   * when creating the ad view.
   * @param parent The parent that the view will eventually be attached to. You might use the
   * parent to determine layout parameters, but should return the view without
   * attaching it to the parent.
   *
   * @return A new ad view.
   */
  @NonNull @Override public View createAdView(@NonNull Context context,
      @Nullable ViewGroup parent) {
    mAdView = LayoutInflater.from(context)
        .inflate(mViewBinder.layoutId, parent, false);
    final View mainImageView = mAdView.findViewById(mViewBinder.mainImageId);
    if (mainImageView == null) {
      return mAdView;
    }

    final ViewGroup.LayoutParams mainImageViewLayoutParams = mainImageView.getLayoutParams();
    final RelativeLayout.LayoutParams primaryViewLayoutParams =
        new RelativeLayout.LayoutParams(mainImageViewLayoutParams.width,
            mainImageViewLayoutParams.height);

    if (mainImageViewLayoutParams instanceof ViewGroup.MarginLayoutParams) {
      final ViewGroup.MarginLayoutParams marginParams =
          (ViewGroup.MarginLayoutParams) mainImageViewLayoutParams;
      primaryViewLayoutParams.setMargins(marginParams.leftMargin, marginParams.topMargin,
          marginParams.rightMargin, marginParams.bottomMargin);
      primaryViewLayoutParams.addRule(RelativeLayout.BELOW, mViewBinder.mainImageId);
    }

    if (mainImageViewLayoutParams instanceof RelativeLayout.LayoutParams) {
      final RelativeLayout.LayoutParams mainImageViewRelativeLayoutParams =
          (RelativeLayout.LayoutParams) mainImageViewLayoutParams;
      final int[] rules = mainImageViewRelativeLayoutParams.getRules();
      for (int i = 0; i < rules.length; i++) {
        primaryViewLayoutParams.addRule(i, rules[i]);
      }
    }
    mainImageView.setVisibility(View.INVISIBLE);

    if (mViewBinder.extras.get(VIEW_BINDER_KEY_PRIMARY_AD_VIEW_LAYOUT) != null) {
      final ViewGroup primaryAdLayout =
          mAdView.findViewById(mViewBinder.extras.get(VIEW_BINDER_KEY_PRIMARY_AD_VIEW_LAYOUT));
      if (primaryAdLayout != null && primaryAdLayout instanceof RelativeLayout) {
        primaryAdLayout.setLayoutParams(primaryViewLayoutParams);
      }
    }
    return mAdView;
  }

  /**
   * Renders a view created by {@link #createAdView} by filling it with ad data.
   *
   * @param view The ad {@link View}
   * @param inMobiNativeAd The ad data that should be bound to the view.
   */
  @Override public void renderAdView(@NonNull View view,
      @NonNull InMobiNativeCustomEvent.InMobiNativeAd inMobiNativeAd) {
    InMobiNativeViewHolder inMobiNativeViewHolder = mViewHolderMap.get(view);
    if (inMobiNativeViewHolder == null) {
      inMobiNativeViewHolder = inMobiNativeViewHolder.fromViewBinder(view, mViewBinder);
      mViewHolderMap.put(view, inMobiNativeViewHolder);
    }
    update(inMobiNativeViewHolder, inMobiNativeAd);

    setViewVisibility(inMobiNativeViewHolder, VISIBLE);
  }

  /**
   * Determines if this renderer supports the type of native ad passed in.
   *
   * @param nativeAd The native ad to render.
   *
   * @return True if the renderer can render the native ad and false if it cannot.
   */
  @Override public boolean supports(@NonNull BaseNativeAd nativeAd) {
    Preconditions.checkNotNull(nativeAd);
    return nativeAd instanceof InMobiNativeCustomEvent.InMobiNativeAd;
  }

  private void update(final InMobiNativeViewHolder inMobiNativeViewHolder,
      final InMobiNativeCustomEvent.InMobiNativeAd inMobiNativeAd) {
    final ImageView mainImageView = inMobiNativeViewHolder.getMainImageView();
    NativeRendererHelper.addTextView(inMobiNativeViewHolder.getTitleView(),
        inMobiNativeAd.getAdTitle());
    NativeRendererHelper.addTextView(inMobiNativeViewHolder.getTextView(),
        inMobiNativeAd.getAdDescription());
    NativeRendererHelper.addCtaButton(inMobiNativeViewHolder.getCallToActionView(),
        inMobiNativeViewHolder.getMainView(), inMobiNativeAd.getAdCtaText());
    NativeImageHelper.loadImageView(inMobiNativeAd.getAdIconUrl(),
        inMobiNativeViewHolder.getIconImageView());

    mAdView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        inMobiNativeAd.onCtaClick();
      }
    });

    final ViewGroup primaryAdViewLayout = inMobiNativeViewHolder.getPrimaryAdViewLayout();
    if (primaryAdViewLayout != null && mainImageView != null) {
      //removed child views and setting native ad primary View to avoid mismatch between native ad and primary
      // view which is caused, because android recycles the view
      primaryAdViewLayout.removeAllViews();
      primaryAdViewLayout.getViewTreeObserver()
          .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
              primaryAdViewLayout.getViewTreeObserver()
                  .removeOnGlobalLayoutListener(this);
              View primaryAdView = inMobiNativeAd.getPrimaryAdView(
                  (ViewGroup) inMobiNativeViewHolder.getPrimaryAdViewLayout());
              if (primaryAdView != null) {
                primaryAdViewLayout.addView(primaryAdView);
              }
            }
          });

      primaryAdViewLayout.setVisibility(VISIBLE);

      mainImageView.setLayoutParams(primaryAdViewLayout.getLayoutParams());
    }
  }

  static class InMobiNativeViewHolder {
    private final StaticNativeViewHolder mStaticNativeViewHolder;
    private final ViewGroup mPrimaryAdViewLayout;
    private final boolean isMainImageViewInRelativeView;

    // Use fromViewBinder instead of a constructor
    private InMobiNativeViewHolder(final StaticNativeViewHolder staticNativeViewHolder,
        final ViewGroup primaryAdViewLayout, final boolean mainImageViewInRelativeView) {
      mStaticNativeViewHolder = staticNativeViewHolder;
      mPrimaryAdViewLayout = primaryAdViewLayout;
      isMainImageViewInRelativeView = mainImageViewInRelativeView;
    }

    static InMobiNativeViewHolder fromViewBinder(final View view, final ViewBinder viewBinder) {
      StaticNativeViewHolder staticNativeViewHolder =
          StaticNativeViewHolder.fromViewBinder(view, viewBinder);
      final View mainImageView = staticNativeViewHolder.mainImageView;
      boolean mainImageViewInRelativeView = false;
      final ViewGroup primaryAdViewLayout;
      if (mainImageView != null) {
        final ViewGroup mainImageParent = (ViewGroup) mainImageView.getParent();
        if (mainImageParent instanceof RelativeLayout) {
          mainImageViewInRelativeView = true;
        }
      }
      if (viewBinder.extras.get(VIEW_BINDER_KEY_PRIMARY_AD_VIEW_LAYOUT) != null) {
        primaryAdViewLayout = (ViewGroup) view.findViewById(
            viewBinder.extras.get(VIEW_BINDER_KEY_PRIMARY_AD_VIEW_LAYOUT));
      } else {
        primaryAdViewLayout = null;
      }
      return new InMobiNativeViewHolder(staticNativeViewHolder, primaryAdViewLayout,
          mainImageViewInRelativeView);
    }

    public View getMainView() {
      return mStaticNativeViewHolder.mainView;
    }

    public TextView getTitleView() {
      return mStaticNativeViewHolder.titleView;
    }

    public TextView getTextView() {
      return mStaticNativeViewHolder.textView;
    }

    public TextView getCallToActionView() {
      return mStaticNativeViewHolder.callToActionView;
    }

    public ImageView getMainImageView() {
      return mStaticNativeViewHolder.mainImageView;
    }

    public ImageView getIconImageView() {
      return mStaticNativeViewHolder.iconImageView;
    }

    public ViewGroup getPrimaryAdViewLayout() {
      return mPrimaryAdViewLayout;
    }

    public boolean isMainImageViewInRelativeView() {
      return isMainImageViewInRelativeView;
    }
  }
}
